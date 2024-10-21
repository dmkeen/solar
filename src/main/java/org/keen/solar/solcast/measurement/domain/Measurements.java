package org.keen.solar.solcast.measurement.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.keen.solar.solcast.measurement.MeasurementsSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for a list of Measurements to upload to Solcast. Created in order to customise JSON serialization
 * according to the requirements of the <a href="https://docs.solcast.com.au/#measurements-rooftop-site">Solcast API</a>.
 */
@JsonSerialize(using = MeasurementsSerializer.class)
public class Measurements {

    private List<Measurement> measurements;

    public Measurements() {
        measurements = new ArrayList<>();
    }

    public Measurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    @Override
    public String toString() {
        return measurements.toString();
    }
}
