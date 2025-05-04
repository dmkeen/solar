package org.keen.solar.system.dal;

import org.keen.solar.system.domain.CurrentPower;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface CurrentPowerRepository extends CrudRepository<CurrentPower, Long> {

    List<CurrentPower> findByUploaded(boolean uploaded);

    List<CurrentPower> findByUploadedAndEpochTimestampBetween(boolean uploaded, long start, long end);
}
