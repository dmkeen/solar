package org.keen.solar.financial.dal;

import org.keen.solar.financial.domain.PowerCost;

public interface PowerCostDao {

    /**
     * Persists the given PowerCost to the repository.
     */
    void save(PowerCost powerCost);
}
