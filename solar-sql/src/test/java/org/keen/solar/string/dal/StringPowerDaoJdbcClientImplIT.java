package org.keen.solar.string.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keen.solar.string.domain.StringPower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "/schema-h2.sql")
@Transactional
public class StringPowerDaoJdbcClientImplIT {

    @Autowired
    private DataSource dataSource;
    private StringPowerDaoJdbcClientImpl stringPowerDaoJdbcClient;
    private JdbcClient jdbcClient;

    @BeforeEach
    public void setUp() {
        jdbcClient = JdbcClient.create(dataSource);
        stringPowerDaoJdbcClient = new StringPowerDaoJdbcClientImpl(jdbcClient);
    }

    @Test
    public void givenNewStringPower_whenSave_thenInsertedInDatabase() {
        // Given
        StringPower stringPower = new StringPower(
                1672531200L,
                300,
                BigDecimal.valueOf(100), BigDecimal.valueOf(2),
                BigDecimal.valueOf(110), BigDecimal.valueOf(1.5)
        );

        // When
        stringPowerDaoJdbcClient.save(stringPower);

        // Then
        List<StringPower> results = jdbcClient.sql("SELECT * FROM string_power")
                .query((rs, rowNum) -> new StringPower(
                        rs.getLong("period_end_epoch"),
                        rs.getInt("period_length_seconds"),
                        rs.getBigDecimal("string1_volts"),
                        rs.getBigDecimal("string1_amps"),
                        rs.getBigDecimal("string2_volts"),
                        rs.getBigDecimal("string2_amps")
                ))
                .list();

        assertEquals(1, results.size());
        StringPower result = results.getFirst();
        assertEquals(1672531200L, result.periodEndEpoch());
        assertEquals(300, result.periodLengthSeconds());
        assertEquals(100.0, result.string1Data().volts());
        assertEquals(2.0, result.string1Data().amps());
        assertEquals(200.0, result.string1Data().power());
        assertEquals(110.0, result.string2Data().volts());
        assertEquals(1.5, result.string2Data().amps());
        assertEquals(165.0, result.string2Data().power());
    }
}