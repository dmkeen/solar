package org.keen.solar.financial.dal;

import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.keen.solar.financial.domain.Tariff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

public class TariffDaoEclipseStoreImpl implements TariffDao {

    private static final Logger logger = LoggerFactory.getLogger(TariffDaoEclipseStoreImpl.class);

    private final EmbeddedStorageManager storageManager;
    private final Map<DayOfWeek, List<Tariff>> root;

    @SuppressWarnings("unchecked")
    public TariffDaoEclipseStoreImpl(EmbeddedStorageManager storageManager) {
        this.storageManager = storageManager;

        if (storageManager.root() == null) {
            root = new EnumMap<>(DayOfWeek.class);
            storageManager.setRoot(root);
        } else {
            root = (Map<DayOfWeek, List<Tariff>>) storageManager.root();
        }
    }

    @Override
    public Tariff getEffectiveFeedInTariff(DayOfWeek dayOfWeek, LocalTime localTime, long epochTime) {
        return getEffectiveTariff(dayOfWeek, localTime, epochTime, Tariff::feedIn);
    }

    @Override
    public Tariff getEffectiveUsageTariff(DayOfWeek dayOfWeek, LocalTime localTime, long epochTime) {
        return getEffectiveTariff(dayOfWeek, localTime, epochTime, not(Tariff::feedIn));
    }

    private Tariff getEffectiveTariff(DayOfWeek dayOfWeek, LocalTime localTime, long epochTime,
                                      Predicate<Tariff> tariffPredicate) {
        synchronized (root) {
            List<Tariff> tariffs = root.get(dayOfWeek);
            if (tariffs == null) {
                return null;
            }
            List<Tariff> applicableTariffs = tariffs.stream()
                    .filter(tariffPredicate)
                    .filter(tariff -> tariff.startEffectiveDateEpoch() < epochTime &&
                            (tariff.endEffectiveDateEpoch() == null || tariff.endEffectiveDateEpoch() > epochTime))
                    .filter(tariff ->
                            (tariff.startOfPeriod().isBefore(localTime) || tariff.startOfPeriod().equals(localTime))
                                    && tariff.endOfPeriod().isAfter(localTime))
                    .toList();
            if (applicableTariffs.isEmpty()) {
                return null;
            }
            if (applicableTariffs.size() > 1) {
                logger.warn("Unexpected state: Found multiple applicable tariffs for {}, {}, {}: {}",
                        dayOfWeek, localTime, epochTime, applicableTariffs);
            }
            return applicableTariffs.getFirst();
        }
    }

    @Override
    public void applyTariffs(List<Tariff> usageTariffs, List<Tariff> feedInTariffs) {
        validateTariffs(usageTariffs, feedInTariffs);

        Map<DayOfWeek, List<Tariff>> newTariffMap = Stream.concat(usageTariffs.stream(), feedInTariffs.stream())
                .collect(Collectors.groupingBy(Tariff::dayOfWeek,
                        Collectors.mapping(Function.identity(), Collectors.toList())));
        // Since we've validated that all effective dates are the same, we can just select one.
        long startEffectiveDateEpoch = newTariffMap.get(DayOfWeek.MONDAY).getFirst().startEffectiveDateEpoch();

        synchronized (root) {
            newTariffMap.forEach((key, value) -> {
                List<Tariff> existingTariffs = root.get(key);
                if (existingTariffs == null) {
                    existingTariffs = new ArrayList<>();
                }
                // Expire current tariffs
                List<Tariff> updatedTariffs = existingTariffs.stream()
                        .map(tariff -> {
                            if (tariff.endEffectiveDateEpoch() != null
                                    && tariff.endEffectiveDateEpoch() < startEffectiveDateEpoch) {
                                return tariff;
                            } else {
                                return new Tariff(tariff.feedIn(), tariff.startEffectiveDateEpoch(),
                                        startEffectiveDateEpoch - 1, tariff.dayOfWeek(),
                                        tariff.startOfPeriod(), tariff.endOfPeriod(), tariff.pricePerKwh());
                            }
                        })
                        .collect(Collectors.toCollection(ArrayList::new));

                // Insert new tariffs
                updatedTariffs.addAll(value);
                // Update store
                boolean replaced = root.replace(key, existingTariffs, updatedTariffs);
                if (!replaced) {
                    logger.warn("Existing tariffs for {} not replaced in store! Existing: {}, Updated: {}",
                            key, existingTariffs, updatedTariffs);
                }
                storageManager.storeAll(root, updatedTariffs);
            });
        }
    }

    private static void validateTariffs(List<Tariff> usageTariffs, List<Tariff> feedInTariffs) {
        long countOfUsageTariffDays = usageTariffs.stream()
                .map(Tariff::dayOfWeek)
                .distinct()
                .count();
        if (countOfUsageTariffDays != 7) {
            throw new IllegalArgumentException("Must supply usage tariffs for each day of the week");
        }
        long countOfFeedInTariffDays = feedInTariffs.stream()
                .map(Tariff::dayOfWeek)
                .distinct()
                .count();
        if (countOfFeedInTariffDays != 7) {
            throw new IllegalArgumentException("Must supply feed-in tariffs for each day of the week");
        }

        List<Long> effectiveDates = Stream.concat(usageTariffs.stream(), feedInTariffs.stream())
                .map(Tariff::startEffectiveDateEpoch)
                .distinct()
                .toList();
        if (effectiveDates.size() > 1) {
            throw new IllegalArgumentException("Tariffs must all have the same start effective date");
        }

    }
}
