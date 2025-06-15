package org.keen.solar.system.domain;

/**
 * Represents the power generation and usage at a point in time
 *
 * @param epochTimestamp Measurement time in number of seconds since the epoch. Note that this
 *                       could be the time as given by the inverter or by the running application.
 *                       See {@link org.keen.solar.system.fronius.GetPowerFlowRealtimeDataDeserializer}.
 * @param generation     Power generation in Watts
 * @param consumption    Power consumption in Watts, represented as a negative value
 */
public record CurrentPower(long epochTimestamp, double generation, double consumption) {

    @Override
    public String toString() {
        return "CurrentPower{" +
                "epochTimestamp=" + epochTimestamp +
                ", generation=" + generation +
                ", consumption=" + consumption +
                '}';
    }
}
