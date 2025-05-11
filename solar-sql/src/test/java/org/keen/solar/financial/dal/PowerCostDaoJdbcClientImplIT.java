package org.keen.solar.financial.dal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.keen.solar.financial.domain.PowerCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;

/**
 * Tests PowerCostDaoJdbcClientImpl using an in-memory database (configured in /test/resources/application.properties).
 */
@SpringBootTest
@Sql(scripts = "/schema-h2.sql")
@Transactional
public class PowerCostDaoJdbcClientImplIT {

    @Autowired
    private DataSource dataSource;

    @Test
    public void givenSavedPowerCost_whenFindById_thenPowerCostReturned() {
        // Given
        PowerCost powerCost = new PowerCost(BigDecimal.valueOf(20711,12),
                1730493120, 60);

        JdbcClient jdbcClient = JdbcClient.create(dataSource);
        PowerCostDaoJdbcClientImpl powerCostDaoJdbcClient = new PowerCostDaoJdbcClientImpl(jdbcClient);
        powerCostDaoJdbcClient.save(powerCost);

        // When
        PowerCost powerCostResult = jdbcClient.sql("SELECT * FROM power_cost WHERE id = ?")
                .param(1, 1L)
                .query(rs -> {
                    if (rs.next()) {
                        return new PowerCost(rs.getBigDecimal("cost"),
                                rs.getLong("period_end_epoch"),
                                rs.getInt("period_length_seconds"));
                    }
                    return null;
                });

        // Then
        Assertions.assertNotNull(powerCostResult);
        Assertions.assertEquals(powerCost.cost(), powerCostResult.cost());
        Assertions.assertEquals(powerCost.periodEndEpoch(), powerCostResult.periodEndEpoch());
        Assertions.assertEquals(powerCost.periodLengthSeconds(), powerCostResult.periodLengthSeconds());
    }
}
