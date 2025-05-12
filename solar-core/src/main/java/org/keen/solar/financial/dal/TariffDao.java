package org.keen.solar.financial.dal;

import org.keen.solar.financial.domain.Tariff;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface TariffDao {

    /**
     * Retrieves the feed-in tariff for the given day and time, effective
     * as at {@code epochTime}.
     *
     * @param dayOfWeek the day of the week to retrieve the tariff for
     * @param localTime the time of day to retrieve the tariff for
     * @param epochTime the epoch time that the tariff is effective for
     * @return the feed-in tariff
     */
    Tariff getEffectiveFeedInTariff(DayOfWeek dayOfWeek, LocalTime localTime, long epochTime);

    /**
     * Retrieves the usage tariff for the given day and time, effective
     * as at {@code epochTime}.
     *
     * @param dayOfWeek the day of the week to retrieve the tariff for
     * @param localTime the time of day to retrieve the tariff for
     * @param epochTime the epoch time that the tariff is effective for
     * @return the usage tariff
     */
    Tariff getEffectiveUsageTariff(DayOfWeek dayOfWeek, LocalTime localTime, long epochTime);

    /**
     * Applies the given lists of usage tariffs and feed-in tariffs by:
     * - Expiring existing tariffs one second prior to the new tariffs' start date.
     * - Inserting the new tariffs into the data store.
     *
     * @param usageTariffs   the list of usage tariffs to apply
     * @param feedInTariffs  the list of feed-in tariffs to apply
     */
    void applyTariffs(List<Tariff> usageTariffs, List<Tariff> feedInTariffs);
}
