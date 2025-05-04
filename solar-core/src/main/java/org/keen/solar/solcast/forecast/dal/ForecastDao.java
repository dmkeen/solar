package org.keen.solar.solcast.forecast.dal;

import org.keen.solar.solcast.forecast.domain.GenerationForecast;

import java.util.Collection;

public interface ForecastDao {

    /**
     * Persists the given forecasts to the repository, updating them if they exist.
     */
    void save(Collection<GenerationForecast> forecasts);
}
