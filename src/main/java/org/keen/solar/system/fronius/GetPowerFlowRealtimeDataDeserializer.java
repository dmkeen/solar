package org.keen.solar.system.fronius;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.keen.solar.system.domain.CurrentPower;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * Deserializes the result from Fronius API call /solar_api/v1/GetPowerFlowRealtimeData.fcgi into a CurrentPower object
 */
public class GetPowerFlowRealtimeDataDeserializer extends StdDeserializer<CurrentPower> {

    public GetPowerFlowRealtimeDataDeserializer() {
        this(CurrentPower.class);
    }

    public GetPowerFlowRealtimeDataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CurrentPower deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        OffsetDateTime applicationTimestamp = OffsetDateTime.now();

        JsonNode jsonNode = p.getCodec().readTree(p);

        String timestampString = jsonNode.get("Head").get("Timestamp").textValue();
        OffsetDateTime inverterTimestamp = OffsetDateTime.parse(timestampString);

        JsonNode siteNode = jsonNode.get("Body").get("Data").get("Site");
        double powerGeneration = roundMeasurement(siteNode.get("P_PV").decimalValue());
        double powerConsumption = roundMeasurement(siteNode.get("P_Load").decimalValue());

        return new CurrentPower(inverterTimestamp.toEpochSecond(), inverterTimestamp.getOffset().getTotalSeconds(),
                Duration.between(inverterTimestamp, applicationTimestamp).getSeconds(),
                powerGeneration, powerConsumption, false);
    }

    /**
     * Returns the given measurement, rounded to two decimal places.<br/>
     * <br/>
     * The Fronius API sometimes returns measurements that are slightly above
     * or below a particular value, apparently due to the imprecision of
     * representing a floating point value. For example, it might return
     * a value for P_PV of 1931.2000000001 or 1931.19999999999. These are best
     * interpreted as 1931.20.
     *
     * @param measurement the measurement returned from the API
     * @return measurement as a double
     */
    private static double roundMeasurement(BigDecimal measurement) {
        return measurement
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
