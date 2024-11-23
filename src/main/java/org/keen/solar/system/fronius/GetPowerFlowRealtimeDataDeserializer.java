package org.keen.solar.system.fronius;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.keen.solar.system.domain.CurrentPower;
import org.keen.solar.util.MathsUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * Deserializes the result from Fronius API call /solar_api/v1/GetPowerFlowRealtimeData.fcgi into a CurrentPower object
 */
@JsonComponent
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
    public CurrentPower deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        OffsetDateTime applicationTimestamp = OffsetDateTime.now();

        JsonNode jsonNode = p.getCodec().readTree(p);

        String timestampString = jsonNode.get("Head").get("Timestamp").textValue();
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

        return new CurrentPower(timestamp.toEpochSecond(), powerGeneration, powerConsumption, false);
    }

}
