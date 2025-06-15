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
        CurrentPower cp = new CurrentPower(1700000000L, 500.0, -200.0);

        // When
        currentPowerDaoJdbcClient.save(cp);

        // Then
        List<CurrentPower> results = jdbcClient.sql("SELECT * FROM current_power")
                .query((rs, rowNum) -> new CurrentPower(
                        rs.getLong("epoch_timestamp"),
                        rs.getDouble("generation"),
                        rs.getDouble("consumption")
                ))
                .list();

        assertEquals(1, results.size());
        CurrentPower result = results.getFirst();
        assertEquals(1700000000L, result.epochTimestamp());
        assertEquals(500.0, result.generation());
        assertEquals(-200.0, result.consumption());
    }

    @Test
    void givenCurrentPowersStartingFrom_whenGetStartingFrom_thenReturnsCorrectPowers() {
        // Given
        CurrentPower cp0 = new CurrentPower(1700000004L, 500.0, -300.0);
        CurrentPower cp1 = new CurrentPower(1700000005L, 600.0, -300.0);
        CurrentPower cp2 = new CurrentPower(1700000006L, 700.0, -350.0);
        currentPowerDaoJdbcClient.save(cp0);
        currentPowerDaoJdbcClient.save(cp1);
        currentPowerDaoJdbcClient.save(cp2);

        // When
        List<CurrentPower> results = currentPowerDaoJdbcClient.getStartingFrom(1700000005L);

        // Then
        assertEquals(2, results.size());
        assertTrue(results.contains(cp1));
        assertTrue(results.contains(cp2));
    }
}