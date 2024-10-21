package org.keen.solar.solcast.measurement.domain;

import java.util.List;

/**
 * Models the response from the Solcast measurements API.
 */
public class MeasurementResponse {

    private String site_resource_id;
    private List<Measurement> measurements;

    public String getSite_resource_id() {
        return site_resource_id;
    }

    public void setSite_resource_id(String site_resource_id) {
        this.site_resource_id = site_resource_id;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
}
