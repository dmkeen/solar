package org.keen.solar.string.dal;

import org.keen.solar.string.domain.StringPower;
import org.springframework.stereotype.Component;

@Component
public class StringPowerDaoSpringDataImpl implements StringPowerDao {

    private final StringPowerRepository repository;

    public StringPowerDaoSpringDataImpl(StringPowerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(StringPower stringPower) {
        repository.save(stringPower);
    }
}
