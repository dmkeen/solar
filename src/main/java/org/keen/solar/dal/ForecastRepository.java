package org.keen.solar.dal;

import org.keen.solar.domain.GenerationForecast;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ForecastRepository extends CrudRepository<GenerationForecast, Long> {

    @Query("SELECT id, pv_estimate, pv_estimate10, pv_estimate90, period_end, period_end_epoch, period" +
            " FROM generation_forecast f" +
            " WHERE f.period_end = :periodEnd")
    GenerationForecast findByPeriodEnd(String periodEnd);
}
