package org.keen.solar.system;

import org.keen.solar.system.dal.CurrentPowerDao;
import org.keen.solar.system.domain.CurrentPower;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
public class CurrentPowerController {

    private final CurrentPowerDao currentPowerDao;

    public CurrentPowerController(CurrentPowerDao currentPowerDao) {
        this.currentPowerDao = currentPowerDao;
    }

    @GetMapping("/current-power")
    public List<CurrentPower> getCurrentPowers(@RequestParam long fromEpochSeconds,
                                               @RequestParam long toEpochSeconds) {
        if (toEpochSeconds < fromEpochSeconds) {
            logInvalidParams(fromEpochSeconds, toEpochSeconds);
            return List.of();
        }

        return currentPowerDao.getCurrentPowers(fromEpochSeconds, toEpochSeconds);
    }

    private static void logInvalidParams(long fromEpochSeconds, long toEpochSeconds) {
        LoggerFactory.getLogger(CurrentPowerController.class)
                .warn("End time ({}) less than start time ({})", toEpochSeconds, fromEpochSeconds);
    }

    @GetMapping("/current-power-surplus")
    public double getCurrentPowerSurplus(@RequestParam long fromEpochSeconds,
                                         @RequestParam long toEpochSeconds) {
        if (toEpochSeconds < fromEpochSeconds) {
            logInvalidParams(fromEpochSeconds, toEpochSeconds);
            return 0;
        }

        Optional<CurrentPower> max = currentPowerDao.getCurrentPowers(fromEpochSeconds, toEpochSeconds)
                .stream()
                .max(Comparator.comparing(CurrentPower::epochTimestamp));
        if (max.isPresent()) {
            CurrentPower currentPower = max.get();
            return currentPower.generation() + currentPower.consumption();
        }
        return 0;
    }

    @GetMapping("/stats")
    public PowerStats getStats(@RequestParam long fromEpochSeconds,
                               @RequestParam long toEpochSeconds) {
        if (toEpochSeconds < fromEpochSeconds) {
            logInvalidParams(fromEpochSeconds, toEpochSeconds);
            return new PowerStats(0, 0, 0, 0);
        }

        List<CurrentPower> currentPowers = currentPowerDao.getCurrentPowers(fromEpochSeconds, toEpochSeconds);
        double totalGeneration = 0;
        double totalConsumption = 0;
        double totalExported = 0;
        double totalImported = 0;
        for (CurrentPower currentPower : currentPowers) {
            totalGeneration += currentPower.generation();
            totalConsumption += currentPower.consumption();
            double balance = currentPower.generation() + currentPower.consumption();
            if (balance < 0) {
                totalImported += balance;
            } else {
                totalExported += balance;
            }
        }
        return new PowerStats(totalGeneration, totalConsumption, totalExported, totalImported);
    }
}
