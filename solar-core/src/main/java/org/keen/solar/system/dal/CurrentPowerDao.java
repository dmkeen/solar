package org.keen.solar.system.dal;

import org.keen.solar.system.domain.CurrentPower;

import java.util.List;

public interface CurrentPowerDao {

    /**
     * Returns all CurrentPowers starting from the given epoch second (inclusive).
     */
    List<CurrentPower> getStartingFrom(long fromEpochSeconds);

    /**
     * Persists the given CurrentPower to the repository.
     */
    void save(CurrentPower currentPower);

}
