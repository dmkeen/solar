package org.keen.solar.system.dal;

import org.keen.solar.system.domain.CurrentPower;

import java.util.Collection;
import java.util.List;

public interface CurrentPowerDao {

    /**
     * Returns all CurrentPowers not yet uploaded to Solcast.
     */
    List<CurrentPower> getNotUploaded();

    /**
     * Persists the given CurrentPower to the repository.
     */
    void save(CurrentPower currentPower);

    /**
     * Persists the given CurrentPowers to the repository.
     */
    void save(Collection<CurrentPower> currentPowers);
}
