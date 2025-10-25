package org.keen.solar.string;

import org.keen.solar.string.dal.StringPowerDao;
import org.keen.solar.string.domain.StringPower;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StringPowerController {

    private final StringPowerDao stringPowerDao;

    public StringPowerController(StringPowerDao stringPowerDao) {
        this.stringPowerDao = stringPowerDao;
    }

    @GetMapping("/string-power")
    public List<StringPower> getStringPowers(@RequestParam long fromEpochSeconds,
                                             @RequestParam long toEpochSeconds) {
        if (toEpochSeconds < fromEpochSeconds) {
            LoggerFactory.getLogger(StringPowerController.class)
                    .warn("End time ({}) less than start time ({})", toEpochSeconds, fromEpochSeconds);
            return List.of();
        }

        return stringPowerDao.getStringPowers(fromEpochSeconds, toEpochSeconds);
    }
}
