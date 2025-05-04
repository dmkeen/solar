package org.keen.solar.solcast.measurement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.keen.solar.solcast.measurement.domain.Measurement;
import org.keen.solar.solcast.measurement.domain.MeasurementResponse;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public class MeasurementResponseDeserializerTest {

    // Verifies that a MeasurementResponse can be correctly deserialized by Jackson with the correct configuration
    @Test
    public void givenMeasurementResponseString_whenReadValue_thenMeasurementResponseDeserialized() throws JsonProcessingException {
        // Given
        String siteId = "my-site-id";
        String periodEndString = "2020-07-04T21:00:00.0000000Z";
        String periodString = "PT5M";
        double totalPower = 1.2345;
        String measurementResponseString = "{\"site_resource_id\":\"" + siteId + "\",\"measurements\":[{\"period_end\":\""
                + periodEndString + "\",\"period\":\"" + periodString + "\",\"total_power\":" + totalPower + "}]}";

        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();

        // When
        MeasurementResponse measurementResponse = objectMapper.readValue(measurementResponseString, MeasurementResponse.class);

        // Then
        Assert.notNull(measurementResponse, "Expected non-null MeasurementResponse");
        Assert.state(measurementResponse.getSite_resource_id().equals(siteId),
                String.format("Expected site id to be %s but was %s", siteId, measurementResponse.getSite_resource_id()));
        List<Measurement> measurements = measurementResponse.getMeasurements();
        Assert.notEmpty(measurements, "Expected at least one Measurement");
        Assert.state(measurements.size() == 1, "Expected a single Measurement");
        Measurement measurement = measurements.get(0);
        Assert.state(measurement.getPeriod_end().equals(OffsetDateTime.parse(periodEndString)),
                String.format("Expected period end to be %s but was %s", periodEndString, measurement.getPeriod_end()));
        Assert.state(measurement.getPeriod().equals(Duration.parse(periodString)),
                String.format("Expected period to be %s but was %s", periodString, measurement.getPeriod()));
        Assert.state(Math.abs(measurement.getTotal_power() - totalPower) < 0.0001D,
                "Expected generation to be within 0.0001 of " + totalPower + " but was " + measurement.getTotal_power());
    }
}
