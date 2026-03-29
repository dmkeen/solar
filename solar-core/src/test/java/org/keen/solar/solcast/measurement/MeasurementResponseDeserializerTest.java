package org.keen.solar.solcast.measurement;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.keen.solar.solcast.measurement.domain.Measurement;
import org.keen.solar.solcast.measurement.domain.MeasurementResponse;
import org.springframework.util.Assert;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public class MeasurementResponseDeserializerTest {

    // Verifies that a MeasurementResponse can be correctly deserialized by Jackson with the correct configuration
    @Test
    public void givenMeasurementResponseString_whenReadValue_thenMeasurementResponseDeserialized() throws JacksonException {
        // Given
        String siteId = "my-site-id";
        String periodEndString = "2020-07-04T21:00:00.0000000Z";
        String periodString = "PT5M";
        double totalPower = 1.2345;
        String measurementResponseString = "{\"site_resource_id\":\"" + siteId + "\",\"measurements\":[{\"period_end\":\""
                + periodEndString + "\",\"period\":\"" + periodString + "\",\"total_power\":" + totalPower + "}]}";

        ObjectMapper objectMapper = JsonMapper.builder()
                //.addModule(new JavaTimeModule())
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();

        // When
        MeasurementResponse measurementResponse = objectMapper.readValue(measurementResponseString, MeasurementResponse.class);

        // Then
        Assert.notNull(measurementResponse, "Expected non-null MeasurementResponse");
        Assert.state(measurementResponse.getSite_resource_id().equals(siteId),
                "Expected site id to be %s but was %s".formatted(siteId, measurementResponse.getSite_resource_id()));
        List<Measurement> measurements = measurementResponse.getMeasurements();
        Assert.notEmpty(measurements, "Expected at least one Measurement");
        Assert.state(measurements.size() == 1, "Expected a single Measurement");
        Measurement measurement = measurements.get(0);
        Assert.state(measurement.getPeriod_end().equals(OffsetDateTime.parse(periodEndString)),
                "Expected period end to be %s but was %s".formatted(periodEndString, measurement.getPeriod_end()));
        Assert.state(measurement.getPeriod().equals(Duration.parse(periodString)),
                "Expected period to be %s but was %s".formatted(periodString, measurement.getPeriod()));
        Assert.state(Math.abs(measurement.getTotal_power() - totalPower) < 0.0001D,
                "Expected generation to be within 0.0001 of " + totalPower + " but was " + measurement.getTotal_power());
    }
}
