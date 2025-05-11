package org.keen.solar.financial.domain;

import java.math.BigDecimal;

/**
 * Represents the electricity cost paid by the consumer for a period of time.
 *
 * @param cost                The cost of the electricity. A positive value means a cost to the
 *                            consumer, whereas a negative value means a benefit to the consumer.
 * @param periodEndEpoch      End of the averaging period, in number of seconds since the epoch
 * @param periodLengthSeconds Length of the averaging period in seconds
 */
public record PowerCost(BigDecimal cost, long periodEndEpoch, int periodLengthSeconds) {

    @Override
    public String toString() {
        return "PowerCost{" +
                "cost=" + cost +
                ", periodEndEpoch=" + periodEndEpoch +
                ", periodLengthSeconds=" + periodLengthSeconds +
                '}';
    }
}
