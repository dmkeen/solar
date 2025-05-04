package org.keen.solar.string.dal;

import org.keen.solar.string.domain.StringPower;

public interface StringPowerDao {

    /**
     * Persists the given StringPower to the repository.
     */
    void save(StringPower stringPower);
}
