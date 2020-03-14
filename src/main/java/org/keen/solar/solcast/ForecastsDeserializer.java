package org.keen.solar.solcast;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.keen.solar.domain.Forecasts;
import org.keen.solar.domain.GenerationForecast;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;

/**
 * Deserializes the result from the Solcast forecasts API call to a Forecasts object.
 */
public class ForecastsDeserializer extends StdDeserializer<Forecasts> {

    public ForecastsDeserializer() {
        this(Forecasts.class);
    }

    public ForecastsDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Forecasts deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode jsonNode = p.getCodec().readTree(p);

        JsonNode forecastsNode = jsonNode.get("forecasts");
        ArrayList<GenerationForecast> generationForecasts = new ArrayList<>();

        forecastsNode.elements().forEachRemaining(forecastNode -> {
            double pv_estimate = forecastNode.get("pv_estimate").asDouble();
            double pv_estimate10 = forecastNode.get("pv_estimate10").asDouble();
            double pv_estimate90 = forecastNode.get("pv_estimate90").asDouble();
            String period_end = forecastNode.get("period_end").textValue();
            long periodEndEpoch = OffsetDateTime.parse(period_end).toEpochSecond();
            Duration period = Duration.parse(forecastNode.get("period").textValue());

            generationForecasts.add(new GenerationForecast(pv_estimate, pv_estimate10, pv_estimate90, period_end, periodEndEpoch, period));
        });

        Forecasts forecasts = new Forecasts();
        forecasts.setForecasts(generationForecasts);
        return forecasts;
    }
}
