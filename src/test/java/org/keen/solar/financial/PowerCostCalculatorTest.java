package org.keen.solar.financial;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.keen.solar.financial.dal.TariffRepository;
import org.keen.solar.financial.dal.PowerCostRepository;
import org.keen.solar.financial.domain.Tariff;
import org.keen.solar.financial.domain.PowerCost;
import org.keen.solar.system.domain.CurrentPower;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PowerCostCalculatorTest {

    @Test
    public void givenTwoMinutesOfPowerCosts_whenCollectUncostedPowers_thenCostCalculatedCorrectly() throws IOException {
        // Given

        BigDecimal feedInPricePerKwh = BigDecimal.valueOf(33, 3);
        TariffRepository tariffRepository = mock(TariffRepository.class);
        when(tariffRepository.findEffectiveFeedInTariff(any(DayOfWeek.class), any(LocalTime.class), anyLong()))
                .thenReturn(new Tariff(true, 0, null, null,
                        null, null, feedInPricePerKwh));
        BigDecimal powerPricePerKwh = BigDecimal.valueOf(4082, 4);
        when(tariffRepository.findEffectiveUsageTariff(any(DayOfWeek.class), any(LocalTime.class), anyLong()))
                .thenReturn(new Tariff(false, 0, null, null,
                        null, null, powerPricePerKwh));

        PowerCostRepository powerCostRepository = mock(PowerCostRepository.class);

        int collectionFrequencySeconds = 60;
        PowerCostCalculator calculator = new PowerCostCalculator(collectionFrequencySeconds, tariffRepository, powerCostRepository);

        // When
        getCurrentPowers().forEach(calculator::collectUncostedPowers);

        // Then
        ArgumentCaptor<PowerCost> argumentCaptor = ArgumentCaptor.forClass(PowerCost.class);
        verify(powerCostRepository, times(2)).save(argumentCaptor.capture());
        List<PowerCost> powerCost = argumentCaptor.getAllValues();
        PowerCost powerCost1 = powerCost.get(0);
        Assertions.assertEquals(BigDecimal.valueOf(59021529L, 12),
                powerCost1.getCost().setScale(12, RoundingMode.HALF_UP));
        Assertions.assertEquals(1730493120, powerCost1.getPeriodEndEpoch());
        Assertions.assertEquals(collectionFrequencySeconds, powerCost1.getPeriodLengthSeconds());
        PowerCost powerCost2 = powerCost.get(1);
        Assertions.assertEquals(BigDecimal.valueOf(-95571392L, 12),
                powerCost2.getCost().setScale(12, RoundingMode.HALF_UP));
        Assertions.assertEquals(1730493180, powerCost2.getPeriodEndEpoch());
        Assertions.assertEquals(collectionFrequencySeconds, powerCost2.getPeriodLengthSeconds());
    }

    public static List<CurrentPower> getCurrentPowers() throws IOException {
        List<String> entries = Files.readAllLines(Path.of("src/test/resources/org/keen/solar/financial/current-power-list.csv"));

        return entries.stream()
                .map(stringEntry -> {
                    String[] components = stringEntry.split(",");
                    return new CurrentPower(Long.parseLong(components[1]),
                            Double.parseDouble(components[2]),
                            Double.parseDouble(components[3]),
                            Boolean.parseBoolean(components[4]));
                })
                .toList();
    }

    @Test
    public void givenNoApplicableTariffFound_whenCollectUncostedPowers_thenNoCostCalculated() {
        // Given
        TariffRepository tariffRepository = mock(TariffRepository.class);
        PowerCostRepository powerCostRepository = mock(PowerCostRepository.class);

        CurrentPower currentPower = new CurrentPower(1730757659L,100, -100, false);

        PowerCostCalculator calculator = new PowerCostCalculator(60, tariffRepository, powerCostRepository);

        // When
        calculator.collectUncostedPowers(currentPower);

        // Then
        verifyNoInteractions(powerCostRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 10, 15, 30, 60, 120, 180, 240, 300, 360, 600,
            720, 900, 1200, 1800, 3600})
    public void givenValidCollectionFrequency_whenNewPowerCostCalculator_thenObjectCreated(int collectionFrequencySeconds) {
        // Given

        // When
        PowerCostCalculator calculator = new PowerCostCalculator(collectionFrequencySeconds,
                mock(TariffRepository.class), mock(PowerCostRepository.class));

        // Then
        Assertions.assertNotNull(calculator);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 7, 45, 61, 420, 480, 540, 780, 840, 960, 3000,
            7200, 10800, 14400, 21600, 28800, 43200, 86400})
    public void givenInvalidCollectionFrequency_whenNewPowerCostCalculator_thenExceptionThrown(int collectionFrequencySeconds) {
        // Given

        // When/Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> new PowerCostCalculator(collectionFrequencySeconds,
                mock(TariffRepository.class), mock(PowerCostRepository.class)));

    }
}
