package org.keen.solar.system.fronius;

import org.keen.solar.system.dal.CurrentPowerRepository;
import org.keen.solar.system.domain.CurrentPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Async
    @Scheduled(fixedRateString = "${app.current-power.sample-rate-ms}")
    public void retrieveAndPersist() {
        CurrentPower currentPower = retriever.retrieve();
        repository.save(currentPower);
        eventPublisher.publishEvent(currentPower);
    }
}
