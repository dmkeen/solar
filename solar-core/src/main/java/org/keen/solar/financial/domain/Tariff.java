package org.keen.solar.financial.domain;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * The cost of electricity as set by the retailer, for the whole or part of a day.
 *
 * @param feedIn                  True if this is a feed-in tariff (the price paid for electricity fed to
 *                                the grid), false if this is a usage tariff.
 * @param startEffectiveDateEpoch the date when this tariff applies from, in seconds since the epoch
 * @param endEffectiveDateEpoch   the date when this tariff stops applying, in seconds since the epoch. Null if it is in force.
 * @param dayOfWeek               the day of the week that this tariff applies to
 * @param startOfPeriod           the start of the period, inclusive
 * @param endOfPeriod             the end of the period, exclusive
 * @param pricePerKwh             the cost per kilowatt-hour
 */
public record Tariff(boolean feedIn, long startEffectiveDateEpoch, Long endEffectiveDateEpoch, DayOfWeek dayOfWeek,
                     LocalTime startOfPeriod, LocalTime endOfPeriod, BigDecimal pricePerKwh) {

    @Override
    public String toString() {
        return "Tariff{" +
                "feedIn=" + feedIn +
                ", startEffectiveDateEpoch=" + startEffectiveDateEpoch +
                ", endEffectiveDateEpoch=" + endEffectiveDateEpoch +
                ", dayOfWeek=" + dayOfWeek +
                ", startOfPeriod=" + startOfPeriod +
                ", endOfPeriod=" + endOfPeriod +
                ", pricePerKwh=" + pricePerKwh +
                '}';
    }
}
