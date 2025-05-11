package org.keen.solar.string.dal;

import org.keen.solar.string.domain.StringPower;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class StringPowerDaoJdbcClientImpl implements StringPowerDao {

    private final JdbcClient jdbcClient;

    public StringPowerDaoJdbcClientImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void save(StringPower stringPower) {
        jdbcClient.sql("""
            INSERT INTO string_power (period_end_epoch, period_length_seconds, string1_volts, string1_amps, string1_power, string2_volts, string2_amps, string2_power)
            VALUES (:period_end_epoch, :period_length_seconds, :string1_volts, :string1_amps, :string1_power, :string2_volts, :string2_amps, :string2_power)
            """)
                .param("period_end_epoch", stringPower.periodEndEpoch())
                .param("period_length_seconds", stringPower.periodLengthSeconds())
                .param("string1_volts", stringPower.string1Data().volts())
                .param("string1_amps", stringPower.string1Data().amps())
                .param("string1_power", stringPower.string1Data().power())
                .param("string2_volts", stringPower.string2Data().volts())
                .param("string2_amps", stringPower.string2Data().amps())
                .param("string2_power", stringPower.string2Data().power())
                .update();
    }
}