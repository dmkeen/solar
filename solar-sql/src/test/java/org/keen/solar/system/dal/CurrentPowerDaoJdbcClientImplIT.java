package org.keen.solar.system.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keen.solar.system.domain.CurrentPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "/schema-h2.sql")
@Transactional
class CurrentPowerDaoJdbcClientImplIT {

    @Autowired
    private DataSource dataSource;

    private JdbcClient jdbcClient;
    private CurrentPowerDaoJdbcClientImpl currentPowerDaoJdbcClient;

    @BeforeEach
    void setUp() {
        jdbcClient = JdbcClient.create(dataSource);
        currentPowerDaoJdbcClient = new CurrentPowerDaoJdbcClientImpl(jdbcClient);
    }

    @Test
    void givenNewCurrentPower_whenSave_thenInsertedInDatabase() {
        // Given
        CurrentPower cp = new CurrentPower(1700000000L, 500.0, -200.0, false);

        // When
        currentPowerDaoJdbcClient.save(cp);

        // Then
        List<CurrentPower> results = jdbcClient.sql("SELECT * FROM current_power")
                .query((rs, rowNum) -> new CurrentPower(
                        rs.getLong("epoch_timestamp"),
                        rs.getDouble("generation"),
                        rs.getDouble("consumption"),
                        rs.getBoolean("uploaded")
                ))
                .list();

        assertEquals(1, results.size());
        CurrentPower result = results.getFirst();
        assertEquals(1700000000L, result.getEpochTimestamp());
        assertEquals(500.0, result.getGeneration());
        assertEquals(-200.0, result.getConsumption());
        assertFalse(result.isUploaded());
    }

    @Test
    void givenMultipleCurrentPowers_whenSaveAll_thenAllInserted() {
        // Given
        CurrentPower cp1 = new CurrentPower(1700000001L, 100.0, -50.0, false);
        CurrentPower cp2 = new CurrentPower(1700000002L, 200.0, -100.0, true);

        // When
        currentPowerDaoJdbcClient.save(List.of(cp1, cp2));

        // Then
        List<CurrentPower> results = jdbcClient.sql("SELECT * FROM current_power")
                .query((rs, rowNum) -> new CurrentPower(
                        rs.getLong("epoch_timestamp"),
                        rs.getDouble("generation"),
                        rs.getDouble("consumption"),
                        rs.getBoolean("uploaded")
                ))
                .list();

        assertEquals(2, results.size());
    }

    @Test
    void givenNotUploadedRows_whenGetNotUploaded_thenReturnsOnlyNotUploaded() {
        // Given
        CurrentPower cp1 = new CurrentPower(1700000003L, 300.0, -150.0, false);
        CurrentPower cp2 = new CurrentPower(1700000004L, 400.0, -200.0, true);
        currentPowerDaoJdbcClient.save(List.of(cp1, cp2));

        // When
        List<CurrentPower> notUploaded = currentPowerDaoJdbcClient.getNotUploaded();

        // Then
        assertEquals(1, notUploaded.size());
        CurrentPower result = notUploaded.getFirst();
        assertEquals(1700000003L, result.getEpochTimestamp());
        assertEquals(300.0, result.getGeneration());
        assertEquals(-150.0, result.getConsumption());
        assertFalse(result.isUploaded());
    }
}