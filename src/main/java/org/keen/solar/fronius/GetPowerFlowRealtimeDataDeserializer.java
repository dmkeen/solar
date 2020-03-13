package org.keen.solar.fronius;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.keen.solar.domain.CurrentPower;

import java.io.IOException;
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
    public CurrentPower deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode jsonNode = p.getCodec().readTree(p);

        String timestampString = jsonNode.get("Head").get("Timestamp").textValue();
        OffsetDateTime timestamp = OffsetDateTime.parse(timestampString);

        JsonNode siteNode = jsonNode.get("Body").get("Data").get("Site");
        double powerGeneration = siteNode.get("P_PV").asDouble();
        double powerConsumption = siteNode.get("P_Load").asDouble();

        return new CurrentPower(timestamp.toLocalDateTime(), powerGeneration, powerConsumption);
    }
}
