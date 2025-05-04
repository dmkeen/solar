package org.keen.solar.system.fronius;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keen.solar.config.TestConfiguration;
import org.keen.solar.system.domain.CurrentPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

/**
 * Calls the actual Fronius inverter API
 */
@ContextConfiguration(classes = {TestConfiguration.class, CurrentPowerRetriever.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "/application.properties")
public class CurrentPowerRetrieverIT {

    @Autowired CurrentPowerRetriever retriever;

    @Test
    public void givenInverterIsOnline_whenRetrieve_thenCurrentPowerReturned() {
        CurrentPower currentPower = retriever.retrieve();

        Assert.state(currentPower.getEpochTimestamp() > 0, "Expected epoch timestamp to be populated");
        Assert.state(Math.abs(0 - currentPower.getConsumption()) > 1, "Expected consumption to be greater than 1");
    }
}
