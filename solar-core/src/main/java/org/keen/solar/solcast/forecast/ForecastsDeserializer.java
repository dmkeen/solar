package org.keen.solar.solcast.forecast;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import org.keen.solar.solcast.forecast.domain.Forecasts;
import org.keen.solar.solcast.forecast.domain.GenerationForecast;

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
    public Forecasts deserialize(JsonParser p, DeserializationContext ctxt) {
        JsonNode jsonNode = p.objectReadContext().readTree(p);

        JsonNode forecastsNode = jsonNode.get("forecasts");
        ArrayList<GenerationForecast> generationForecasts = new ArrayList<>();

        forecastsNode.values().iterator().forEachRemaining(forecastNode -> {
            double pv_estimate = forecastNode.get("pv_estimate").asDouble();
            double pv_estimate10 = forecastNode.get("pv_estimate10").asDouble();
            double pv_estimate90 = forecastNode.get("pv_estimate90").asDouble();
            String period_end = forecastNode.get("period_end").asString();
            long periodEndEpoch = OffsetDateTime.parse(period_end).toEpochSecond();
            Duration period = Duration.parse(forecastNode.get("period").asString());

            generationForecasts.add(new GenerationForecast(pv_estimate, pv_estimate10, pv_estimate90, periodEndEpoch, (int) period.toSeconds()));
        });

        Forecasts forecasts = new Forecasts();
        forecasts.setForecasts(generationForecasts);
        return forecasts;
    }
}
