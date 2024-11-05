package org.keen.solar.financial.domain;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * The cost of electricity as set by the retailer, for the whole or part of a day.
 */
public final class Tariff {

    @Id
    private Long id;
    /**
     * True if this is a feed-in tariff (the price paid for electricity fed to
     * the grid), false if this is a usage tariff.
     */
    private final boolean feedIn;
    /**
     * the date when this tariff applies from, in seconds since the epoch
     */
    private final long startEffectiveDateEpoch;
    /**
     * the date when this tariff stops applying, in seconds since the epoch. Null if it is in force.
     */
    private final Long endEffectiveDateEpoch;
    /**
     * the day of the week that this tariff applies to
     */
    private final DayOfWeek dayOfWeek;
    /**
     * the start of the period, inclusive
     */
    private final LocalTime startOfPeriod;
    /**
     * the end of the period, exclusive
     */
    private final LocalTime endOfPeriod;
    /**
     * the cost per kilowatt-hour
     */
    private final BigDecimal pricePerKwh;

    public Tariff(
            boolean feedIn, long startEffectiveDateEpoch,
            Long endEffectiveDateEpoch,
            DayOfWeek dayOfWeek,
            LocalTime startOfPeriod,
            LocalTime endOfPeriod,
            BigDecimal pricePerKwh) {
        this.feedIn = feedIn;
        this.startEffectiveDateEpoch = startEffectiveDateEpoch;
        this.endEffectiveDateEpoch = endEffectiveDateEpoch;
        this.dayOfWeek = dayOfWeek;
        this.startOfPeriod = startOfPeriod;
        this.endOfPeriod = endOfPeriod;
        this.pricePerKwh = pricePerKwh;
    }

    public Long getId() {
        return id;
    }

    public boolean isFeedIn() {
        return feedIn;
    }

    public long getStartEffectiveDateEpoch() {
        return startEffectiveDateEpoch;
    }

    public Long getEndEffectiveDateEpoch() {
        return endEffectiveDateEpoch;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartOfPeriod() {
        return startOfPeriod;
    }

    public LocalTime getEndOfPeriod() {
        return endOfPeriod;
    }

    public BigDecimal getPricePerKwh() {
        return pricePerKwh;
    }

    @Override
    public String toString() {
        return "Tariff{" +
                "id=" + id +
                ", feedIn=" + feedIn +
                ", startEffectiveDateEpoch=" + startEffectiveDateEpoch +
                ", endEffectiveDateEpoch=" + endEffectiveDateEpoch +
                ", dayOfWeek=" + dayOfWeek +
                ", startOfPeriod=" + startOfPeriod +
                ", endOfPeriod=" + endOfPeriod +
                ", pricePerKwh=" + pricePerKwh +
                '}';
    }
}
