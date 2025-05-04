package org.keen.solar.solcast.forecast.dal;

import org.keen.solar.solcast.forecast.domain.GenerationForecast;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

public interface ForecastRepository extends CrudRepository<GenerationForecast, Long> {

    @Query("SELECT id, pv_estimate, pv_estimate10, pv_estimate90, period_end_epoch, period_length_seconds" +
            " FROM generation_forecast f" +
            " WHERE f.period_end_epoch = :periodEndEpoch")
    GenerationForecast findByPeriodEnd(long periodEndEpoch);
}
