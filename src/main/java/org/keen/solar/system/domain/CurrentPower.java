package org.keen.solar.system.domain;

import org.springframework.data.annotation.Id;

/**
 * Represents the power generation and usage at a point in time
 */
public class CurrentPower {

    @Id
    private Long id;
    /**
     * Measurement time in number of seconds since the epoch. Note that this
     * could be the time as given by the inverter or by the running application.
     * See {@link org.keen.solar.system.fronius.GetPowerFlowRealtimeDataDeserializer}.
     */
    private final long epochTimestamp;
    /**
     * Power generation in Watts
     */
    private final double generation;
    /**
     * Power consumption in Watts, represented as a negative value
     */
    private final double consumption;
    /**
     * Flag indicating whether this power generation measurement has been uploaded to Solcast
     */
    private boolean uploaded;

    public CurrentPower(long epochTimestamp, double generation, double consumption, boolean uploaded) {
        this.epochTimestamp = epochTimestamp;
        this.generation = generation;
        this.consumption = consumption;
        this.uploaded = uploaded;
    }

    public Long getId() {
        return id;
    }

    public long getEpochTimestamp() {
        return epochTimestamp;
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
                ", epochTimestamp=" + epochTimestamp +
                ", generation=" + generation +
                ", consumption=" + consumption +
                ", uploaded=" + uploaded +
                '}';
    }
}
