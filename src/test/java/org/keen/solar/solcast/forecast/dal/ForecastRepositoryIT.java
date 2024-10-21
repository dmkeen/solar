package org.keen.solar.solcast.forecast.dal;

import org.junit.jupiter.api.Test;
import org.keen.solar.solcast.forecast.dal.ForecastRepository;
import org.keen.solar.solcast.forecast.domain.GenerationForecast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.util.Assert;

import java.time.OffsetDateTime;

/**
 * Tests the ForecastRepository using an in-memory database (configured in /test/resources/application.properties).
 *
 * Setting spring.test.database.replace=none prevents @DataJdbcTest from configuring its own in-memory database
 * (which wouldn't have its schema setup by /test/resources/schema-h2.sql).
 */
@DataJdbcTest(properties = "spring.test.database.replace=none")
public class ForecastRepositoryIT {

    @Autowired
    private ForecastRepository repository;

    @Test
    public void givenCurrentPower_whenSave_thenSavedToRepository() {
        // Given
        OffsetDateTime currentTime = OffsetDateTime.now();
        GenerationForecast forecast = new GenerationForecast(2D, 1D, 3D, currentTime.toEpochSecond(), 30*60);

        // When
        GenerationForecast savedForecast = repository.save(forecast);

        // Then
        Assert.notNull(savedForecast.getId(), "Expected id to be generated");
    }
}
