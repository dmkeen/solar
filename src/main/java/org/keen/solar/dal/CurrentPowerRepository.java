package org.keen.solar.dal;

import org.keen.solar.domain.CurrentPower;
import org.springframework.data.repository.CrudRepository;

public interface CurrentPowerRepository extends CrudRepository<CurrentPower, Long> {
}
