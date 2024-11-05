package org.keen.solar.financial.domain;

import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

/**
 * Represents the electricity cost paid by the consumer for a period of time.
 */
public final class PowerCost {

    @Id
    private Long id;
    /**
     * The cost of the electricity. A positive value means a cost to the
     * consumer, whereas a negative value means a benefit to the consumer.
     */
    private final BigDecimal cost;
    /**
     * End of the averaging period, in number of seconds since the epoch
     */
    private final long periodEndEpoch;
    /**
     * Length of the averaging period in seconds
     */
    private final int periodLengthSeconds;

    public PowerCost(BigDecimal cost, long periodEndEpoch, int periodLengthSeconds) {
        this.cost = cost;
        this.periodEndEpoch = periodEndEpoch;
        this.periodLengthSeconds = periodLengthSeconds;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public long getPeriodEndEpoch() {
        return periodEndEpoch;
    }

    public int getPeriodLengthSeconds() {
        return periodLengthSeconds;
    }

    @Override
    public String toString() {
        return "PowerCost{" +
                "id=" + id +
                ", cost=" + cost +
                ", periodEndEpoch=" + periodEndEpoch +
                ", periodLengthSeconds=" + periodLengthSeconds +
                '}';
    }
}
