package org.keen.solar.financial.dal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.keen.solar.financial.domain.Tariff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.TestExecutionListener;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Tests the FeedInTariffRepository using an in-memory database (configured in /test/resources/application.properties).
 * <br/>
 * Setting spring.test.database.replace=none prevents @DataJdbcTest from configuring its own in-memory database
 * (which wouldn't have its schema setup by /test/resources/schema-h2.sql).
 */
@DataJdbcTest(properties = "spring.test.database.replace=none")
public class TariffRepositoryIT implements TestExecutionListener {

    @Autowired
    private TariffRepository repository;

    @BeforeEach
    public void populateRepository() {
        // Feed-in tariffs
        repository.save(new Tariff(true, 1704027600, 1717164000L, DayOfWeek.SUNDAY,
                LocalTime.of(0, 0), LocalTime.of(10, 00), BigDecimal.valueOf(55, 3)));
        repository.save(new Tariff(true, 1704027600, 1717164000L, DayOfWeek.SUNDAY,
                LocalTime.of(10, 0), LocalTime.of(14, 00), BigDecimal.valueOf(22, 3)));
        repository.save(new Tariff(true, 1704027600, 1717164000L, DayOfWeek.SUNDAY,
                LocalTime.of(14, 0), LocalTime.of(23, 59, 59), BigDecimal.valueOf(51, 3)));

        repository.save(new Tariff(true, 1717164000, 1730379600L, DayOfWeek.SUNDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(45, 3)));
        repository.save(new Tariff(true, 1717164000, 1730379600L, DayOfWeek.MONDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(45, 3)));
        repository.save(new Tariff(true, 1717164000, 1730379600L, DayOfWeek.TUESDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(45, 3)));
        repository.save(new Tariff(true, 1717164000, 1730379600L, DayOfWeek.WEDNESDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(45, 3)));
        repository.save(new Tariff(true, 1717164000, 1730379600L, DayOfWeek.THURSDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(45, 3)));
        repository.save(new Tariff(true, 1717164000, 1730379600L, DayOfWeek.FRIDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(45, 3)));
        repository.save(new Tariff(true, 1717164000, 1730379600L, DayOfWeek.SATURDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(45, 3)));

        repository.save(new Tariff(true, 1730379600, null, DayOfWeek.SUNDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(33, 3)));
        repository.save(new Tariff(true, 1730379600, null, DayOfWeek.MONDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(33, 3)));
        repository.save(new Tariff(true, 1730379600, null, DayOfWeek.TUESDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(33, 3)));
        repository.save(new Tariff(true, 1730379600, null, DayOfWeek.WEDNESDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(33, 3)));
        repository.save(new Tariff(true, 1730379600, null, DayOfWeek.THURSDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(33, 3)));
        repository.save(new Tariff(true, 1730379600, null, DayOfWeek.FRIDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(33, 3)));
        repository.save(new Tariff(true, 1730379600, null, DayOfWeek.SATURDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(33, 3)));

        // Usage tariffs
        repository.save(new Tariff(false, 1704027600, 1717164000L, DayOfWeek.SUNDAY,
                LocalTime.of(0, 0), LocalTime.of(15, 00), BigDecimal.valueOf(2174, 4)));
        repository.save(new Tariff(false, 1704027600, 1717164000L, DayOfWeek.SUNDAY,
                LocalTime.of(15, 0), LocalTime.of(21, 00), BigDecimal.valueOf(4082, 4)));
        repository.save(new Tariff(false, 1704027600, 1717164000L, DayOfWeek.SUNDAY,
                LocalTime.of(21, 0), LocalTime.of(23, 59, 59), BigDecimal.valueOf(2174, 4)));

        repository.save(new Tariff(false, 1717164000, 1730379600L, DayOfWeek.SUNDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3485, 4)));
        repository.save(new Tariff(false, 1717164000, 1730379600L, DayOfWeek.MONDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3485, 4)));
        repository.save(new Tariff(false, 1717164000, 1730379600L, DayOfWeek.TUESDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3485, 4)));
        repository.save(new Tariff(false, 1717164000, 1730379600L, DayOfWeek.WEDNESDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3485, 4)));
        repository.save(new Tariff(false, 1717164000, 1730379600L, DayOfWeek.THURSDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3485, 4)));
        repository.save(new Tariff(false, 1717164000, 1730379600L, DayOfWeek.FRIDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3485, 4)));
        repository.save(new Tariff(false, 1717164000, 1730379600L, DayOfWeek.SATURDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3485, 4)));

        repository.save(new Tariff(false, 1730379600, null, DayOfWeek.SUNDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3974, 4)));
        repository.save(new Tariff(false, 1730379600, null, DayOfWeek.MONDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3974, 4)));
        repository.save(new Tariff(false, 1730379600, null, DayOfWeek.TUESDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3974, 4)));
        repository.save(new Tariff(false, 1730379600, null, DayOfWeek.WEDNESDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3974, 4)));
        repository.save(new Tariff(false, 1730379600, null, DayOfWeek.THURSDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3974, 4)));
        repository.save(new Tariff(false, 1730379600, null, DayOfWeek.FRIDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3974, 4)));
        repository.save(new Tariff(false, 1730379600, null, DayOfWeek.SATURDAY,
                LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(3974, 4)));

    }

    @ParameterizedTest
    @CsvSource({"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"})
    public void givenTariffWithNoEndDate_whenFindEffectiveFeedInTariff_thenTariffRetrievedCorrectly(DayOfWeek dayOfWeek) {
        // Given

        // When
        Tariff effectiveTariff = repository.findEffectiveFeedInTariff(dayOfWeek, LocalTime.of(11, 45), 1730594700);

        // Then
        Assertions.assertNotNull(effectiveTariff);
        Assertions.assertTrue(effectiveTariff.isFeedIn());
        Assertions.assertEquals(BigDecimal.valueOf(33, 3), effectiveTariff.getPricePerKwh());
        Assertions.assertEquals(dayOfWeek, effectiveTariff.getDayOfWeek());
        Assertions.assertEquals(1730379600, effectiveTariff.getStartEffectiveDateEpoch());
        Assertions.assertEquals(null, effectiveTariff.getEndEffectiveDateEpoch());
        Assertions.assertEquals(LocalTime.of(0, 0), effectiveTariff.getStartOfPeriod());
        Assertions.assertEquals(LocalTime.of(23, 59), effectiveTariff.getEndOfPeriod());
    }

    @ParameterizedTest
    @CsvSource({"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"})
    public void givenTariffWithEndDate_whenFindEffectiveFeedInTariff_thenTariffRetrievedCorrectly(DayOfWeek dayOfWeek) {
        // Given

        // When
        Tariff effectiveTariff = repository.findEffectiveFeedInTariff(dayOfWeek, LocalTime.of(11, 45), 1730279600);

        // Then
        Assertions.assertNotNull(effectiveTariff);
        Assertions.assertTrue(effectiveTariff.isFeedIn());
        Assertions.assertEquals(BigDecimal.valueOf(45, 3), effectiveTariff.getPricePerKwh());
        Assertions.assertEquals(dayOfWeek, effectiveTariff.getDayOfWeek());
        Assertions.assertEquals(1717164000, effectiveTariff.getStartEffectiveDateEpoch());
        Assertions.assertEquals(1730379600L, effectiveTariff.getEndEffectiveDateEpoch());
        Assertions.assertEquals(LocalTime.of(0, 0), effectiveTariff.getStartOfPeriod());
        Assertions.assertEquals(LocalTime.of(23, 59), effectiveTariff.getEndOfPeriod());
    }

    @Test
    public void givenMultipleTariffsPerDay_whenFindEffectiveFeedInTariff_thenTariffRetrievedCorrectly() {
        // Given

        // When
        Tariff midnightTariff = repository.findEffectiveFeedInTariff(DayOfWeek.SUNDAY, LocalTime.of(0, 0), 1708174800);
        Tariff nineFiftyNineTariff = repository.findEffectiveFeedInTariff(DayOfWeek.SUNDAY, LocalTime.of(9, 59), 1708210740);
        Tariff tenTariff = repository.findEffectiveFeedInTariff(DayOfWeek.SUNDAY, LocalTime.of(10, 0), 1708210800);
        Tariff thirteenFiftyNineTariff = repository.findEffectiveFeedInTariff(DayOfWeek.SUNDAY, LocalTime.of(13, 59), 1708225140);
        Tariff fourteenTariff = repository.findEffectiveFeedInTariff(DayOfWeek.SUNDAY, LocalTime.of(14, 0), 1708225200);
        Tariff twentyThreeFiftyNineTariff = repository.findEffectiveFeedInTariff(DayOfWeek.SUNDAY, LocalTime.of(23, 59), 1708261140);

        // Then
        Assertions.assertEquals(BigDecimal.valueOf(55, 3), midnightTariff.getPricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(55, 3), nineFiftyNineTariff.getPricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(22, 3), tenTariff.getPricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(22, 3), thirteenFiftyNineTariff.getPricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(51, 3), fourteenTariff.getPricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(51, 3), twentyThreeFiftyNineTariff.getPricePerKwh());
    }

    @ParameterizedTest
    @CsvSource({"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"})
    public void givenTariffWithNoEndDate_whenFindEffectiveUsageTariff_thenTariffRetrievedCorrectly(DayOfWeek dayOfWeek) {
        // Given

        // When
        Tariff effectiveTariff = repository.findEffectiveUsageTariff(dayOfWeek, LocalTime.of(11, 45), 1730594700);

        // Then
        Assertions.assertNotNull(effectiveTariff);
        Assertions.assertFalse(effectiveTariff.isFeedIn());
        Assertions.assertEquals(BigDecimal.valueOf(3974, 4), effectiveTariff.getPricePerKwh());
        Assertions.assertEquals(dayOfWeek, effectiveTariff.getDayOfWeek());
        Assertions.assertEquals(1730379600, effectiveTariff.getStartEffectiveDateEpoch());
        Assertions.assertEquals(null, effectiveTariff.getEndEffectiveDateEpoch());
        Assertions.assertEquals(LocalTime.of(0, 0), effectiveTariff.getStartOfPeriod());
        Assertions.assertEquals(LocalTime.of(23, 59), effectiveTariff.getEndOfPeriod());
    }

    @ParameterizedTest
    @CsvSource({"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"})
    public void givenTariffWithEndDate_whenFindEffectiveUsageTariff_thenTariffRetrievedCorrectly(DayOfWeek dayOfWeek) {
        // Given

        // When
        Tariff effectiveTariff = repository.findEffectiveUsageTariff(dayOfWeek, LocalTime.of(11, 45), 1730279600);

        // Then
        Assertions.assertNotNull(effectiveTariff);
        Assertions.assertFalse(effectiveTariff.isFeedIn());
        Assertions.assertEquals(BigDecimal.valueOf(3485, 4), effectiveTariff.getPricePerKwh());
        Assertions.assertEquals(dayOfWeek, effectiveTariff.getDayOfWeek());
        Assertions.assertEquals(1717164000, effectiveTariff.getStartEffectiveDateEpoch());
        Assertions.assertEquals(1730379600L, effectiveTariff.getEndEffectiveDateEpoch());
        Assertions.assertEquals(LocalTime.of(0, 0), effectiveTariff.getStartOfPeriod());
        Assertions.assertEquals(LocalTime.of(23, 59), effectiveTariff.getEndOfPeriod());
    }

    @Test
    public void givenMultipleTariffsPerDay_whenFindEffectiveUsageTariff_thenTariffRetrievedCorrectly() {
        // Given

        // When
        Tariff midnightTariff = repository.findEffectiveUsageTariff(DayOfWeek.SUNDAY, LocalTime.of(0, 0), 1708174800);
        Tariff nineFiftyNineTariff = repository.findEffectiveUsageTariff(DayOfWeek.SUNDAY, LocalTime.of(14, 59), 1708210740);
        Tariff tenTariff = repository.findEffectiveUsageTariff(DayOfWeek.SUNDAY, LocalTime.of(15, 0), 1708210800);
        Tariff thirteenFiftyNineTariff = repository.findEffectiveUsageTariff(DayOfWeek.SUNDAY, LocalTime.of(20, 59), 1708225140);
        Tariff fourteenTariff = repository.findEffectiveUsageTariff(DayOfWeek.SUNDAY, LocalTime.of(21, 0), 1708225200);
        Tariff twentyThreeFiftyNineTariff = repository.findEffectiveUsageTariff(DayOfWeek.SUNDAY, LocalTime.of(23, 59), 1708261140);

        // Then
        Assertions.assertEquals(BigDecimal.valueOf(2174, 4), midnightTariff.getPricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(2174, 4), nineFiftyNineTariff.getPricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(4082, 4), tenTariff.getPricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(4082, 4), thirteenFiftyNineTariff.getPricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(2174, 4), fourteenTariff.getPricePerKwh());
        Assertions.assertEquals(BigDecimal.valueOf(2174, 4), twentyThreeFiftyNineTariff.getPricePerKwh());
    }
}
