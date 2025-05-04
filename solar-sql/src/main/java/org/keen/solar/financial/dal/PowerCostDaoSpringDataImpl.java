package org.keen.solar.financial.dal;

import org.keen.solar.financial.dal.PowerCostDao;
import org.keen.solar.financial.domain.PowerCost;
import org.springframework.stereotype.Component;

@Component
public class PowerCostDaoSpringDataImpl implements PowerCostDao {

    private final PowerCostRepository repository;

    public PowerCostDaoSpringDataImpl(PowerCostRepository repository) {
        this.repository = repository;
    }


    @Override
    public void save(PowerCost powerCost) {
        repository.save(powerCost);
    }
}
