package org.keen.solar.financial.dal;

import org.keen.solar.financial.domain.PowerCost;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PowerCostDaoJdbcClientImpl implements PowerCostDao {

    private final JdbcClient jdbcClient;

    public PowerCostDaoJdbcClientImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void save(PowerCost powerCost) {
        jdbcClient.sql("INSERT INTO power_cost (cost, period_end_epoch, period_length_seconds) VALUES (?, ?, ?)")
                .param(1, powerCost.cost())
                .param(2, powerCost.periodEndEpoch())
                .param(3, powerCost.periodLengthSeconds())
                .update();
    }

    @Override
    public List<PowerCost> getPowerCosts(long fromEpochTime, long toEpochTime) {
        return jdbcClient.sql("SELECT * from power_cost WHERE period_end_epoch >= :fromEpochTime AND period_end_epoch < :toEpochTime")
                .param("fromEpochTime", fromEpochTime)
                .param("toEpochTime", toEpochTime)
                .query((rs, rowNum) -> new PowerCost(rs.getBigDecimal("cost"),
                        rs.getLong("period_end_epoch"), rs.getInt("period_length_seconds")))
                .list();
    }
}
