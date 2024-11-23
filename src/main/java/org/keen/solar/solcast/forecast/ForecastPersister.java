package org.keen.solar.solcast.forecast;

import org.keen.solar.solcast.forecast.dal.ForecastDao;
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
    private ForecastDao repository;

    @Async
    @Scheduled(cron = "${app.solcast.forecast-retrieval-cron}")
    public void retrieveAndPersistAsync() {
        retrieveAndPersist();
    }

    public void retrieveAndPersist() {
        List<GenerationForecast> forecasts = retriever.retrieve();
        repository.save(forecasts);
    }

}
