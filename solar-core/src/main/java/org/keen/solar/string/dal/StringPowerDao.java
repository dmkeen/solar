package org.keen.solar.string.dal;

import org.keen.solar.string.domain.StringPower;

import java.util.List;

public interface StringPowerDao {

    /**
     * Persists the given StringPower to the repository.
     */
    void save(StringPower stringPower);

    /**
     * Returns all the StringPowers for the given period.
     *
     * @param fromEpochSeconds start time, in seconds since the epoch (inclusive)
     * @param toEpochSeconds   end time, in seconds since the epoch (exclusive)
     * @return list of StringPower
     */
    List<StringPower> getStringPowers(long fromEpochSeconds, long toEpochSeconds);
}
