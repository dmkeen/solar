package org.keen.solar.solcast.measurement;

import org.junit.jupiter.api.Test;
import org.keen.solar.system.dal.CurrentPowerRepository;
import org.keen.solar.system.domain.CurrentPower;
import org.keen.solar.solcast.measurement.domain.Measurement;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.util.Assert;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.DoubleStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MeasurementUploaderTest {

    @Test
    public void givenFiveMinutesOfCurrentPower_whenConvertToMeasurements_thenConvertedToSingleMeasurement() {
        // Given
        Instant startTime = Instant.parse("2020-06-28T21:00:00.00Z");
        ArrayList<CurrentPower> currentPowerList = generateCurrentPowerList(300, startTime);

        // When
        List<Measurement> measurements = MeasurementUploader.convertToMeasurements(currentPowerList);

        // Then
        Assert.notEmpty(measurements, "Expected at least one Measurement");
        Assert.state(measurements.size() == 1, "Expected only one Measurement");
        double expectedAverageGeneration = currentPowerList.stream().mapToDouble(CurrentPower::getGeneration).average().getAsDouble() / 1000;

        Measurement measurement = measurements.get(0);
        Assert.state(Math.abs(measurement.getTotal_power() - expectedAverageGeneration) < 0.01D,
                "Expected generation to be within 0.01 of " + expectedAverageGeneration + " but was " + measurement.getTotal_power());
        Assert.state(measurement.getPeriod().equals(Duration.ofMinutes(5)),
                "Expected duration to be 5 minutes but was: " + measurement.getPeriod());
        OffsetDateTime expectedPeriodEnd = startTime.plus(5, ChronoUnit.MINUTES).atOffset(ZoneOffset.UTC);
        Assert.state(measurement.getPeriod_end().isEqual(expectedPeriodEnd),
                "Expected period end to be " + expectedPeriodEnd + " but was " + measurement.getPeriod_end());
    }

    @Test
    public void givenTwentyMinutesOfCurrentPower_whenConvertToMeasurements_thenConvertedToFourMeasurements() {
        // Given
        Instant startTime = Instant.parse("2020-06-28T21:00:00.00Z");
        ArrayList<Measurement> expectedMeasurements = new ArrayList<>();
        ArrayList<CurrentPower> currentPowerList = new ArrayList<>();
        int numberOfMeasurements = 4;
        for (int i = 1; i <= numberOfMeasurements; i++) {
            ArrayList<CurrentPower> tempList = generateCurrentPowerList(300, startTime.plus(5 * (i - 1), ChronoUnit.MINUTES));
            currentPowerList.addAll(tempList);
            double expectedAverageGeneration = tempList.stream().mapToDouble(CurrentPower::getGeneration).average().getAsDouble() / 1000;
            OffsetDateTime expectedPeriodEnd = startTime.plus(5 * i, ChronoUnit.MINUTES).atOffset(ZoneOffset.UTC);
            expectedMeasurements.add(new Measurement(expectedPeriodEnd, Duration.ofMinutes(5), expectedAverageGeneration, tempList));
        }

        // When
        List<Measurement> measurements = MeasurementUploader.convertToMeasurements(currentPowerList);

        // Then
        Assert.notEmpty(measurements, "Expected at least one Measurement");
        Assert.state(measurements.size() == numberOfMeasurements, "Expected " + numberOfMeasurements
                + " Measurements, but found " + measurements.size());

        for (int i = 0; i < numberOfMeasurements; i++) {
            Measurement expectedMeasurement = expectedMeasurements.get(i);
            Measurement actualMeasurement = measurements.get(i);
            Assert.state(Math.abs(actualMeasurement.getTotal_power() - expectedMeasurement.getTotal_power()) < 0.01D,
                    "Expected generation to be within 0.01 of " + expectedMeasurement.getTotal_power() + " but was " + actualMeasurement.getTotal_power());
            Assert.state(actualMeasurement.getPeriod().equals(expectedMeasurement.getPeriod()),
                    "Expected duration to be 5 minutes but was: " + actualMeasurement.getPeriod());
            Assert.state(actualMeasurement.getPeriod_end().isEqual(expectedMeasurement.getPeriod_end()),
                    "Expected period end to be " + expectedMeasurement.getPeriod_end() + " but was " + actualMeasurement.getPeriod_end());
        }
    }

    @Test
    public void givenDateRange_whenUploadByDateRange_thenCorrectDateRangePassedToRepository() {
        // Given
        LocalDate startDate = LocalDate.of(2020, 7, 18);
        LocalDate endDate = LocalDate.of(2020, 7, 19);
        long startDateInUTCEpochSeconds = 1595030400L;
        long endDateInUTCEpochSeconds = 1595116800L;

        CurrentPowerRepository repository = mock(CurrentPowerRepository.class);
        when(repository.findByUploadedAndEpochTimestampBetween(eq(false), anyLong(), anyLong())).thenReturn(new ArrayList<>());
        MeasurementUploader measurementUploader = new MeasurementUploader(mock(RestTemplateBuilder.class), repository);

        // When
        measurementUploader.uploadByDateRange(startDate, endDate);

        // Then
        ArgumentCaptor<Long> captorStart = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> captorEnd = ArgumentCaptor.forClass(Long.class);
        verify(repository).findByUploadedAndEpochTimestampBetween(eq(false), captorStart.capture(), captorEnd.capture());
        Long startValue = captorStart.getValue();
        Long endValue = captorEnd.getValue();
        Assert.state(startValue.equals(startDateInUTCEpochSeconds), String.format("Expected %s to be converted to %d but was %d",
                startDate.toString(), startDateInUTCEpochSeconds, startValue));
        Assert.state(endValue.equals(endDateInUTCEpochSeconds), String.format("Expected %s to be converted to %d but was %d",
                endDate.toString(), endDateInUTCEpochSeconds, endValue));
    }

    private ArrayList<CurrentPower> generateCurrentPowerList(int listLength, Instant startTime) {
        AtomicLong atomicLong = new AtomicLong();
        ConcurrentLinkedQueue<CurrentPower> currentPowerQueue = new ConcurrentLinkedQueue<>();
        Random random = new Random();
        DoubleStream doubleStream = random.doubles(listLength, 0, 4000);
        int zoneOffsetSeconds = OffsetDateTime.now().getOffset().getTotalSeconds();
        doubleStream.forEach(value -> currentPowerQueue.add(new CurrentPower(
                startTime.plusSeconds(atomicLong.getAndAdd(1)).toEpochMilli() / 1000, zoneOffsetSeconds, 0,
                value, 500D, false)));
        return new ArrayList<>(currentPowerQueue);
    }
}
