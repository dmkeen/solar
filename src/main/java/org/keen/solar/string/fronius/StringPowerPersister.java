package org.keen.solar.string.fronius;

import org.keen.solar.string.dal.StringPowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

public class StringPowerPersister {

    @Autowired
    private StringPowerRetriever retriever;

    @Autowired
    private StringPowerRepository repository;

    @Async
    @Scheduled(cron = "15 0/5 * * * *")
    public void retrieveAndPersist() {
        repository.save(retriever.getLatest());
    }
}
