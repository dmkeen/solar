package org.keen.solar.system.dal;

import org.keen.solar.system.domain.CurrentPower;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
class CurrentPowerDaoSpringDataImpl implements CurrentPowerDao {

    private final CurrentPowerRepository repository;

    public CurrentPowerDaoSpringDataImpl(CurrentPowerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CurrentPower> getNotUploaded() {
        return repository.findByUploaded(false);
    }

    @Override
    public void save(CurrentPower currentPower) {
        repository.save(currentPower);
    }

    @Override
    public void save(Collection<CurrentPower> currentPowers) {
        repository.saveAll(currentPowers);
    }
}
