package org.keen.solar.solcast.forecast.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.keen.solar.solcast.forecast.ForecastsDeserializer;

import java.util.List;

/**
 * Wrapper for a list of Solcast solar panel output forecasts. Created to simplify JSON deserialization.
 */
@JsonDeserialize(using = ForecastsDeserializer.class)
public class Forecasts {

    private List<GenerationForecast> forecasts;

    public Forecasts() {
    }

    public void setForecasts(List<GenerationForecast> forecasts) {
        this.forecasts = forecasts;
    }

    public List<GenerationForecast> getForecasts() {
        return forecasts;
    }
}
