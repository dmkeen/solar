package org.keen.solar.dal;

import org.keen.solar.domain.GenerationForecast;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface ForecastRepository extends CrudRepository<GenerationForecast, Long> {

    @Query("SELECT id, pv_estimate, pv_estimate10, pv_estimate90, period_end, period" +
            " FROM generation_forecast f" +
            " WHERE f.period_end = :periodEnd")
    GenerationForecast findByPeriodEnd(LocalDateTime periodEnd);
}
