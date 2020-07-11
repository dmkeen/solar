package org.keen.solar.power.dal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;
import org.keen.solar.power.domain.CurrentPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.util.Assert;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Tests the CurrentPowerRepository using an in-memory database (configured in /test/resources/application.properties).
 *
 * Setting spring.test.database.replace=none prevents @DataJdbcTest from configuring its own in-memory database
 * (which wouldn't have its schema setup by /test/resources/schema-h2.sql).
 */
@DataJdbcTest(properties = "spring.test.database.replace=none")
public class CurrentPowerRepositoryIT {

    @Autowired
    private CurrentPowerRepository repository;

    @Test
    public void givenCurrentPower_whenSave_thenSavedToRepository() {
        // Given
        OffsetDateTime currentTime = OffsetDateTime.now();
        CurrentPower currentPower = new CurrentPower(currentTime.toString(), currentTime.toEpochSecond(), currentTime.toString(), 1000D, 500.45D, false);

        // When
        CurrentPower savedCurrentPower = repository.save(currentPower);

        // Then
        Assert.notNull(savedCurrentPower.getId(), "Expected id to be generated");
    }

    @Test
    public void givenCurrentPowerSaved_whenFindByNotUploaded_thenNonUploadedCurrentPowerReturned() {
        // Given
        OffsetDateTime currentTime = OffsetDateTime.now();
        CurrentPower currentPower1 = new CurrentPower(currentTime.toString(), currentTime.toEpochSecond(), currentTime.toString(), 1000D, 500.45D, false);
        currentTime = currentTime.plusSeconds(1);
        CurrentPower currentPower2 = new CurrentPower(currentTime.toString(), currentTime.toEpochSecond(), currentTime.toString(), 2000D, 600.45D, false);
        currentTime = currentTime.plusSeconds(1);
        CurrentPower currentPower3 = new CurrentPower(currentTime.toString(), currentTime.toEpochSecond(), currentTime.toString(), 3000D, 700.45D, false);
        currentTime = currentTime.plusSeconds(1);
        CurrentPower currentPower4 = new CurrentPower(currentTime.toString(), currentTime.toEpochSecond(), currentTime.toString(), 4000D, 800.45D, true);

        repository.save(currentPower1);
        repository.save(currentPower2);
        repository.save(currentPower3);
        repository.save(currentPower4);

        // When
        List<CurrentPower> notUploaded = repository.findByUploaded(false);

        // Then
        Assert.state(EqualsBuilder.reflectionEquals(currentPower1, notUploaded.get(0)), "Expected currentPower1 to be in the list");
        Assert.state(EqualsBuilder.reflectionEquals(currentPower2, notUploaded.get(1)), "Expected currentPower2 to be in the list");
        Assert.state(EqualsBuilder.reflectionEquals(currentPower3, notUploaded.get(2)), "Expected currentPower3 to be in the list");
        Assert.state(notUploaded.size() == 3, "Expected currentPower4 to not be in the list");
    }

    @Test
    public void givenCurrentPowerSaved_whenFindByUploaded_thenUploadedCurrentPowerReturned() {
        // Given
        OffsetDateTime currentTime = OffsetDateTime.now();
        CurrentPower currentPower1 = new CurrentPower(currentTime.toString(), currentTime.toEpochSecond(), currentTime.toString(), 1000D, 500.45D, false);
        currentTime = currentTime.plusSeconds(1);
        CurrentPower currentPower2 = new CurrentPower(currentTime.toString(), currentTime.toEpochSecond(), currentTime.toString(), 2000D, 600.45D, false);
        currentTime = currentTime.plusSeconds(1);
        CurrentPower currentPower3 = new CurrentPower(currentTime.toString(), currentTime.toEpochSecond(), currentTime.toString(), 3000D, 700.45D, false);
        currentTime = currentTime.plusSeconds(1);
        CurrentPower currentPower4 = new CurrentPower(currentTime.toString(), currentTime.toEpochSecond(), currentTime.toString(), 4000D, 800.45D, true);

        repository.save(currentPower1);
        repository.save(currentPower2);
        repository.save(currentPower3);
        repository.save(currentPower4);

        // When
        List<CurrentPower> notUploaded = repository.findByUploaded(true);

        // Then
        Assert.state(EqualsBuilder.reflectionEquals(currentPower4, notUploaded.get(0)), "Expected currentPower4 to be in the list");
        Assert.state(notUploaded.size() == 1, "Only expected currentPower4 to be in the list");
    }

}
