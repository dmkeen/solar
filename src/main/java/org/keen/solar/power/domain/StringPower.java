package org.keen.solar.power.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * Data for "strings" of panels connected to an inverter for a specified period
 */
public class StringPower {

    public static class StringData {
        /**
         * Average DC voltage
         */
        private final double volts;
        /**
         * Average amperage
         */
        private final double amps;
        /**
         * Average power in Watts
         */
        private final double power;

        public StringData(double volts, double amps) {
            this.volts = volts;
            this.amps = amps;
            this.power = volts * amps;
        }

        public double getVolts() {
            return volts;
        }

        public double getAmps() {
            return amps;
        }

        public double getPower() {
            return power;
        }

        @Override
        public String toString() {
            return "{" +
                    "V=" + volts +
                    ", A=" + amps +
                    ", W=" + power +
                    '}';
        }
    }

    @Id
    private Long id;
    /**
     * End of the averaging period
     */
    private OffsetDateTime periodEnd;
    /**
     * End of the averaging period, in number of seconds since the epoch
     */
    private Long periodEndEpoch;
    /**
     * Length of the averaging period
     */
    private Duration period;
    /**
     * Data for string 1
     */
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL, prefix = "string1_")
    private StringData string1Data;
    /**
     * Data for string 2
     */
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL, prefix = "string2_")
    private StringData string2Data;

    private StringPower() {
    }

    public StringPower(OffsetDateTime periodEnd, Long periodEndEpoch, Duration period, double string1Volts,
                       double string1Amps, double string2Volts, double string2Amps) {
        this.periodEnd = periodEnd;
        this.periodEndEpoch = periodEndEpoch;
        this.period = period;
        this.string1Data = new StringData(string1Volts, string1Amps);
        this.string2Data = new StringData(string2Volts, string2Amps);
    }

    public Long getId() {
        return id;
    }

    public OffsetDateTime getPeriodEnd() {
        return periodEnd;
    }

    public Long getPeriodEndEpoch() {
        return periodEndEpoch;
    }

    public Duration getPeriod() {
        return period;
    }

    public StringData getString1Data() {
        return string1Data;
    }

    public StringData getString2Data() {
        return string2Data;
    }

    @Override
    public String toString() {
        return "StringPower{" +
                "periodEnd=" + periodEnd +
                ", period=" + period +
                ", string1=" + string1Data +
                ", string2=" + string2Data +
                '}';
    }
}
