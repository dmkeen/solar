package org.keen.solar.solcast.forecast;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keen.solar.config.TestConfiguration;
import org.keen.solar.solcast.forecast.domain.GenerationForecast;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

        Assertions.assertFalse(forecasts.isEmpty(), "Expected forecasts to be returned");
        GenerationForecast forecast = forecasts.getFirst();
        Assertions.assertTrue(forecast.period_length_seconds() > 0, "Expected period length to be populated");
        Assertions.assertTrue(forecast.period_end_epoch() > 0, "Expected period end epoch to be populated");
        Assertions.assertTrue(forecast.pv_estimate() > 0, "Expected pv estimate to be populated");
        Assertions.assertTrue(forecast.pv_estimate10() > 0, "Expected pv estimate 10 to be populated");
        Assertions.assertTrue(forecast.pv_estimate90() > 0, "Expected pv estimate 90 to be populated");
    }
}
