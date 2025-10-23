package org.keen.solar.financial.dal;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.keen.solar.financial.domain.Tariff;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Integration test for TariffDaoEclipseStoreImpl.<br/>
 * <br/>
 * EclipseStore requires: --add-exports java.base/jdk.internal.misc=ALL-UNNAMED
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TariffDaoEclipseStoreImplIT {

    private EmbeddedStorageManager storageManager;
    private Map<DayOfWeek, List<Tariff>> root;
    private TariffDao tariffDao;

    @BeforeAll
    void setup(@TempDir Path tempDir) {
        storageManager = EmbeddedStorage.start(tempDir);
    }

    @BeforeEach
    void populateData() {
        root = new EnumMap<>(DayOfWeek.class);

        insertTariff(true, 1704027600, 1717164000L, DayOfWeek.SUNDAY, "00:00:00", "10:00:00", BigDecimal.valueOf(55, 3));
        insertTariff(true, 1704027600, 1717164000L, DayOfWeek.SUNDAY, "10:00:00", "14:00:00", BigDecimal.valueOf(22, 3));
        insertTariff(true, 1704027600, 1717164000L, DayOfWeek.SUNDAY, "14:00:00", "23:59:59", BigDecimal.valueOf(51, 3));

        insertTariff(true, 1717164000, 1730379600L, DayOfWeek.SUNDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));
        insertTariff(true, 1717164000, 1730379600L, DayOfWeek.MONDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));
        insertTariff(true, 1717164000, 1730379600L, DayOfWeek.TUESDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));
        insertTariff(true, 1717164000, 1730379600L, DayOfWeek.WEDNESDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));
        insertTariff(true, 1717164000, 1730379600L, DayOfWeek.THURSDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));
        insertTariff(true, 1717164000, 1730379600L, DayOfWeek.FRIDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));
        insertTariff(true, 1717164000, 1730379600L, DayOfWeek.SATURDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(45, 3));

        insertTariff(true, 1730379600, null, DayOfWeek.SUNDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));
        insertTariff(true, 1730379600, null, DayOfWeek.MONDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));
        insertTariff(true, 1730379600, null, DayOfWeek.TUESDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));
        insertTariff(true, 1730379600, null, DayOfWeek.WEDNESDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));
        insertTariff(true, 1730379600, null, DayOfWeek.THURSDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));
        insertTariff(true, 1730379600, null, DayOfWeek.FRIDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));
        insertTariff(true, 1730379600, null, DayOfWeek.SATURDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(33, 3));

        insertTariff(false, 1704027600, 1717164000L, DayOfWeek.SUNDAY, "00:00:00", "15:00:00", BigDecimal.valueOf(2174, 4));
        insertTariff(false, 1704027600, 1717164000L, DayOfWeek.SUNDAY, "15:00:00", "21:00:00", BigDecimal.valueOf(4082, 4));
        insertTariff(false, 1704027600, 1717164000L, DayOfWeek.SUNDAY, "21:00:00", "23:59:59", BigDecimal.valueOf(2174, 4));

        insertTariff(false, 1717164000, 1730379600L, DayOfWeek.SUNDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));
        insertTariff(false, 1717164000, 1730379600L, DayOfWeek.MONDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));
        insertTariff(false, 1717164000, 1730379600L, DayOfWeek.TUESDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));
        insertTariff(false, 1717164000, 1730379600L, DayOfWeek.WEDNESDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));
        insertTariff(false, 1717164000, 1730379600L, DayOfWeek.THURSDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));
        insertTariff(false, 1717164000, 1730379600L, DayOfWeek.FRIDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));
        insertTariff(false, 1717164000, 1730379600L, DayOfWeek.SATURDAY, "00:00:00", "23:59:00", BigDecimal.valueOf(3485, 4));

        insertTariff(false, 1730379600, null, DayOfWeek.SUNDAY, "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.SUNDAY, "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.MONDAY, "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.MONDAY, "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.TUESDAY, "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.TUESDAY, "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.WEDNESDAY, "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.WEDNESDAY, "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.THURSDAY, "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.THURSDAY, "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.FRIDAY, "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.FRIDAY, "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.SATURDAY, "00:00:00", "15:00:00", BigDecimal.valueOf(3974, 4));
        insertTariff(false, 1730379600, null, DayOfWeek.SATURDAY, "15:00:00", "23:59:00", BigDecimal.valueOf(3074, 4));

        storageManager.setRoot(root);
        tariffDao = new TariffDaoEclipseStoreImpl(storageManager);
    }

    private void insertTariff(boolean feedIn, long startEpoch, Long endEpoch, DayOfWeek dayOfWeek,
                              String startTime, String endTime, BigDecimal pricePerKwh) {
        Tariff tariff = createTariff(feedIn, startEpoch, endEpoch, dayOfWeek, LocalTime.parse(startTime), LocalTime.parse(endTime), pricePerKwh);
        List<Tariff> tariffs = root.computeIfAbsent(dayOfWeek, dayOfWeek1 -> new ArrayList<>());
        tariffs.add(tariff);
    }

    private static Tariff createTariff(boolean feedIn, long startEpoch, Long endEpoch, DayOfWeek dayOfWeek,
                                       LocalTime startTime, LocalTime endTime, BigDecimal pricePerKwh) {
        return new Tariff(feedIn, startEpoch, endEpoch, dayOfWeek, startTime, endTime, pricePerKwh);
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
        long startEffectiveDateEpoch = 1731196800L;
        LocalTime startTime = LocalTime.of(0, 0);
        LocalTime endTime = LocalTime.of(23, 59, 59);
        BigDecimal pricePerKwh = new BigDecimal("0.25");

        // When: The tariff is applied
        tariffDao.applyTariffs(List.of(createTariff(false, startEffectiveDateEpoch, null, DayOfWeek.SUNDAY, startTime, endTime, pricePerKwh),
                        createTariff(false, startEffectiveDateEpoch, null, DayOfWeek.MONDAY, startTime, endTime, pricePerKwh),
                        createTariff(false, startEffectiveDateEpoch, null, DayOfWeek.TUESDAY, startTime, endTime, pricePerKwh),
                        createTariff(false, startEffectiveDateEpoch, null, DayOfWeek.WEDNESDAY, startTime, endTime, pricePerKwh),
                        createTariff(false, startEffectiveDateEpoch, null, DayOfWeek.THURSDAY, startTime, endTime, pricePerKwh),
                        createTariff(false, startEffectiveDateEpoch, null, DayOfWeek.FRIDAY, startTime, endTime, pricePerKwh),
                        createTariff(false, startEffectiveDateEpoch, null, DayOfWeek.SATURDAY, startTime, endTime, pricePerKwh)),
                List.of(createTariff(true, startEffectiveDateEpoch, null, DayOfWeek.SUNDAY, startTime, endTime, pricePerKwh),
                        createTariff(true, startEffectiveDateEpoch, null, DayOfWeek.MONDAY, startTime, endTime, pricePerKwh),
                        createTariff(true, startEffectiveDateEpoch, null, DayOfWeek.TUESDAY, startTime, endTime, pricePerKwh),
                        createTariff(true, startEffectiveDateEpoch, null, DayOfWeek.WEDNESDAY, startTime, endTime, pricePerKwh),
                        createTariff(true, startEffectiveDateEpoch, null, DayOfWeek.THURSDAY, startTime, endTime, pricePerKwh),
                        createTariff(true, startEffectiveDateEpoch, null, DayOfWeek.FRIDAY, startTime, endTime, pricePerKwh),
                        createTariff(true, startEffectiveDateEpoch, null, DayOfWeek.SATURDAY, startTime, endTime, pricePerKwh)));

        // Then: The tariff is inserted into the store
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            List<Tariff> tariffs = root.get(dayOfWeek).stream()
                    .filter(tariff -> !tariff.feedIn())
                    .filter(tariff -> tariff.startEffectiveDateEpoch() == startEffectiveDateEpoch)
                    .toList();

            Assertions.assertAll(
                    () -> Assertions.assertEquals(1, tariffs.size(), "Expected one tariff per day to be inserted"),
                    () -> Assertions.assertEquals(startEffectiveDateEpoch, tariffs.getFirst().startEffectiveDateEpoch(),
                            "Inserted tariff does not match the expected tariff"),
                    () -> Assertions.assertEquals(startTime, tariffs.getFirst().startOfPeriod(),
                            "Inserted tariff does not match the expected tariff"),
                    () -> Assertions.assertEquals(endTime, tariffs.getFirst().endOfPeriod(),
                            "Inserted tariff does not match the expected tariff"),
                    () -> Assertions.assertEquals(pricePerKwh, tariffs.getFirst().pricePerKwh(),
                            "Inserted tariff does not match the expected tariff")
            );
        }
    }

}
