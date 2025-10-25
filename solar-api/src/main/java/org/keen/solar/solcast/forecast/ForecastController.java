package org.keen.solar.solcast.forecast;

import org.keen.solar.solcast.forecast.dal.ForecastDao;
import org.keen.solar.solcast.forecast.domain.GenerationForecast;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ForecastController {

    private final ForecastDao forecastDao;

    public ForecastController(ForecastDao forecastDao) {
        this.forecastDao = forecastDao;
    }

    @GetMapping("/forecast")
    public List<GenerationForecast> getForecasts(@RequestParam long fromEpochSeconds,
                                                 @RequestParam long toEpochSeconds) {
        if (toEpochSeconds < fromEpochSeconds) {
            LoggerFactory.getLogger(ForecastController.class)
                    .warn("End time ({}) less than start time ({})", toEpochSeconds, fromEpochSeconds);
            return List.of();
        }

        return forecastDao.getForecasts(fromEpochSeconds, toEpochSeconds);
    }
}
