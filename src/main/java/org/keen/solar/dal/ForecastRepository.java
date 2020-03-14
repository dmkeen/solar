package org.keen.solar.dal;

import org.keen.solar.domain.GenerationForecast;
import org.springframework.data.repository.CrudRepository;

public interface ForecastRepository extends CrudRepository<GenerationForecast, Long> {
}
