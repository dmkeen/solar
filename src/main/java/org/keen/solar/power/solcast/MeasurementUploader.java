package org.keen.solar.power.solcast;

import org.keen.solar.power.dal.CurrentPowerRepository;
import org.keen.solar.power.domain.CurrentPower;
import org.keen.solar.power.domain.Measurement;
import org.keen.solar.power.domain.MeasurementResponse;
import org.keen.solar.power.domain.Measurements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Sends the solar power generated for a period to Solcast to assist with forecast tuning.
 * <p>
 * See <a href="https://docs.solcast.com.au/#measurements-rooftop-site">Solcast API</a>
 */
@Service
public class MeasurementUploader {

    private final Logger logger = LoggerFactory.getLogger(MeasurementUploader.class);

    private final RestTemplate restTemplate;
    private final CurrentPowerRepository repository;

    @Value("${app.solcast.base-url}")
    private String solcastApiBaseUrl;

    @Value("${app.solcast.site-id}")
    private String solcastSiteId;

    @Value("${app.solcast.api-key}")
    private String solcastApiKey;

    public MeasurementUploader(RestTemplateBuilder restTemplateBuilder, CurrentPowerRepository repository) {
        this.restTemplate = restTemplateBuilder.build();
        this.repository = repository;
    }

    /**
     * Uploads all measurements not yet uploaded to Solcast
     */
    public void upload() {
        List<CurrentPower> currentPowerNotUploaded = repository.findByUploaded(false);
        if (currentPowerNotUploaded.isEmpty()) {
            logger.info("Nothing to upload");
            return;
        }
        List<Measurement> measurementsToUpload = convertToMeasurements(currentPowerNotUploaded);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(solcastApiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> httpEntity = new HttpEntity<>(new Measurements(measurementsToUpload), headers);

        String url = solcastApiBaseUrl + "/rooftop_sites/" + solcastSiteId + "/measurements";
        int numberOfMeasurements = measurementsToUpload.size();
        logger.info("Uploading " + numberOfMeasurements + " solar generation measurement" + (numberOfMeasurements > 1 ? "s" : "") + " to " + url);

        // Docs say a 400 is returned if a single measurement is posted and is invalid
        // If multiple measurements are posted, a 200 is returned along with the valid measurements
        ResponseEntity<MeasurementResponse> measurementResponse = restTemplate.exchange(url,
                HttpMethod.POST, httpEntity, MeasurementResponse.class);

        if (handleErrors(measurementResponse)) return;

        updateRepository(currentPowerNotUploaded, measurementsToUpload, measurementResponse.getBody().getMeasurements());
    }

    /**
     * Updates the repository for successfully uploaded CurrentPowers.
     *
     * @param currentPowerNotUploaded list of CurrentPower submitted for upload
     * @param measurementsToUpload    list of Measurements submitted for upload
     * @param returnedMeasurements    list of Measurements successfully uploaded
     */
    private void updateRepository(List<CurrentPower> currentPowerNotUploaded, List<Measurement> measurementsToUpload,
                                  List<Measurement> returnedMeasurements) {
        if (returnedMeasurements.size() == measurementsToUpload.size()) {
            // Update repository to say that we've successfully uploaded the measurements
            currentPowerNotUploaded.parallelStream().forEach(currentPower -> currentPower.setUploaded(true));
            repository.saveAll(currentPowerNotUploaded);
            return;
        }
        // Only update repository for successfully uploaded measurements
        List<Measurement> measurementsUploaded = new ArrayList<>(measurementsToUpload);
        measurementsUploaded.retainAll(returnedMeasurements);
        measurementsUploaded.forEach(measurement -> repository.saveAll(measurement.getSource()));
        // Log which measurements were in error
        measurementsToUpload.removeAll(returnedMeasurements);
        logger.warn("The following measurements were not uploaded successfully: " + measurementsToUpload.toString());
    }

    /**
     * Handles error scenarios.
     *
     * @param measurementResponse response from measurements API
     * @return true if there were errors
     */
    private boolean handleErrors(ResponseEntity<MeasurementResponse> measurementResponse) {
        if (measurementResponse.getStatusCode().isError()) {
            logger.error("Error response from measurements API: " + measurementResponse.getStatusCode());
            return true;
        }
        if (measurementResponse.getBody() == null) {
            logger.error("Unexpected error - empty response body");
            return true;
        }
        return false;
    }

    /**
     * Converts a list of {@link CurrentPower} to a list of {@link Measurement} by averaging over 5 minute periods.
     *
     * @param currentPowerList list of CurrentPower
     * @return list of Measurement
     */
    static List<Measurement> convertToMeasurements(List<CurrentPower> currentPowerList) {
        // Reference: https://stackoverflow.com/a/34438690

        // Group into 5 minute blocks
        int groupByMinutes = 5;
        int groupBySeconds = groupByMinutes * 60;
        Map<Long, List<CurrentPower>> currentPowerByPeriod = currentPowerList.stream()
                .collect(Collectors.groupingBy(currentPower -> currentPower.getInverterEpochTimestamp() / groupBySeconds));
        return currentPowerByPeriod.entrySet().stream().map(listEntry -> {
            List<CurrentPower> powerList = listEntry.getValue();
            // Calculate average power generated for this 5 minute period
            // Note that CurrentPower generation is in Watts while Measurement is in Kilowatts, hence we divide by 1000
            double averageGeneration = powerList.stream().collect(Collectors.averagingDouble(CurrentPower::getGeneration)) / 1000;
            // Create the Measurement for the *end* of the 5 minute period.
            // The key is first multiplied by groupBySeconds to get the epoch timestamp (because we divided by groupBySeconds above),
            // then we add groupBySeconds to get the end of the period.
            return new Measurement(Instant.ofEpochSecond(listEntry.getKey() * groupBySeconds)
                    .plusSeconds(groupBySeconds).atOffset(ZoneOffset.UTC), Duration.ofMinutes(5), averageGeneration, powerList);
        }).sorted(Comparator.comparing(Measurement::getPeriod_end))
                .collect(Collectors.toList());
    }

}
