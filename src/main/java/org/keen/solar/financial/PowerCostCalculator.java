package org.keen.solar.financial;

import org.keen.solar.financial.dal.TariffRepository;
import org.keen.solar.financial.dal.PowerCostRepository;
import org.keen.solar.financial.domain.PowerCost;
import org.keen.solar.financial.domain.Tariff;
import org.keen.solar.system.domain.CurrentPower;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class PowerCostCalculator {

    private final Logger logger = LoggerFactory.getLogger(PowerCostCalculator.class);

    private static final int SECONDS_PER_MINUTE = 60;
    private static final BigDecimal WATTS_PER_KILOWATT = BigDecimal.valueOf(1000);
    private static final BigDecimal SECONDS_PER_HOUR = BigDecimal.valueOf(3600);
    /**
     * The maximum number of significant figures in the tariffs.
     */
    private static final int TARIFF_SCALE = 5;
    private static final int KILOWATT_T0_WATT_CONVERSION_SCALE = TARIFF_SCALE + 3;
    private static final int HOUR_TO_SECOND_CONVERSION_SCALE = 16;

    /**
     * Frequency at which to create a PowerCost measurement. This value must evenly
     * divide into 60 (the number of seconds in one minute), or be a multiple of 60,
     * up to 3600 (the number of seconds in one hour).
     */
    private final int collectionFrequencySeconds;
    private final TariffRepository tariffRepository;
    private final PowerCostRepository powerCostRepository;
    private final Deque<CurrentPower> uncostedPowers = new ConcurrentLinkedDeque<>();

    public PowerCostCalculator(@Value("${app.power.collection-frequency-sec}") int collectionFrequencySeconds,
                               TariffRepository tariffRepository,
                               PowerCostRepository powerCostRepository) {
        validateCollectionFrequency(collectionFrequencySeconds);

        this.collectionFrequencySeconds = collectionFrequencySeconds;
        this.tariffRepository = tariffRepository;
        this.powerCostRepository = powerCostRepository;
    }

    /**
     * Collects CurrentPower measurements as they are produced. When the collection
     * frequency is reached, calculates the cost and persists it.
     *
     * @param currentPower a new CurrentPower
     */
    @EventListener
    public void collectUncostedPowers(CurrentPower currentPower) {
        uncostedPowers.add(currentPower);

        // If we've reached a boundary, sum and persist.
        if (currentPower.getEpochTimestamp() % collectionFrequencySeconds == 0) {
            calculateCostAndPersist(currentPower);
        }
    }

    @EventListener(classes = ContextStoppedEvent.class)
    public void persistOnShutdown() {
        CurrentPower lastCurrentPower = uncostedPowers.peekLast();
        if (lastCurrentPower != null) {
            calculateCostAndPersist(lastCurrentPower);
        }
    }

    /**
     * Calculate the cost of the collected uncosted CurrentPower list and persist.
     *
     * @param currentPower the last CurrentPower to include in the calculation
     */
    private void calculateCostAndPersist(CurrentPower currentPower) {
        logger.trace("Calculating power cost for period end {}", currentPower.getEpochTimestamp());
        ZonedDateTime measurementZonedDateTime = getMeasurementZonedDateTime();
        DayOfWeek dayOfWeek = measurementZonedDateTime.getDayOfWeek();
        LocalTime localTime = measurementZonedDateTime.toLocalTime();
        // Get feed-in tariff
        Tariff effectiveFeedInTariff = tariffRepository
                .findEffectiveFeedInTariff(dayOfWeek, localTime, currentPower.getEpochTimestamp());
        if (effectiveFeedInTariff == null) {
            logger.warn("No effective feed-in tariff found; unable to calculate power cost.");
            return;
        }
        BigDecimal feedInTariffWattSecond = convertTariffToWattSeconds(effectiveFeedInTariff);
        // Get usage tariff
        Tariff effectiveUsageTariff = tariffRepository
                .findEffectiveUsageTariff(dayOfWeek, localTime, currentPower.getEpochTimestamp());
        if (effectiveUsageTariff == null) {
            logger.warn("No effective usage tariff found; unable to calculate power cost.");
            return;
        }
        BigDecimal usageTariffWattSecond = convertTariffToWattSeconds(effectiveUsageTariff);

        BigDecimal costSum = calculateCost(currentPower, feedInTariffWattSecond, usageTariffWattSecond);
        logger.debug("Cost for period end {}: {}", currentPower.getEpochTimestamp(), costSum);
        PowerCost powerCost = new PowerCost(costSum,
                currentPower.getEpochTimestamp(),
                collectionFrequencySeconds);
        powerCostRepository.save(powerCost);
    }

    /**
     * Calculate the cost of the uncosted CurrentPower list, up to the given CurrentPower.
     * Cost is calculated by summing the generation and consumption per measurement (assumed
     * to be per-second), applying the correct tariff, then adding to the total.
     *
     * @param currentPower           the last CurrentPower to sum from the uncosted CurrentPower list
     * @param feedInTariffWattSecond the applicable feed-in tariff
     * @param usageTariffWattSecond  the applicable usage tariff
     * @return cost of power to the consumer. A positive value means a cost to the
     * consumer, whereas a negative value means a benefit to the consumer.
     */
    private BigDecimal calculateCost(CurrentPower currentPower, BigDecimal feedInTariffWattSecond,
                                     BigDecimal usageTariffWattSecond) {
        BigDecimal costSum = BigDecimal.ZERO;
        while (true) {
            CurrentPower cp = uncostedPowers.poll();
            // Could happen if triggered on shutdown, and we haven't accumulated a full period
            if (cp == null)
                break;
            BigDecimal wattSecond = BigDecimal.valueOf(cp.getGeneration())
                    .add(BigDecimal.valueOf(cp.getConsumption()));
            BigDecimal costPerSecond;
            if (wattSecond.compareTo(BigDecimal.ZERO) >= 0) {
                // Positive value = feeding power to the grid
                costPerSecond = wattSecond.multiply(feedInTariffWattSecond);
            } else {
                // Negative value = consuming power from the grid
                costPerSecond = wattSecond.multiply(usageTariffWattSecond);
            }
            costSum = costSum.add(costPerSecond);

            if (cp.getEpochTimestamp() == currentPower.getEpochTimestamp())
                break;
        }
        return costSum.negate();
    }

    /**
     * Converts the tariff from cost per kilowatt-hour to cost per watt-second.
     *
     * @param tariff the tariff to convert
     * @return converted tariff
     */
    private BigDecimal convertTariffToWattSeconds(Tariff tariff) {
        return tariff.getPricePerKwh()
                .divide(WATTS_PER_KILOWATT, KILOWATT_T0_WATT_CONVERSION_SCALE, RoundingMode.HALF_UP)
                .divide(SECONDS_PER_HOUR, HOUR_TO_SECOND_CONVERSION_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Returns the measurement timestamp of the first uncosted CurrentPower as a ZonedDateTime.
     *
     * @return measurement timestamp
     */
    private ZonedDateTime getMeasurementZonedDateTime() {
        CurrentPower firstPower = uncostedPowers.peek();
        // firstPower will not be null since we always add before calling this method
        long measurementTimestampEpoch = firstPower.getEpochTimestamp();
        return Instant.ofEpochSecond(measurementTimestampEpoch).atZone(ZoneId.systemDefault());
    }

    /**
     * Validates collectionFrequencySeconds. Ensures that the collection frequency aligns to minute
     * and hour boundaries so that only one tariff applies when calculating the power cost.
     *
     * @param collectionFrequencySeconds the collection frequency to validate
     */
    private static void validateCollectionFrequency(int collectionFrequencySeconds) {
        if (collectionFrequencySeconds > SECONDS_PER_HOUR.intValue()) {
            throw new IllegalArgumentException("collectionFrequencySeconds cannot be greater than 3600");
        }
        if (collectionFrequencySeconds <= 0) {
            throw new IllegalArgumentException("collectionFrequencySeconds must be between 1 and 3600 (inclusive)");
        }
        if (SECONDS_PER_HOUR.intValue() % collectionFrequencySeconds != 0) {
            throw new IllegalArgumentException("collectionFrequencySeconds must divide evenly into 3600");
        }
        if (collectionFrequencySeconds > SECONDS_PER_MINUTE) {
            if (collectionFrequencySeconds % SECONDS_PER_MINUTE != 0) {
                throw new IllegalArgumentException("When collectionFrequencySeconds is between 60 and 3600, " +
                        "it must be a multiple of 60");
            }
        } else {
            if (SECONDS_PER_MINUTE % collectionFrequencySeconds != 0) {
                throw new IllegalArgumentException("When collectionFrequencySeconds is between 1 and 60, " +
                        "it must evenly divide into 60");
            }
        }
    }

}
