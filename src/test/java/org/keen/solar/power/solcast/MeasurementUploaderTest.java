package org.keen.solar.power.solcast;

import org.junit.jupiter.api.Test;
import org.keen.solar.power.domain.CurrentPower;
import org.keen.solar.power.domain.Measurement;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.DoubleStream;

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

    private ArrayList<CurrentPower> generateCurrentPowerList(int listLength, Instant startTime) {
        AtomicLong atomicLong = new AtomicLong();
        ConcurrentLinkedQueue<CurrentPower> currentPowerQueue = new ConcurrentLinkedQueue<>();
        Random random = new Random();
        DoubleStream doubleStream = random.doubles(listLength, 0, 4000);
        doubleStream.forEach(value -> currentPowerQueue.add(new CurrentPower("",
                startTime.plusSeconds(atomicLong.getAndAdd(1)).toEpochMilli() / 1000, "",
                value, 500D, false)));
        return new ArrayList<>(currentPowerQueue);
    }
}
