package org.keen.solar.system.dal;

import org.keen.solar.system.domain.CurrentPower;

import java.util.List;

public interface CurrentPowerDao {

    /**
     * Returns all CurrentPowers starting from the given epoch second (inclusive).
     */
    List<CurrentPower> getStartingFrom(long fromEpochSeconds);

    /**
     * Returns all the CurrentPowers for the given period.
     *
     * @param fromEpochSeconds start time, in seconds since the epoch (inclusive)
     * @param toEpochSeconds   end time, in seconds since the epoch (exclusive)
     * @return list of CurrentPower
     */
    List<CurrentPower> getCurrentPowers(long fromEpochSeconds, long toEpochSeconds);

    /**
     * Persists the given CurrentPower to the repository.
     */
    void save(CurrentPower currentPower);

}
