package org.keen.solar.sources.solcast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keen.solar.TestConfiguration;
import org.keen.solar.domain.GenerationForecast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Calls the actual Solcast forecast API. Note that there is a limited number of calls allowed per day.
 */
@ContextConfiguration(classes = {TestConfiguration.class, ForecastRetriever.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "/application.properties")
public class ForecastRetrieverIT {

    @Autowired
    private ForecastRetriever retriever;

    @Test
    public void givenApiIsOnline_whenRetrieve_thenForecastReturned() {
        List<GenerationForecast> forecasts = retriever.retrieve();

        Assert.notEmpty(forecasts, "Expected forecasts to be returned");
        Assert.notNull(forecasts.get(0).getPeriod(), "Expected period to be populated");
        Assert.notNull(forecasts.get(0).getPeriod_end(), "Expected period end to be populated");
        Assert.notNull(forecasts.get(0).getPeriod_end_epoch(), "Expected period end epoch to be populated");
        Assert.notNull(forecasts.get(0).getPv_estimate(), "Expected pv estimate to be populated");
        Assert.notNull(forecasts.get(0).getPv_estimate10(), "Expected pv estimate 10 to be populated");
        Assert.notNull(forecasts.get(0).getPv_estimate90(), "Expected pv estimate 90 to be populated");
    }
}
