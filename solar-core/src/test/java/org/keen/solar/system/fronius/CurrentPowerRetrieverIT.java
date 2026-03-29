package org.keen.solar.system.fronius;

import org.junit.jupiter.api.Test;
import org.keen.solar.config.TestConfiguration;
import org.keen.solar.system.domain.CurrentPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.Assert;

/**
 * Calls the actual Fronius inverter API
 */
@SpringJUnitConfig(classes = {TestConfiguration.class, CurrentPowerRetriever.class})
@TestPropertySource(locations = "/application.properties")
public class CurrentPowerRetrieverIT {

    @Autowired CurrentPowerRetriever retriever;

    @Test
    public void givenInverterIsOnline_whenRetrieve_thenCurrentPowerReturned() {
        CurrentPower currentPower = retriever.retrieve();

        Assert.state(currentPower.epochTimestamp() > 0, "Expected epoch timestamp to be populated");
        Assert.state(Math.abs(0 - currentPower.consumption()) > 1, "Expected consumption to be greater than 1");
    }
}
