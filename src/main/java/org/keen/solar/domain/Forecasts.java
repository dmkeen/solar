package org.keen.solar.domain;

import java.util.List;

/**
 * Wrapper for a list of Solcast solar panel output forecasts. Created to simplify JSON deserialization.
 */
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
