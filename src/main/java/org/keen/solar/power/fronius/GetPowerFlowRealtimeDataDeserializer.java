package org.keen.solar.power.fronius;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.keen.solar.power.domain.CurrentPower;

import java.io.IOException;
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
        double powerGeneration = siteNode.get("P_PV").asDouble();
        double powerConsumption = siteNode.get("P_Load").asDouble();

        return new CurrentPower(inverterTimestamp.toEpochSecond(), inverterTimestamp.getOffset().getTotalSeconds(),
                Duration.between(inverterTimestamp, applicationTimestamp).getSeconds(),
                powerGeneration, powerConsumption, false);
    }
}
