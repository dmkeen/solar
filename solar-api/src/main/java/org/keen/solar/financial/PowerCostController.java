package org.keen.solar.financial;

import org.keen.solar.financial.dal.PowerCostDao;
import org.keen.solar.financial.domain.PowerCost;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class PowerCostController {

    private final PowerCostDao powerCostDao;

    public PowerCostController(PowerCostDao powerCostDao) {
        this.powerCostDao = powerCostDao;
    }

    @GetMapping("/cost")
    public BigDecimal getCost(@RequestParam long fromEpochSeconds,
                              @RequestParam long toEpochSeconds) {
        if (toEpochSeconds < fromEpochSeconds) {
            LoggerFactory.getLogger(PowerCostController.class)
                    .warn("End time ({}) less than start time ({})", toEpochSeconds, fromEpochSeconds);
            return BigDecimal.ZERO;
        }

        List<PowerCost> powerCosts = powerCostDao.getPowerCosts(fromEpochSeconds, toEpochSeconds);
        return powerCosts.stream()
                .map(PowerCost::cost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
