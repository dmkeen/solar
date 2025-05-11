package org.keen.solar.financial.dal;

import org.keen.solar.financial.domain.Tariff;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
public class TariffDaoJdbcClientImpl implements TariffDao {

    private final JdbcClient jdbcClient;

    public TariffDaoJdbcClientImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Tariff getEffectiveFeedInTariff(DayOfWeek dayOfWeek, LocalTime localTime, long epochTime) {
        return jdbcClient.sql("""
                SELECT *
                FROM tariff
                WHERE feed_in = true
                  AND start_effective_date_epoch <= ?
                  AND (end_effective_date_epoch IS NULL OR end_effective_date_epoch > ?)
                  AND day_of_week = ?
                  AND start_of_period <= ?
                  AND end_of_period > ?
                """)
                .param(1, epochTime)
                .param(2, epochTime)
                .param(3, dayOfWeek.toString())
                .param(4, localTime)
                .param(5, localTime)
                .query(rs -> rs.next() ? new Tariff(
                        rs.getBoolean("feed_in"),
                        rs.getLong("start_effective_date_epoch"),
                        rs.getObject("end_effective_date_epoch", Long.class),
                        DayOfWeek.valueOf(rs.getString("day_of_week")),
                        rs.getTime("start_of_period").toLocalTime(),
                        rs.getTime("end_of_period").toLocalTime(),
                        rs.getBigDecimal("price_per_kwh")
                ) : null);
    }

    @Override
    public Tariff getEffectiveUsageTariff(DayOfWeek dayOfWeek, LocalTime localTime, long epochTime) {
        return jdbcClient.sql("""
                SELECT *
                FROM tariff
                WHERE feed_in = false
                  AND start_effective_date_epoch <= ?
                  AND (end_effective_date_epoch IS NULL OR end_effective_date_epoch > ?)
                  AND day_of_week = ?
                  AND start_of_period <= ?
                  AND end_of_period > ?
                """)
                .param(1, epochTime)
                .param(2, epochTime)
                .param(3, dayOfWeek.toString())
                .param(4, localTime)
                .param(5, localTime)
                .query(rs -> rs.next() ? new Tariff(
                        rs.getBoolean("feed_in"),
                        rs.getLong("start_effective_date_epoch"),
                        rs.getObject("end_effective_date_epoch", Long.class),
                        DayOfWeek.valueOf(rs.getString("day_of_week")),
                        rs.getTime("start_of_period").toLocalTime(),
                        rs.getTime("end_of_period").toLocalTime(),
                        rs.getBigDecimal("price_per_kwh")
                ) : null);
    }
}