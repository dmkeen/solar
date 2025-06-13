package org.keen.solar.financial.dal;

import org.keen.solar.financial.domain.Tariff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TariffDaoJdbcClientImpl implements TariffDao {

    private static final Logger logger = LoggerFactory.getLogger(TariffDaoJdbcClientImpl.class);

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

    @Override
    @Transactional
    public void applyTariffs(List<Tariff> usageTariffs, List<Tariff> feedInTariffs) {
        List<Tariff> allTariffs = new ArrayList<>();
        allTariffs.addAll(usageTariffs);
        allTariffs.addAll(feedInTariffs);

        for (Tariff tariff : allTariffs) {
            // Expire existing tariffs
            int updatedRows = jdbcClient.sql("""
                                UPDATE tariff
                                SET end_effective_date_epoch = :endEffectiveDateEpoch
                                WHERE feed_in = :feedIn
                                  AND day_of_week = :dayOfWeek
                                  AND (end_effective_date_epoch IS NULL OR end_effective_date_epoch > :startEffectiveDateEpoch)
                            """)
                    .param("endEffectiveDateEpoch", tariff.startEffectiveDateEpoch() - 1)
                    .param("feedIn", tariff.feedIn())
                    .param("dayOfWeek", tariff.dayOfWeek().toString())
                    .param("startEffectiveDateEpoch", tariff.startEffectiveDateEpoch())
                    .update();

            if (updatedRows > 0) {
                logger.info("Updated {} existing tariffs for feedIn={}, dayOfWeek={}, startEffectiveDateEpoch={}",
                        updatedRows, tariff.feedIn(), tariff.dayOfWeek(), tariff.startEffectiveDateEpoch());
            }
        }

        for (Tariff tariff : allTariffs) {
            // Insert new tariffs
            jdbcClient.sql("""
                    INSERT INTO tariff (feed_in, start_effective_date_epoch, end_effective_date_epoch, day_of_week, start_of_period, end_of_period, price_per_kwh)
                    VALUES (:feedIn, :startEffectiveDateEpoch, :endEffectiveDateEpoch, :dayOfWeek, :startOfPeriod, :endOfPeriod, :pricePerKwh)
                """)
                .param("feedIn", tariff.feedIn())
                .param("startEffectiveDateEpoch", tariff.startEffectiveDateEpoch())
                .param("endEffectiveDateEpoch", tariff.endEffectiveDateEpoch())
                .param("dayOfWeek", tariff.dayOfWeek().toString())
                .param("startOfPeriod", tariff.startOfPeriod())
                .param("endOfPeriod", tariff.endOfPeriod())
                .param("pricePerKwh", tariff.pricePerKwh())
                .update();
        }
    }
}

