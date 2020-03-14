package org.keen.solar.solcast;

import org.keen.solar.dal.ForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

public class ForecastPersister {

    @Autowired
    private ForecastRetriever retriever;

    @Autowired
    private ForecastRepository repository;

    @Scheduled(cron = "${app.solcast.forecast-retrieval-cron}")
    public void retrieveAndPersist() {
        repository.saveAll(retriever.retrieve());
    }
}
