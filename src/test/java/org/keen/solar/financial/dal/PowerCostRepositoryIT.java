package org.keen.solar.financial.dal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keen.solar.financial.domain.PowerCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Tests the PowerCostRepository using an in-memory database (configured in /test/resources/application.properties).
 * <br/>
 * Setting spring.test.database.replace=none prevents @DataJdbcTest from configuring its own in-memory database
 * (which wouldn't have its schema setup by /test/resources/schema-h2.sql).
 */
@DataJdbcTest(properties = "spring.test.database.replace=none")
public class PowerCostRepositoryIT {

    @Autowired
    private PowerCostRepository repository;

    @Test
    public void givenSavedPowerCost_whenFindById_thenPowerCostReturned() {
        // Given
        PowerCost powerCost = new PowerCost(BigDecimal.valueOf(20711,12),
                1730493120, 60);
        repository.save(powerCost);

        // When
        Optional<PowerCost> optionalPowerCost = repository.findById(1L);

        // Then
        Assertions.assertTrue(optionalPowerCost.isPresent());
        PowerCost actual = optionalPowerCost.get();
        Assertions.assertEquals(powerCost.getCost(), actual.getCost());
        Assertions.assertEquals(powerCost.getPeriodEndEpoch(), actual.getPeriodEndEpoch());
        Assertions.assertEquals(powerCost.getPeriodLengthSeconds(), actual.getPeriodLengthSeconds());
    }
}
