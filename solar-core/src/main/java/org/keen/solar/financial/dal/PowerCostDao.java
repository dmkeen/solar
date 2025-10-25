package org.keen.solar.financial.dal;

import org.keen.solar.financial.domain.PowerCost;

import java.util.List;

public interface PowerCostDao {

    /**
     * Persists the given PowerCost to the repository.
     */
    void save(PowerCost powerCost);

    /**
     * Retrieves the PowerCosts for the given period.
     *
     * @param fromEpochTime start time, in seconds since the epoch (inclusive)
     * @param toEpochTime   end time, in seconds since the epoch (exclusive)
     * @return list of PowerCosts, possibly empty
     */
    List<PowerCost> getPowerCosts(long fromEpochTime, long toEpochTime);
}
