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
    private String inverterTimestamp;
    /**
     * Measurement time according to the inverter, in number of seconds since the epoch
     */
    private Long inverterEpochTimestamp;
    /**
     * Time that the measurement was received by this application, in ISO8601 format including offset
     */
    private String applicationTimestamp;
    /**
     * Power generation in Watts
     */
    private double generation;
    /**
     * Power consumption in Watts
     */
    private double consumption;

    public CurrentPower(String inverterTimestamp, Long inverterEpochTimestamp, String applicationTimestamp, double generation, double consumption) {
        this.inverterTimestamp = inverterTimestamp;
        this.inverterEpochTimestamp = inverterEpochTimestamp;
        this.applicationTimestamp = applicationTimestamp;
        this.generation = generation;
        this.consumption = consumption;
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
}
