package org.keen.solar.power.dal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keen.solar.power.domain.StringPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * Tests the StringPowerRepository using an in-memory database (configured in /test/resources/application.properties).
 *
 * Setting spring.test.database.replace=none prevents @DataJdbcTest from configuring its own in-memory database
 * (which wouldn't have its schema setup by /test/resources/schema-h2.sql).
 */
@DataJdbcTest(properties = "spring.test.database.replace=none")
public class StringPowerRepositoryIT {

    @Autowired
    private StringPowerRepository repository;

    @Test
    public void givenStringPower_whenSave_thenSavedToRepository() {
        // Given
        OffsetDateTime now = OffsetDateTime.now();
        StringPower stringPower = new StringPower(now, now.toEpochSecond(), Duration.ofMinutes(5), 100D, 2D, 200D, 1.5D);

        // When
        StringPower savedStringPower = repository.save(stringPower);

        // Then
        Assertions.assertNotNull(savedStringPower.getId());
    }
}
