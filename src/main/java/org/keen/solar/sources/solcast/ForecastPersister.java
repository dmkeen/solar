package org.keen.solar.sources.solcast;

import org.keen.solar.dal.ForecastRepository;
import org.keen.solar.domain.GenerationForecast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Retrieves the solar panel output forecasts and persists them
 */
public class ForecastPersister {

    @Autowired
    private ForecastRetriever retriever;

    @Autowired
    private ForecastRepository repository;

    @Scheduled(cron = "${app.solcast.forecast-retrieval-cron}")
    public void retrieveAndPersist() {
        List<GenerationForecast> forecasts = retriever.retrieve();
        // Retrieve the id for any existing forecast for the same period so that it gets updated in the database,
        // rather than inserted.
        // Not particularly efficient, given that each forecast is retrieved individually from the database.
        // Spring Data JDBC doesn't provide a mechanism to write queries that take collections as parameters.
        forecasts.forEach(forecast -> {
            GenerationForecast existingForecast = repository.findByPeriodEnd(forecast.getPeriod_end());
            if (existingForecast != null) {
                forecast.setId(existingForecast.getId());
            }
        });
        repository.saveAll(forecasts);
    }
}
