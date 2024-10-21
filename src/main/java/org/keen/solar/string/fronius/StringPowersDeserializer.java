package org.keen.solar.string.fronius;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.keen.solar.string.domain.StringPower;
import org.keen.solar.string.domain.StringPowers;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StringPowersDeserializer extends StdDeserializer<StringPowers> {

    public StringPowersDeserializer() {
        this(StringPowers.class);
    }

    protected StringPowersDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public StringPowers deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode jsonNode = p.getCodec().readTree(p);

        // Note that while the StartDate returned in the response is the same as the parameter in the request,
        // the EndDate is greater by one day (and sometimes less one second).
        // This is correct for the case that only a date (without time) is passed as the EndDate parameter,
        // because the API will return data up to the end of that day.
        // It is incorrect if a date and time (other than midnight) is passed in, though.
        // The data returned corresponds to the requested range, however.
        String startDateString = jsonNode.get("Head").get("RequestArguments").get("StartDate").textValue();
        OffsetDateTime startDateTime = OffsetDateTime.parse(startDateString);

        jsonNode = jsonNode.get("Body").get("Data").get("inverter/1").get("Data");

        Iterator<Map.Entry<String, JsonNode>> currentString1 = jsonNode.get("Current_DC_String_1").get("Values").fields();
        Iterator<Map.Entry<String, JsonNode>> currentString2 = jsonNode.get("Current_DC_String_2").get("Values").fields();
        Iterator<Map.Entry<String, JsonNode>> voltageString1 = jsonNode.get("Voltage_DC_String_1").get("Values").fields();
        Iterator<Map.Entry<String, JsonNode>> voltageString2 = jsonNode.get("Voltage_DC_String_2").get("Values").fields();

        List<StringPower> stringPowers = new ArrayList<>();
        while (currentString1.hasNext()) {
            Map.Entry<String, JsonNode> current1 = currentString1.next();
            Map.Entry<String, JsonNode> current2 = currentString2.next();
            Map.Entry<String, JsonNode> voltage1 = voltageString1.next();
            Map.Entry<String, JsonNode> voltage2 = voltageString2.next();
            OffsetDateTime periodEnd = startDateTime.plusSeconds(Long.parseLong(current1.getKey()));
            stringPowers.add(new StringPower(periodEnd.toEpochSecond(),
                    300,
                    voltage1.getValue().asDouble(), current1.getValue().asDouble(),
                    voltage2.getValue().asDouble(), current2.getValue().asDouble()));
        }

        return new StringPowers(stringPowers);
    }
}
