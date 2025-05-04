package org.keen.solar.string.dal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keen.solar.string.domain.StringPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Tests the StringPowerRepository using an in-memory database (configured in /test/resources/application.properties).
 * <br/>
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
        StringPower stringPower = new StringPower(now.toEpochSecond(), 300,
                BigDecimal.valueOf(100), BigDecimal.valueOf(2), BigDecimal.valueOf(200), BigDecimal.valueOf(1.5));

        // When
        StringPower savedStringPower = repository.save(stringPower);

        // Then
        Assertions.assertNotNull(savedStringPower.getId());
    }
}
