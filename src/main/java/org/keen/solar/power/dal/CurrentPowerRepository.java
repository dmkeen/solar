package org.keen.solar.power.dal;

import org.keen.solar.power.domain.CurrentPower;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CurrentPowerRepository extends CrudRepository<CurrentPower, Long> {

    List<CurrentPower> findByUploaded(boolean uploaded);

    List<CurrentPower> findByUploadedAndInverterEpochTimestampBetween(boolean uploaded, long start, long end);
}
