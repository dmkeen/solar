package org.keen.solar.system.domain;

/**
 * Represents the power generation and usage at a point in time
 */
public class CurrentPower {

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
                "epochTimestamp=" + epochTimestamp +
                ", generation=" + generation +
                ", consumption=" + consumption +
                ", uploaded=" + uploaded +
                '}';
    }
}
