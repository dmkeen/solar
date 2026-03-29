package org.keen.solar.system.dal;

import org.keen.solar.system.domain.CurrentPower;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CurrentPowerDaoJdbcClientImpl implements CurrentPowerDao {

    private static final String EPOCH_TIMESTAMP_COLUMN = "epoch_timestamp";
    private static final String GENERATION_COLUMN = "generation";
    private static final String CONSUMPTION_COLUMN = "consumption";

    private final JdbcClient jdbcClient;

    public CurrentPowerDaoJdbcClientImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<CurrentPower> getStartingFrom(long fromEpochSeconds) {
        return jdbcClient.sql("SELECT * FROM current_power WHERE epoch_timestamp >= :fromEpochSeconds")
                .param("fromEpochSeconds", fromEpochSeconds)
                .query((rs, _) -> new CurrentPower(
                        rs.getLong(EPOCH_TIMESTAMP_COLUMN),
                        rs.getDouble(GENERATION_COLUMN),
                        rs.getDouble(CONSUMPTION_COLUMN)
                ))
                .list();
    }

    @Override
    public List<CurrentPower> getCurrentPowers(long fromEpochSeconds, long toEpochSeconds) {
        return jdbcClient.sql("""
                        SELECT * FROM current_power
                        WHERE epoch_timestamp >= :fromEpochSeconds
                        AND epoch_timestamp < :toEpochSeconds
                        """)
                .param("fromEpochSeconds", fromEpochSeconds)
                .param("toEpochSeconds", toEpochSeconds)
                .query((rs, _) -> new CurrentPower(
                        rs.getLong(EPOCH_TIMESTAMP_COLUMN),
                        rs.getDouble(GENERATION_COLUMN),
                        rs.getDouble(CONSUMPTION_COLUMN)
                ))
                .list();
    }

    @Override
    public void save(CurrentPower currentPower) {
        jdbcClient.sql("""
            INSERT INTO current_power (epoch_timestamp, generation, consumption)
            VALUES (:epochTimestamp, :generated, :consumed)
            """)
                .param("epochTimestamp", currentPower.epochTimestamp())
                .param("generated", currentPower.generation())
                .param("consumed", currentPower.consumption())
                .update();
    }

}