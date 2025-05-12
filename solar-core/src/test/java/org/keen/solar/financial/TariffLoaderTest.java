package org.keen.solar.financial;

import org.junit.jupiter.api.Test;
import org.keen.solar.financial.dal.TariffDao;
import org.keen.solar.financial.domain.Tariff;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TariffLoaderTest {

    @Test
    void givenValidTariffFile_whenUpdateTariffs_thenTariffsAreLoadedAndApplied() throws Exception {
        // Given
        TariffDao tariffDao = mock(TariffDao.class);
        TariffLoader tariffLoader = new TariffLoader(
                tariffDao,
                "src/test/resources/org/keen/solar/financial/tariff.csv",
                "yyyy-MM-dd HH:mm:ss",
                "Australia/Melbourne"
        );

        // When: The updateTariffs method is called
        tariffLoader.updateTariffs();

        // Then: Tariffs are parsed and passed to the DAO
        ArgumentCaptor<List<Tariff>> usageTariffsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Tariff>> feedInTariffsCaptor = ArgumentCaptor.forClass(List.class);

        verify(tariffDao, times(1)).applyTariffs(usageTariffsCaptor.capture(), feedInTariffsCaptor.capture());

        // Verify feed-in tariffs
        List<Tariff> feedInTariffs = feedInTariffsCaptor.getValue();
        assertAll(
                () -> assertEquals(7, feedInTariffs.size(), "Expected 7 feed-in tariffs"),
                () -> assertEquals(new Tariff(
                        true,
                        1731157200L,
                        null,
                        DayOfWeek.TUESDAY,
                        LocalTime.of(0, 0),
                        LocalTime.of(23, 59, 59),
                        new BigDecimal("0.032")
                ), feedInTariffs.get(1), "Feed-in tariff for Tuesday does not match"),
                () -> assertEquals(new Tariff(
                        true,
                        1731157200L,
                        null,
                        DayOfWeek.WEDNESDAY,
                        LocalTime.of(0, 0),
                        LocalTime.of(23, 59, 59),
                        new BigDecimal("0.035")
                ), feedInTariffs.get(2), "Feed-in tariff for Wednesday does not match")
        );

        // Verify usage tariffs
        List<Tariff> usageTariffs = usageTariffsCaptor.getValue();
        assertAll(
                () -> assertEquals(17, usageTariffs.size(), "Expected 17 usage tariffs"),
                () -> assertEquals(new Tariff(
                        false,
                        1731157200L,
                        null,
                        DayOfWeek.TUESDAY,
                        LocalTime.of(0, 0),
                        LocalTime.of(10, 0),
                        new BigDecimal("0.2500")
                ), usageTariffs.get(2), "Usage tariff for Tuesday does not match"),
                () -> assertEquals(new Tariff(
                        false,
                        1731157200L,
                        null,
                        DayOfWeek.THURSDAY,
                        LocalTime.of(0, 0),
                        LocalTime.of(6, 0),
                        new BigDecimal("0.1900")
                ), usageTariffs.get(7), "Usage tariff for Thursday does not match")
        );
    }

    @Test
    void givenInvalidTariffFile_whenUpdateTariffs_thenExceptionIsThrown() {
        // Given: An invalid tariff file path
        TariffDao tariffDao = mock(TariffDao.class);
        TariffLoader invalidLoader = new TariffLoader(
                tariffDao,
                "src/test/resources/invalid.csv",
                "yyyy-MM-dd HH:mm:ss",
                "Australia/Melbourne"
        );

        // When/Then: An exception is thrown when updateTariffs is called
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                invalidLoader::updateTariffs
        );

        assertTrue(exception.getMessage().contains("Tariff file not found"), "Exception message does not contain expected text");
        verifyNoInteractions(tariffDao);
    }

    @Test
    void givenTariffFileWithIncompleteDay_whenUpdateTariffs_thenValidationFails() {
        // Given: A tariff file with an incomplete day
        TariffDao tariffDao = mock(TariffDao.class);
        TariffLoader tariffLoader = new TariffLoader(
                tariffDao,
                "src/test/resources/org/keen/solar/financial/tariff-incomplete-day.csv",
                "yyyy-MM-dd HH:mm:ss",
                "Australia/Melbourne"
        );

        // When/Then: An exception is thrown when updateTariffs is called
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                tariffLoader::updateTariffs
        );

        assertTrue(exception.getMessage().contains("do not cover the entire day"), "Exception message does not indicate incomplete day coverage");
        verifyNoInteractions(tariffDao);
    }

    @Test
    void givenTariffFileWithMissingDay_whenUpdateTariffs_thenValidationFails() {
        // Given: A tariff file with a missing day
        TariffDao tariffDao = mock(TariffDao.class);
        TariffLoader tariffLoader = new TariffLoader(
                tariffDao,
                "src/test/resources/org/keen/solar/financial/tariff-missing-day.csv",
                "yyyy-MM-dd HH:mm:ss",
                "Australia/Melbourne"
        );

        // When/Then: An exception is thrown when updateTariffs is called
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                tariffLoader::updateTariffs
        );

        assertTrue(exception.getMessage().contains("tariffs are missing for"), "Exception message does not indicate missing day");
        verifyNoInteractions(tariffDao);
    }
}
