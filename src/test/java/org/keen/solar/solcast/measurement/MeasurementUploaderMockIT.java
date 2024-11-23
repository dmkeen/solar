package org.keen.solar.solcast.measurement;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keen.solar.config.TestConfiguration;
import org.keen.solar.system.dal.CurrentPowerRepository;
import org.keen.solar.system.domain.CurrentPower;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.keen.solar.solcast.measurement.MeasurementSerializer.PERIOD_END_FORMAT;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Integration test with a mock Solcast service
 */
// Without the @JsonTest annotation, the response from the MockRestServiceServer is
// not deserialized correctly and the response body is empty. I was unable to achieve
// deserialization with @AutoConfigureJson and @AutoConfigureJsonTesters alone.
// But including @JsonTest switches off the request/response logging and the assertion
// logging from MockRestServiceServer.
@JsonTest
@ContextConfiguration(classes = {TestConfiguration.class, MeasurementUploader.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "/application.properties")
public class MeasurementUploaderMockIT {

    @MockBean
    private CurrentPowerRepository currentPowerRepository;

    @Autowired
    private MeasurementUploader measurementUploader;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Value("${app.solcast.api-key}")
    private String solcastApiKey;

    @Value("${app.solcast.site-id}")
    private String solcastSiteId;

    @Test
    public void givenSingleCurrentPower_whenUploadAll_thenMeasurementUploadedAndSavedToRepository() {
        // Given
        List<CurrentPower> currentPowerList = new ArrayList<>();
        double generationWatts = 1245D;
        Instant now = Instant.now().minus(10, ChronoUnit.MINUTES);
        long inverterEpochTimestamp = now.toEpochMilli() / 1000;
        currentPowerList.add(new CurrentPower(inverterEpochTimestamp, generationWatts, 0D, false));
        when(currentPowerRepository.findByUploaded(false)).thenReturn(currentPowerList);

        RestTemplate restTemplate = restTemplateBuilder.build();
        MockRestServiceServer restServiceServer = MockRestServiceServer.bindTo(restTemplate).build();

        Instant nowRoundedToNextFiveMinuteBoundary = now
                .truncatedTo(ChronoUnit.HOURS)
                .plus(5 * (now.atOffset(ZoneOffset.UTC).getMinute() / 5), ChronoUnit.MINUTES)
                .plus(5, ChronoUnit.MINUTES);
        String expectedPeriodEnd = OffsetDateTime.ofInstant(nowRoundedToNextFiveMinuteBoundary, ZoneId.of("Z")).format(DateTimeFormatter.ofPattern(PERIOD_END_FORMAT));
        restServiceServer.expect(requestTo("https://api.solcast.com.au/rooftop_sites/" + solcastSiteId + "/measurements"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + solcastApiKey))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.measurement", Matchers.anything()))
                .andExpect(jsonPath("$.measurement.period_end", Matchers.is(expectedPeriodEnd)))
                .andExpect(jsonPath("$.measurement.period", Matchers.is(Duration.ofMinutes(5).toString())))
                .andExpect(jsonPath("$.measurement.total_power", Matchers.is(generationWatts / 1000)))
                .andRespond(withSuccess("{\"site_resource_id\":\"test-site-id\",\"measurements\":[{\"period_end\":\""
                        + expectedPeriodEnd + "\",\"period\":\"PT5M\",\"total_power\":" + generationWatts / 1000 + "}]}", MediaType.APPLICATION_JSON));

        // When
        measurementUploader.uploadAll();

        // Then
        restServiceServer.verify();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CurrentPower>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(currentPowerRepository).saveAll(argumentCaptor.capture());
        List<CurrentPower> captorValue = argumentCaptor.getValue();
        Assert.notEmpty(captorValue, "Expected non-empty list to be saved");
        CurrentPower currentPower = captorValue.get(0);
        Assert.state(currentPower.isUploaded(), "Expected CurrentPower to have 'uploaded' flag set");
    }

    @Test
    public void givenMultipleCurrentPower5MinsApart_whenUploadAll_thenMeasurementsUploadedAndSavedToRepository() {
        // Given
        List<CurrentPower> currentPowerList = new ArrayList<>();
        double generationWatts = 1245D;
        Instant now = Instant.now().minus(20, ChronoUnit.MINUTES);
        long inverterEpochTimestamp = now.toEpochMilli() / 1000;
        currentPowerList.add(new CurrentPower(inverterEpochTimestamp, generationWatts, 0D, false));
        Instant nowPlus5Mins = now.plus(5, ChronoUnit.MINUTES);
        long inverterEpochTimestampPlus5Mins = nowPlus5Mins.toEpochMilli() / 1000;
        currentPowerList.add(new CurrentPower(inverterEpochTimestampPlus5Mins, generationWatts, 0D, false));
        when(currentPowerRepository.findByUploaded(false)).thenReturn(currentPowerList);

        RestTemplate restTemplate = restTemplateBuilder.build();
        MockRestServiceServer restServiceServer = MockRestServiceServer.bindTo(restTemplate).build();

        Instant nowRoundedToNextFiveMinBoundary = now
                .truncatedTo(ChronoUnit.HOURS)
                .plus(5 * (now.atOffset(ZoneOffset.UTC).getMinute() / 5), ChronoUnit.MINUTES)
                .plus(5, ChronoUnit.MINUTES);
        String expectedPeriodEnd = OffsetDateTime.ofInstant(nowRoundedToNextFiveMinBoundary, ZoneId.of("Z")).format(DateTimeFormatter.ofPattern(PERIOD_END_FORMAT));
        Instant nowRoundedToNextFiveMinBoundaryPlus5 = nowRoundedToNextFiveMinBoundary.plus(5, ChronoUnit.MINUTES);
        String expectedPeriodEndPlus5 = OffsetDateTime.ofInstant(nowRoundedToNextFiveMinBoundaryPlus5, ZoneId.of("Z")).format(DateTimeFormatter.ofPattern(PERIOD_END_FORMAT));
        restServiceServer.expect(requestTo("https://api.solcast.com.au/rooftop_sites/" + solcastSiteId + "/measurements"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + solcastApiKey))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.measurements", Matchers.anything()))
                .andExpect(jsonPath("$.measurements[0].period_end", Matchers.is(expectedPeriodEnd)))
                .andExpect(jsonPath("$.measurements[0].period", Matchers.is(Duration.ofMinutes(5).toString())))
                .andExpect(jsonPath("$.measurements[0].total_power", Matchers.is(generationWatts / 1000)))
                .andExpect(jsonPath("$.measurements[1].period_end", Matchers.is(expectedPeriodEndPlus5)))
                .andExpect(jsonPath("$.measurements[1].period", Matchers.is(Duration.ofMinutes(5).toString())))
                .andExpect(jsonPath("$.measurements[1].total_power", Matchers.is(generationWatts / 1000)))
                .andRespond(withSuccess("{\"site_resource_id\":\"test-site-id\",\"measurements\":[{\"period_end\":\""
                                + expectedPeriodEnd + "\",\"period\":\"PT5M\",\"total_power\":" + generationWatts / 1000 + "},"
                                + "{\"period_end\":\"" + expectedPeriodEndPlus5 + "\",\"period\":\"PT5M\",\"total_power\":"
                                + generationWatts / 1000 + "}]}", MediaType.APPLICATION_JSON));

        // When
        measurementUploader.uploadAll();

        // Then
        restServiceServer.verify();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CurrentPower>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(currentPowerRepository, times(2)).saveAll(argumentCaptor.capture());
        List<CurrentPower> captorValue = argumentCaptor.getAllValues().stream().flatMap(List::stream).toList();
        Assert.notEmpty(captorValue, "Expected non-empty list to be saved");
        Assert.state(captorValue.size() == 2, "Expected 2 CurrentPowers to be saved");
        CurrentPower currentPower = captorValue.get(0);
        Assert.state(currentPower.isUploaded(), "Expected CurrentPower to have 'uploaded' flag set");
        currentPower = captorValue.get(1);
        Assert.state(currentPower.isUploaded(), "Expected CurrentPower to have 'uploaded' flag set");
    }
}
