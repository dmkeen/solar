package org.keen.solar.power.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.keen.solar.power.fronius.GetPowerFlowRealtimeDataDeserializer;
import org.springframework.data.annotation.Id;

/**
 * Represents the power generation and usage at a point in time
 */
@JsonDeserialize(using = GetPowerFlowRealtimeDataDeserializer.class)
public class CurrentPower {

    @Id
    private Long id;
    /**
     * Measurement time according to the inverter, in ISO8601 format including offset
     */
    private final String inverterTimestamp;
    /**
     * Measurement time according to the inverter, in number of seconds since the epoch
     */
    private final Long inverterEpochTimestamp;
    /**
     * Time that the measurement was received by this application, in ISO8601 format including offset
     */
    private final String applicationTimestamp;
    /**
     * Power generation in Watts
     */
    private final double generation;
    /**
     * Power consumption in Watts
     */
    private final double consumption;
    /**
     * Flag indicating whether this power generation measurement has been uploaded to Solcast
     */
    private boolean uploaded;

    public CurrentPower(String inverterTimestamp, Long inverterEpochTimestamp, String applicationTimestamp,
                        double generation, double consumption, boolean uploaded) {
        this.inverterTimestamp = inverterTimestamp;
        this.inverterEpochTimestamp = inverterEpochTimestamp;
        this.applicationTimestamp = applicationTimestamp;
        this.generation = generation;
        this.consumption = consumption;
        this.uploaded = uploaded;
    }

    public Long getId() {
        return id;
    }

    public String getInverterTimestamp() {
        return inverterTimestamp;
    }

    public Long getInverterEpochTimestamp() {
        return inverterEpochTimestamp;
    }

    public String getApplicationTimestamp() {
        return applicationTimestamp;
    }

    public double getGeneration() {
        return generation;
    }

    public double getConsumption() {
        return consumption;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    @Override
    public String toString() {
        return "CurrentPower{" +
                "id=" + id +
                ", inverterTimestamp='" + inverterTimestamp + '\'' +
                ", inverterEpochTimestamp=" + inverterEpochTimestamp +
                ", applicationTimestamp='" + applicationTimestamp + '\'' +
                ", generation=" + generation +
                ", consumption=" + consumption +
                ", uploaded=" + uploaded +
                '}';
    }
}
