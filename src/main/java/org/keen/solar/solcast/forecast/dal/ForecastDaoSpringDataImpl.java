package org.keen.solar.solcast.forecast.dal;

import org.keen.solar.solcast.forecast.domain.GenerationForecast;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ForecastDaoSpringDataImpl implements ForecastDao {

    private final ForecastRepository repository;

    public ForecastDaoSpringDataImpl(ForecastRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Collection<GenerationForecast> forecasts) {
        // Retrieve the id for any existing forecast for the same period so that it gets updated in the database,
        // rather than inserted.
        // Not particularly efficient, given that each forecast is retrieved individually from the database.
        // Spring Data JDBC doesn't provide a mechanism to write queries that take collections as parameters.
        forecasts.forEach(forecast -> {
            GenerationForecast existingForecast = repository.findByPeriodEnd(forecast.getPeriod_end_epoch());
            if (existingForecast != null) {
                forecast.setId(existingForecast.getId());
            }
        });
        repository.saveAll(forecasts);
    }
}
