package org.keen.solar.fronius;

import org.junit.jupiter.api.Test;
import org.keen.solar.domain.CurrentPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class CurrentPowerRetrieverIT {

    @Autowired CurrentPowerRetriever retriever;

    @Test
    public void givenInverterIsOnline_whenRetrieve_thenCurrentPowerReturned() {
        CurrentPower currentPower = retriever.retrieve();

        Assert.notNull(currentPower.getTimestamp(), "Expected timestamp to be populated");
        Assert.state(Math.abs(0 - currentPower.getConsumption()) > 1, "Expected consumption to be greater than 1");
    }
}
