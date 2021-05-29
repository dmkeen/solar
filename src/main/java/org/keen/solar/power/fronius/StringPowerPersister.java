package org.keen.solar.power.fronius;

import org.keen.solar.power.dal.StringPowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

public class StringPowerPersister {

    @Autowired
    private StringDataRetriever retriever;

    @Autowired
    private StringPowerRepository repository;

    @Scheduled(cron = "15 0/5 * * * *")
    @Async
    public void retrieveAndPersist() {
        repository.save(retriever.getLatest());
    }
}
