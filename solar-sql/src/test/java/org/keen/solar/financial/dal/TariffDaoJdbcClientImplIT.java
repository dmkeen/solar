package org.keen.solar.financial.dal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.keen.solar.financial.domain.Tariff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@Sql(scripts = "/schema-h2.sql")
@Transactional
class TariffDaoJdbcClientImplIT {

    private TariffDaoJdbcClientImpl tariffDao;

    private JdbcClient jdbcClient;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void populateRepository() {
        jdbcClient = JdbcClient.create(dataSource);
        tariffDao = new TariffDaoJdbcClientImpl(jdbcClient);

        // Clear existing data
        jdbcClient.sql("DELETE FROM tariff").update();

        // Insert test data directly using JdbcClient
        insertTariff(true, 1704027600, 1717164000L, "SUNDAY", "00:00:00", "10:00:00", BigDecimal.valueOf(55, 3));
        insertTariff(true, 1704027600, 1717164000L, "SUNDAY", "10:00:00", "14:00:00", BigDecimal.valueOf(22, 3));
        insertTariff(true, 1704027600, 1717164000L, "SUNDAY", "14:00:00", "23:59:59", BigDecimal.valueOf(51, 3));

        insertTariff(true, 1717164000, 1730379600L, "SUNDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));
        insertTariff(true, 1717164000, 1730379600L, "MONDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));
        insertTariff(true, 1717164000, 1730379600L, "TUESDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));
        insertTariff(true, 1717164000, 1730379600L, "WEDNESDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));
        insertTariff(true, 1717164000, 1730379600L, "THURSDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));
        insertTariff(true, 1717164000, 1730379600L, "FRIDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));
        insertTariff(true, 1717164000, 1730379600L, "SATURDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));

        insertTariff(true, 1730379600, null, "SUNDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));
        insertTariff(true, 1730379600, null, "MONDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));
        insertTariff(true, 1730379600, null, "TUESDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));
        insertTariff(true, 1730379600, null, "WEDNESDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));
        insertTariff(true, 1730379600, null, "THURSDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));
        insertTariff(true, 1730379600, null, "FRIDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));
        insertTariff(true, 1730379600, null, "SATURDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));

        insertTariff(false, 1704027600, 1717164000L, "SUNDAY", "00:00:00", "15:00:00", BigDecimal.valueOf(2174, 4));
        insertTariff(false, 1704027600, 1717164000L, "SUNDAY", "15:00:00", "21:00:00", BigDecimal.valueOf(4082, 4));
        insertTariff(false, 1704027600, 1717164000L, "SUNDAY", "21:00:00", "23:59:59", BigDecimal.valueOf(2174, 4));

        insertTariff(false, 1717164000, 1730379600L, "SUNDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));
        insertTariff(false, 1717164000, 1730379600L, "MONDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));
        insertTariff(false, 1717164000, 1730379600L, "TUESDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));
        insertTariff(false, 1717164000, 1730379600L, "WEDNESDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));
        insertTariff(false, 1717164000, 1730379600L, "THURSDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));
        insertTariff(false, 1717164000, 1730379600L, "FRIDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));
        insertTariff(false, 1717164000, 1730379600L, "SATURDAY", "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));

        insertTariff(false, 1730379600, null, "SUNDAY", "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, "SUNDAY", "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
        insertTariff(false, 1730379600, null, "MONDAY", "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, "MONDAY", "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
        insertTariff(false, 1730379600, null, "TUESDAY", "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, "TUESDAY", "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
        insertTariff(false, 1730379600, null, "WEDNESDAY", "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, "WEDNESDAY", "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
        insertTariff(false, 1730379600, null, "THURSDAY", "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, "THURSDAY", "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
        insertTariff(false, 1730379600, null, "FRIDAY", "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, "FRIDAY", "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
        insertTariff(false, 1730379600, null, "SATURDAY", "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, "SATURDAY", "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
    }

    private void insertTariff(boolean feedIn, long startEpoch, Long endEpoch, String dayOfWeek, String startTime, String endTime, BigDecimal pricePerKwh) {
        jdbcClient.sql("""
                INSERT INTO tariff (feed_in, start_effective_date_epoch, end_effective_date_epoch,
                                    day_of_week, start_of_period, end_of_period, price_per_kwh)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """)
                .param(1, feedIn)
                .param(2, startEpoch)
                .param(3, endEpoch)
                .param(4, dayOfWeek)
                .param(5, startTime)
                .param(6, endTime)
                .param(7, pricePerKwh)
                .update();
    }


    @ParameterizedTest
    @CsvSource({"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"})
    void givenTariffWithNoEndDate_whenFindEffectiveFeedInTariff_thenTariffRetrievedCorrectly(DayOfWeek dayOfWeek) {
        Tariff effectiveTariff = tariffDao.getEffectiveFeedInTariff(dayOfWeek, LocalTime.of(11, 45), 1730594700);

        Assertions.assertNotNull(effectiveTariff);
        Assertions.assertTrue(effectiveTariff.feedIn());
        Assertions.assertEquals(BigDecimal.valueOf(33, 3), effectiveTariff.pricePerKwh());
        Assertions.assertEquals(dayOfWeek, effectiveTariff.dayOfWeek());
        Assertions.assertEquals(1730379600, effectiveTariff.startEffectiveDateEpoch());
        Assertions.assertNull(effectiveTariff.endEffectiveDateEpoch());
        Assertions.assertEquals(LocalTime.of(0, 0), effectiveTariff.startOfPeriod());
        Assertions.assertEquals(LocalTime.of(23, 59), effectiveTariff.endOfPeriod());
    }

    @ParameterizedTest
    @CsvSource({"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"})
    void givenTariffWithEndDate_whenFindEffectiveFeedInTariff_thenTariffRetrievedCorrectly(DayOfWeek dayOfWeek) {
        Tariff effectiveTariff = tariffDao.getEffectiveFeedInTariff(dayOfWeek, LocalTime.of(11, 45), 1730279600);

        Assertions.assertNotNull(effectiveTariff);
        Assertions.assertTrue(effectiveTariff.feedIn());
        Assertions.assertEquals(BigDecimal.valueOf(45, 3), effectiveTariff.pricePerKwh());
        Assertions.assertEquals(dayOfWeek, effectiveTariff.dayOfWeek());
        Assertions.assertEquals(1717164000, effectiveTariff.startEffectiveDateEpoch());
        Assertions.assertEquals(1730379600L, effectiveTariff.endEffectiveDateEpoch());
        Assertions.assertEquals(LocalTime.of(0, 0), effectiveTariff.startOfPeriod());
        Assertions.assertEquals(LocalTime.of(23, 59), effectiveTariff.endOfPeriod());
    }

    @Test
    void givenMultipleTariffsPerDay_whenFindEffectiveFeedInTariff_thenTariffRetrievedCorrectly() {
        Tariff midnightTariff = tariffDao.getEffectiveFeedInTariff(DayOfWeek.SUNDAY, LocalTime.of(0, 0), 1708174800);
        Tariff nineFiftyNineTariff = tariffDao.getEffectiveFeedInTariff(DayOfWeek.SUNDAY, LocalTime.of(9, 59), 1708210740);
        Tariff tenTariff = tariffDao.getEffectiveFeedInTariff(DayOfWeek.SUNDAY, LocalTime.of(10, 0), 1708210800);
        Tariff thirteenFiftyNineTariff = tariffDao.getEffectiveFeedInTariff(DayOfWeek.SUNDAY, LocalTime.of(13, 59), 1708225140);
        Tariff fourteenTariff = tariffDao.getEffectiveFeedInTariff(DayOfWeek.SUNDAY, LocalTime.of(14, 0), 1708225200);
        Tariff twentyThreeFiftyNineTariff = tariffDao.getEffectiveFeedInTariff(DayOfWeek.SUNDAY, LocalTime.of(23, 59), 1708261140);

        Assertions.assertEquals(BigDecimal.valueOf(55, 3), midnightTariff.pricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(55, 3), nineFiftyNineTariff.pricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(22, 3), tenTariff.pricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(22, 3), thirteenFiftyNineTariff.pricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(51, 3), fourteenTariff.pricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(51, 3), twentyThreeFiftyNineTariff.pricePerKwh());
    }

    @ParameterizedTest
    @CsvSource({"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"})
    void givenTariffWithNoEndDate_whenFindEffectiveUsageTariff_thenTariffRetrievedCorrectly(DayOfWeek dayOfWeek) {
        Tariff effectiveTariff = tariffDao.getEffectiveUsageTariff(dayOfWeek, LocalTime.of(11, 45), 1730594700);

        Assertions.assertNotNull(effectiveTariff);
        Assertions.assertFalse(effectiveTariff.feedIn());
        Assertions.assertEquals(BigDecimal.valueOf(3974, 4), effectiveTariff.pricePerKwh());
        Assertions.assertEquals(dayOfWeek, effectiveTariff.dayOfWeek());
        Assertions.assertEquals(1730379600, effectiveTariff.startEffectiveDateEpoch());
        Assertions.assertNull(effectiveTariff.endEffectiveDateEpoch());
        Assertions.assertEquals(LocalTime.of(0, 0), effectiveTariff.startOfPeriod());
        Assertions.assertEquals(LocalTime.of(15, 0), effectiveTariff.endOfPeriod());
    }

    @ParameterizedTest
    @CsvSource({"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"})
    void givenTariffWithEndDate_whenFindEffectiveUsageTariff_thenTariffRetrievedCorrectly(DayOfWeek dayOfWeek) {
        Tariff effectiveTariff = tariffDao.getEffectiveUsageTariff(dayOfWeek, LocalTime.of(11, 45), 1730279600);

        Assertions.assertNotNull(effectiveTariff);
        Assertions.assertFalse(effectiveTariff.feedIn());
        Assertions.assertEquals(BigDecimal.valueOf(3485, 4), effectiveTariff.pricePerKwh());
        Assertions.assertEquals(dayOfWeek, effectiveTariff.dayOfWeek());
        Assertions.assertEquals(1717164000, effectiveTariff.startEffectiveDateEpoch());
        Assertions.assertEquals(1730379600L, effectiveTariff.endEffectiveDateEpoch());
        Assertions.assertEquals(LocalTime.of(0, 0), effectiveTariff.startOfPeriod());
        Assertions.assertEquals(LocalTime.of(23, 59), effectiveTariff.endOfPeriod());
    }

    @Test
    void givenMultipleTariffsPerDay_whenFindEffectiveUsageTariff_thenTariffRetrievedCorrectly() {
        Tariff midnightTariff = tariffDao.getEffectiveUsageTariff(DayOfWeek.SUNDAY, LocalTime.of(0, 0), 1708174800);
        Tariff nineFiftyNineTariff = tariffDao.getEffectiveUsageTariff(DayOfWeek.SUNDAY, LocalTime.of(14, 59), 1708210740);
        Tariff tenTariff = tariffDao.getEffectiveUsageTariff(DayOfWeek.SUNDAY, LocalTime.of(15, 0), 1708210800);
        Tariff thirteenFiftyNineTariff = tariffDao.getEffectiveUsageTariff(DayOfWeek.SUNDAY, LocalTime.of(20, 59), 1708225140);
        Tariff fourteenTariff = tariffDao.getEffectiveUsageTariff(DayOfWeek.SUNDAY, LocalTime.of(21, 0), 1708225200);
        Tariff twentyThreeFiftyNineTariff = tariffDao.getEffectiveUsageTariff(DayOfWeek.SUNDAY, LocalTime.of(23, 59), 1708261140);

        Assertions.assertEquals(BigDecimal.valueOf(2174, 4), midnightTariff.pricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(2174, 4), nineFiftyNineTariff.pricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(4082, 4), tenTariff.pricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(4082, 4), thirteenFiftyNineTariff.pricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(2174, 4), fourteenTariff.pricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(2174, 4), twentyThreeFiftyNineTariff.pricePerKwh());
    }

    @Test
    void givenNewTariff_whenApplyTariffs_thenTariffIsInserted() {
        // Given: A new tariff that does not exist in the database
        Tariff newTariff1 = new Tariff(
                false,
                1731196800L,
                null,
                DayOfWeek.SUNDAY,
                LocalTime.of(0, 0),
                LocalTime.of(16, 0),
                new BigDecimal("0.25")
        );
        Tariff newTariff2 = new Tariff(
                false,
                1731196800L,
                null,
                DayOfWeek.SUNDAY,
                LocalTime.of(16, 0),
                LocalTime.of(23, 59, 59),
                new BigDecimal("0.27")
        );

        // When: The tariff is applied
        tariffDao.applyTariffs(List.of(newTariff1, newTariff2), List.of());

        // Then: The tariff is inserted into the database
        List<Tariff> tariffs = jdbcClient.sql("SELECT * FROM tariff WHERE feed_in = false " +
                        "AND day_of_week = 'SUNDAY' AND start_effective_date_epoch = 1731196800")
                .query((rs, rowNum) -> new Tariff(
                        rs.getBoolean("feed_in"),
                        rs.getLong("start_effective_date_epoch"),
                        rs.getObject("end_effective_date_epoch", Long.class),
                        DayOfWeek.valueOf(rs.getString("day_of_week")),
                        rs.getTime("start_of_period").toLocalTime(),
                        rs.getTime("end_of_period").toLocalTime(),
                        rs.getBigDecimal("price_per_kwh")
                )).list();

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, tariffs.size(), "Expected two tariffs to be inserted"),
                () -> Assertions.assertEquals(newTariff1, tariffs.getFirst(), "Inserted tariff does not match the expected tariff"),
                () -> Assertions.assertEquals(newTariff2, tariffs.getLast(), "Inserted tariff does not match the expected tariff")
        );
    }

}
