package org.keen.solar.solcast.forecast.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keen.solar.solcast.forecast.domain.GenerationForecast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "/schema-h2.sql")
@Transactional
public class ForecastDaoJdbcClientImplIT {

    @Autowired
    private DataSource dataSource;
    private ForecastDaoJdbcClientImpl forecastDaoJdbcClient;
    private JdbcClient jdbcClient;

    @BeforeEach
    public void setUp() {
        jdbcClient = JdbcClient.create(dataSource);
        forecastDaoJdbcClient = new ForecastDaoJdbcClientImpl(jdbcClient);

        // Clear the table before each test
        jdbcClient.sql("DELETE FROM generation_forecast").update();
    }

    @Test
    public void givenNewForecasts_whenSave_thenInserted() {
        // Given
        GenerationForecast forecast1 = new GenerationForecast(2.0, 1.0, 3.0, 1730493120L, 1800);
        GenerationForecast forecast2 = new GenerationForecast(4.0, 2.0, 6.0, 1730493180L, 1800);

        // When
        forecastDaoJdbcClient.save(List.of(forecast1, forecast2));

        // Then
        List<GenerationForecast> results = jdbcClient.sql("SELECT * FROM generation_forecast")
                .query((rs, rowNum) -> new GenerationForecast(
                        rs.getDouble("pv_estimate"),
                        rs.getDouble("pv_estimate10"),
                        rs.getDouble("pv_estimate90"),
                        rs.getLong("period_end_epoch"),
                        rs.getInt("period_length_seconds")
                ))
                .list();
        assertEquals(2, results.size());
    }

    @Test
    public void givenExistingForecast_whenSave_thenUpdated() {
        // Given
        jdbcClient.sql("""
            INSERT INTO generation_forecast (period_end_epoch, pv_estimate, pv_estimate10, pv_estimate90, period_length_seconds)
            VALUES (1730493120, 2.0, 1.0, 3.0, 1800)
        """).update();

        GenerationForecast updatedForecast = new GenerationForecast(5.0, 2.5, 7.5, 1730493120L, 3600);

        // When
        forecastDaoJdbcClient.save(List.of(updatedForecast));

        // Then
        GenerationForecast result = jdbcClient.sql("SELECT * FROM generation_forecast WHERE period_end_epoch = ?")
                .param(1, 1730493120L)
                .query(rs -> {
                    if (rs.next()) {
                        return new GenerationForecast(
                                rs.getDouble("pv_estimate"),
                                rs.getDouble("pv_estimate10"),
                                rs.getDouble("pv_estimate90"),
                                rs.getLong("period_end_epoch"),
                                rs.getInt("period_length_seconds")
                        );
                    }
                    return null;
                });

        assertNotNull(result);
        assertEquals(5.0, result.pv_estimate());
        assertEquals(2.5, result.pv_estimate10());
        assertEquals(7.5, result.pv_estimate90());
        assertEquals(3600, result.period_length_seconds());
    }
}