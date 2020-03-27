package org.keen.solar.sources.fronius;

import org.keen.solar.dal.CurrentPowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Retrieves the current power values and persists them
 */
public class CurrentPowerPersister {

    @Autowired
    private CurrentPowerRetriever retriever;

    @Autowired
    private CurrentPowerRepository repository;

    @Scheduled(fixedRateString = "${app.current-power.sample-rate-ms}")
    @Async
    public void retrieveAndPersist() {
        repository.save(retriever.retrieve());
    }
}
