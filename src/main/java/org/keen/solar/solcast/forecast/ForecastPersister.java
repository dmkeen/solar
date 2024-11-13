package org.keen.solar.solcast.forecast;

import org.keen.solar.solcast.forecast.dal.ForecastRepository;
import org.keen.solar.solcast.forecast.domain.GenerationForecast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Retrieves the solar panel output forecasts and persists them
 */
@Profile("!test")
@Service
public class ForecastPersister {

    @Autowired
    private ForecastRetriever retriever;

    @Autowired
    private ForecastRepository repository;

    @Async
    @Scheduled(cron = "${app.solcast.forecast-retrieval-cron}")
    public void retrieveAndPersistAsync() {
        retrieveAndPersist();
    }

    public void retrieveAndPersist() {
        List<GenerationForecast> forecasts = retriever.retrieve();
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
