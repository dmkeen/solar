package org.keen.solar.string.domain;


import java.math.BigDecimal;

/**
 * Data for "strings" of panels connected to an inverter for a specified period
 *
 * @param periodEndEpoch      End of the averaging period, in number of seconds since the epoch
 * @param periodLengthSeconds Length of the averaging period in seconds
 * @param string1Data        Data for string 1
 * @param string2Data        Data for string 2
 */
public record StringPower(long periodEndEpoch, int periodLengthSeconds,
                          StringData string1Data, StringData string2Data) {

    /**
     * Data for a string of panels connected to an inverter
     *
     * @param volts Average DC voltage
     * @param amps  Average amperage
     * @param power Average power in Watts
     */
    public record StringData(double volts, double amps, double power) {

        public StringData(BigDecimal volts, BigDecimal amps) {
            this(volts.doubleValue(), amps.doubleValue(), volts.multiply(amps).doubleValue());
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

    public StringPower(long periodEndEpoch, int periodLengthSeconds, BigDecimal string1Volts,
                       BigDecimal string1Amps, BigDecimal string2Volts, BigDecimal string2Amps) {
        this(periodEndEpoch, periodLengthSeconds,
                new StringData(string1Volts, string1Amps),
                new StringData(string2Volts, string2Amps));
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
