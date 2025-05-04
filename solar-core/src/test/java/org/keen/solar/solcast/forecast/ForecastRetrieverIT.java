package org.keen.solar.solcast.forecast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keen.solar.config.TestConfiguration;
import org.keen.solar.solcast.forecast.domain.GenerationForecast;
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
        Assert.state(forecasts.get(0).getPeriod_length_seconds() > 0, "Expected period length to be populated");
        Assert.state(forecasts.get(0).getPeriod_end_epoch() > 0, "Expected period end epoch to be populated");
        Assert.state(forecasts.get(0).getPv_estimate() > 0, "Expected pv estimate to be populated");
        Assert.state(forecasts.get(0).getPv_estimate10() > 0, "Expected pv estimate 10 to be populated");
        Assert.state(forecasts.get(0).getPv_estimate90() > 0, "Expected pv estimate 90 to be populated");
    }
}
