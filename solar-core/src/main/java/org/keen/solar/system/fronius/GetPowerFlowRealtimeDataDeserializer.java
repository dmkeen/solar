package org.keen.solar.system.fronius;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import org.keen.solar.system.domain.CurrentPower;
import org.keen.solar.util.MathsUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JacksonComponent;

import java.time.OffsetDateTime;

/**
 * Deserializes the result from Fronius API call /solar_api/v1/GetPowerFlowRealtimeData.fcgi into a CurrentPower object
 */
@JacksonComponent
public class GetPowerFlowRealtimeDataDeserializer extends StdDeserializer<CurrentPower> {

    @Value("${app.current-power.use-inverter-timestamp}")
    private boolean useInverterTimestamp;

    public GetPowerFlowRealtimeDataDeserializer() {
        this(CurrentPower.class);
        LoggerFactory.getLogger(GetPowerFlowRealtimeDataDeserializer.class)
                .info("useInverterTimestamp configured as: {}", useInverterTimestamp);
    }

    public GetPowerFlowRealtimeDataDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CurrentPower deserialize(JsonParser p, DeserializationContext ctxt) {
        OffsetDateTime applicationTimestamp = OffsetDateTime.now();

        JsonNode jsonNode = p.objectReadContext().readTree(p);

        String timestampString = jsonNode.get("Head").get("Timestamp").asString();
        OffsetDateTime inverterTimestamp = OffsetDateTime.parse(timestampString);

        JsonNode siteNode = jsonNode.get("Body").get("Data").get("Site");
        double powerGeneration = MathsUtil.roundMeasurement(siteNode.get("P_PV").decimalValue()).doubleValue();
        double powerConsumption = MathsUtil.roundMeasurement(siteNode.get("P_Load").decimalValue()).doubleValue();

        OffsetDateTime timestamp;
        if (useInverterTimestamp) {
            timestamp = inverterTimestamp;
        } else {
            timestamp = applicationTimestamp;
        }

        return new CurrentPower(timestamp.toEpochSecond(), powerGeneration, powerConsumption);
    }

}
