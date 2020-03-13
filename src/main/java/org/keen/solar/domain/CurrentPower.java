package org.keen.solar.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.keen.solar.fronius.GetPowerFlowRealtimeDataDeserializer;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * Represents the power generation and usage at a point in time
 */
@JsonDeserialize(using = GetPowerFlowRealtimeDataDeserializer.class)
public class CurrentPower {

    @Id
    private Long id;
    /**
     * Timestamp of the measurement
     */
    private LocalDateTime timestamp;
    /**
     * Power generation in Watts
     */
    private double generation;
    /**
     * Power consumption in Watts
     */
    private double consumption;

    public CurrentPower(LocalDateTime timestamp, double generation, double consumption) {
        this.timestamp = timestamp;
        this.generation = generation;
        this.consumption = consumption;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getGeneration() {
        return generation;
    }

    public double getConsumption() {
        return consumption;
    }
}
