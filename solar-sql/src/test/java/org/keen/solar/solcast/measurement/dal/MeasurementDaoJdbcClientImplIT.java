package org.keen.solar.solcast.measurement.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Sql(scripts = "/schema-h2.sql")
@Transactional
class MeasurementDaoJdbcClientImplIT {

    @Autowired
    private DataSource dataSource;
    private JdbcClient jdbcClient;

    @BeforeEach
    void setUp() {
        jdbcClient = JdbcClient.create(dataSource);

        // Clear the table before each test
        jdbcClient.sql("DELETE FROM measurement_upload").update();
    }

    @Test
    void givenNoUploads_whenGetLastUploadedEpochTimestamp_thenReturnsZero() {
        // Given
        MeasurementDaoJdbcClientImpl measurementDao = new MeasurementDaoJdbcClientImpl(jdbcClient);

        // When
        long lastUploaded = measurementDao.getLastUploadedEpochTimestamp();

        // Then
        assertEquals(0L, lastUploaded);
    }

    @Test
    void givenLastUploadedEpochTimestamp_whenSetAndGet_thenReturnsCorrectValue() {
        // Given
        MeasurementDaoJdbcClientImpl measurementDao = new MeasurementDaoJdbcClientImpl(jdbcClient);
        long epochSeconds = 1700000000L;

        // When
        measurementDao.setLastUploadedEpochTimestamp(epochSeconds);
        long lastUploaded = measurementDao.getLastUploadedEpochTimestamp();

        // Then
        assertEquals(epochSeconds, lastUploaded);
    }
}
