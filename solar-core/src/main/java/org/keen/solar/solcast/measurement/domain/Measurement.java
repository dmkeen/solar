package org.keen.solar.solcast.measurement.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.keen.solar.system.domain.CurrentPower;
import org.keen.solar.solcast.measurement.MeasurementSerializer;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * A power generation measurement for a specified period
 */
@JsonSerialize(using = MeasurementSerializer.class)
public class Measurement {

    /**
     * End of the averaging period in ISO8601 datetime format in UTC timezone
     */
    private OffsetDateTime period_end;
    /**
     * Length of averaging period in ISO8601 duration format. Note that the minimum period supported by Solcast
     * is 5 minutes (source: https://docs.solcast.com.au/#measurements-rooftop-site).
     */
    private Duration period;
    /**
     * Power output being measured averaged over the period ending at period_end, in kilowatts
     */
    private double total_power;
    /**
     * CurrentPowers that were averaged to create this Measurement
     */
    private List<CurrentPower> source;

    /**
     * Constructor for deserialization only
     */
    private Measurement() {}

    public Measurement(OffsetDateTime period_end, Duration period, double total_power, List<CurrentPower> source) {
        this.period_end = period_end;
        this.period = period;
        this.total_power = total_power;
        this.source = source;
    }

    public OffsetDateTime getPeriod_end() {
        return period_end;
    }

    public Duration getPeriod() {
        return period;
    }

    public double getTotal_power() {
        return total_power;
    }

    public List<CurrentPower> getSource() {
        return source;
    }

    // Implementation of equals is primarily to simplify the comparison of uploaded measurements
    // with the successfully uploaded measurements from the Solcast API. Could have included
    // total_power but would probably need to compare with a tolerance.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measurement that = (Measurement) o;
        return period_end.equals(that.period_end) &&
                period.equals(that.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(period_end, period);
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "period_end=" + period_end +
                ", period=" + period +
                ", total_power=" + total_power +
                ", source=" + source +
                '}';
    }
}
