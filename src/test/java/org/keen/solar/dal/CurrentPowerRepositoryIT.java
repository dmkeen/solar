package org.keen.solar.dal;

import org.junit.jupiter.api.Test;
import org.keen.solar.domain.CurrentPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@SpringBootTest
public class CurrentPowerRepositoryIT {

    @Autowired
    private CurrentPowerRepository repository;

    @Test
    public void givenCurrentPower_whenSave_thenSavedToRepository() {
        // Given
        CurrentPower currentPower = new CurrentPower(LocalDateTime.now(), 1000D, 500.45D);

        // When
        CurrentPower savedCurrentPower = repository.save(currentPower);

        // Then
        Assert.notNull(savedCurrentPower.getId(), "Expected id to be generated");
    }
}
