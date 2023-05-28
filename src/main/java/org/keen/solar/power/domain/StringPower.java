package org.keen.solar.power.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;

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
     * End of the averaging period, in number of seconds since the epoch
     */
    private long periodEndEpoch;
    /**
     * Length of the averaging period in seconds
     */
    private int periodLengthSeconds;
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

    public StringPower(long periodEndEpoch, int periodLengthSeconds, double string1Volts,
                       double string1Amps, double string2Volts, double string2Amps) {
        this.periodEndEpoch = periodEndEpoch;
        this.periodLengthSeconds = periodLengthSeconds;
        this.string1Data = new StringData(string1Volts, string1Amps);
        this.string2Data = new StringData(string2Volts, string2Amps);
    }

    public Long getId() {
        return id;
    }

    public long getPeriodEndEpoch() {
        return periodEndEpoch;
    }

    public int getPeriodLengthSeconds() {
        return periodLengthSeconds;
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
                "periodEndEpoch=" + periodEndEpoch +
                ", period=" + periodLengthSeconds +
                ", string1=" + string1Data +
                ", string2=" + string2Data +
                '}';
    }
}
