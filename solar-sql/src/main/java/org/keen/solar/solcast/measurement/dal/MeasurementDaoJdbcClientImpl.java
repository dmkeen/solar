package org.keen.solar.solcast.measurement.dal;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class MeasurementDaoJdbcClientImpl implements MeasurementDao {

    private final JdbcClient jdbcClient;

    public MeasurementDaoJdbcClientImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public long getLastUploadedEpochTimestamp() {
        return jdbcClient.sql("SELECT last_uploaded_epoch from measurement_upload")
                .query(Long.class)
                .optional()
                .orElse(0L);
    }

    @Override
    public void setLastUploadedEpochTimestamp(long epochSeconds) {
        // Clear any existing records
        jdbcClient.sql("DELETE FROM measurement_upload")
                .update();

        jdbcClient.sql("INSERT INTO measurement_upload (last_uploaded_epoch) VALUES (:epochSeconds)")
                .param("epochSeconds", epochSeconds)
                .update();
    }
}
