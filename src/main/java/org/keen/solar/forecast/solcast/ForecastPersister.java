package org.keen.solar.forecast.solcast;

import org.keen.solar.forecast.dal.ForecastRepository;
import org.keen.solar.forecast.domain.GenerationForecast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Retrieves the solar panel output forecasts and persists them
 */
@RestController
public class ForecastPersister {

    @Autowired
    private ForecastRetriever retriever;

    @Autowired
    private ForecastRepository repository;

    @PostMapping("/forecast/retrieve")
    @Scheduled(cron = "${app.solcast.forecast-retrieval-cron}")
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
