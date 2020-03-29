package org.keen.solar.power.dal;

import org.keen.solar.power.domain.CurrentPower;
import org.springframework.data.repository.CrudRepository;

public interface CurrentPowerRepository extends CrudRepository<CurrentPower, Long> {
}
