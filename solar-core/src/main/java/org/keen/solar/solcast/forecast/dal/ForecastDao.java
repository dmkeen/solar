package org.keen.solar.solcast.forecast.dal;

import org.keen.solar.solcast.forecast.domain.GenerationForecast;

import java.util.Collection;
import java.util.List;

public interface ForecastDao {

    /**
     * Persists the given forecasts to the repository, updating them if they exist.
     */
    void save(Collection<GenerationForecast> forecasts);

    /**
     * Returns all the GenerationForecasts for the given period.
     *
     * @param fromEpochSeconds start time, in seconds since the epoch (inclusive)
     * @param toEpochSeconds   end time, in seconds since the epoch (exclusive)
     * @return list of GenerationForecast
     */
    List<GenerationForecast> getForecasts(long fromEpochSeconds, long toEpochSeconds);
}
