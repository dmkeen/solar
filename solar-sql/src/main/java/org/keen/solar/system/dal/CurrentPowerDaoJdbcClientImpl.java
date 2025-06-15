package org.keen.solar.system.dal;

import org.keen.solar.system.domain.CurrentPower;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CurrentPowerDaoJdbcClientImpl implements CurrentPowerDao {

    private final JdbcClient jdbcClient;

    public CurrentPowerDaoJdbcClientImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<CurrentPower> getStartingFrom(long fromEpochSeconds) {
        return jdbcClient.sql("SELECT * FROM current_power WHERE epoch_timestamp >= :fromEpochSeconds")
                .param("fromEpochSeconds", fromEpochSeconds)
                .query((rs, rowNum) -> new CurrentPower(
                        rs.getLong("epoch_timestamp"),
                        rs.getDouble("generation"),
                        rs.getDouble("consumption")
                ))
                .list();
    }

    @Override
    public void save(CurrentPower currentPower) {
        jdbcClient.sql("""
            INSERT INTO current_power (epoch_timestamp, generation, consumption)
            VALUES (:epoch_timestamp, :generation, :consumption)
            """)
                .param("epoch_timestamp", currentPower.epochTimestamp())
                .param("generation", currentPower.generation())
                .param("consumption", currentPower.consumption())
                .update();
    }

}