package org.keen.solar.solcast.forecast.dal;

import org.keen.solar.solcast.forecast.domain.GenerationForecast;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ForecastDaoJdbcClientImpl implements ForecastDao {

    private final JdbcClient jdbcClient;

    public ForecastDaoJdbcClientImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void save(Collection<GenerationForecast> forecasts) {
        for (GenerationForecast forecast : forecasts) {
            jdbcClient.sql("""
            INSERT INTO generation_forecast (period_end_epoch, pv_estimate, pv_estimate10, pv_estimate90, period_length_seconds)
            VALUES (:period_end_epoch, :pv_estimate, :pv_estimate10, :pv_estimate90, :period_length_seconds)
            ON DUPLICATE KEY UPDATE
                pv_estimate = VALUES(pv_estimate),
                pv_estimate10 = VALUES(pv_estimate10),
                pv_estimate90 = VALUES(pv_estimate90),
                period_length_seconds = VALUES(period_length_seconds)
            """)
                    .param("period_end_epoch", forecast.period_end_epoch())
                    .param("pv_estimate", forecast.pv_estimate())
                    .param("pv_estimate10", forecast.pv_estimate10())
                    .param("pv_estimate90", forecast.pv_estimate90())
                    .param("period_length_seconds", forecast.period_length_seconds())
                    .update();
        }
    }
}
