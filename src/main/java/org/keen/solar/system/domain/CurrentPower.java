package org.keen.solar.system.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.keen.solar.system.fronius.GetPowerFlowRealtimeDataDeserializer;
import org.springframework.data.annotation.Id;

/**
 * Represents the power generation and usage at a point in time
 */
@JsonDeserialize(using = GetPowerFlowRealtimeDataDeserializer.class)
public class CurrentPower {

    @Id
    private Long id;
    /**
     * Measurement time according to the inverter, in number of seconds since the epoch
     */
    private final long inverterEpochTimestamp;
    /**
     * Number of seconds of the zone offset of the inverter time
     */
    private final int inverterZoneOffsetSeconds;
    /**
     * Number of seconds difference between the application time and the inverter measurement time.
     * Useful if the inverter is not keeping good time. Value is positive if the inverter time is
     * earlier than the application time.
     */
    private final long appTimeDifference;
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

    public CurrentPower(long inverterEpochTimestamp, int inverterZoneOffsetSeconds, long appTimeDifference,
                        double generation, double consumption, boolean uploaded) {
        this.inverterEpochTimestamp = inverterEpochTimestamp;
        this.inverterZoneOffsetSeconds = inverterZoneOffsetSeconds;
        this.appTimeDifference = appTimeDifference;
        this.generation = generation;
        this.consumption = consumption;
        this.uploaded = uploaded;
    }

    public Long getId() {
        return id;
    }

    public long getInverterEpochTimestamp() {
        return inverterEpochTimestamp;
    }

    public int getInverterZoneOffsetSeconds() {
        return inverterZoneOffsetSeconds;
    }

    public long getAppTimeDifference() {
        return appTimeDifference;
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
                ", inverterEpochTimestamp=" + inverterEpochTimestamp +
                ", inverterZoneOffsetSeconds=" + inverterZoneOffsetSeconds +
                ", appTimeDifference=" + appTimeDifference +
                ", generation=" + generation +
                ", consumption=" + consumption +
                ", uploaded=" + uploaded +
                '}';
    }
}
