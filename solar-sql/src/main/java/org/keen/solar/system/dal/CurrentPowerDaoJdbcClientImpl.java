package org.keen.solar.system.dal;

import org.keen.solar.system.domain.CurrentPower;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class CurrentPowerDaoJdbcClientImpl implements CurrentPowerDao {

    private final JdbcClient jdbcClient;

    public CurrentPowerDaoJdbcClientImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<CurrentPower> getNotUploaded() {
        return jdbcClient.sql("SELECT * FROM current_power WHERE uploaded = false")
                .query((rs, rowNum) -> new CurrentPower(
                        rs.getLong("epoch_timestamp"),
                        rs.getDouble("generation"),
                        rs.getDouble("consumption"),
                        rs.getBoolean("uploaded")
                ))
                .list();
    }

    @Override
    public void save(CurrentPower currentPower) {
        jdbcClient.sql("""
            INSERT INTO current_power (epoch_timestamp, generation, consumption, uploaded)
            VALUES (:epoch_timestamp, :generation, :consumption, :uploaded)
            """)
                .param("epoch_timestamp", currentPower.getEpochTimestamp())
                .param("generation", currentPower.getGeneration())
                .param("consumption", currentPower.getConsumption())
                .param("uploaded", currentPower.isUploaded())
                .update();
    }

    @Override
    public void save(Collection<CurrentPower> currentPowers) {
        for (CurrentPower currentPower : currentPowers) {
            save(currentPower);
        }
    }
}