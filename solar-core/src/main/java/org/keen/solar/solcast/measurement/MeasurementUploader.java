package org.keen.solar.solcast.measurement;

import org.keen.solar.solcast.measurement.dal.MeasurementDao;
import org.keen.solar.system.dal.CurrentPowerDao;
import org.keen.solar.system.domain.CurrentPower;
import org.keen.solar.solcast.measurement.domain.Measurement;
import org.keen.solar.solcast.measurement.domain.MeasurementResponse;
import org.keen.solar.solcast.measurement.domain.Measurements;
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
    private final CurrentPowerDao currentPowerDao;
    private final MeasurementDao measurementDao;

    @Value("${app.solcast.base-url}")
    private String solcastApiBaseUrl;

    @Value("${app.solcast.site-id}")
    private String solcastSiteId;

    @Value("${app.solcast.api-key}")
    private String solcastApiKey;

    public MeasurementUploader(RestTemplateBuilder restTemplateBuilder, CurrentPowerDao currentPowerDao,
                               MeasurementDao measurementDao) {
        this.restTemplate = restTemplateBuilder.build();
        this.currentPowerDao = currentPowerDao;
        this.measurementDao = measurementDao;
    }

    /**
     * Uploads all measurements not yet uploaded to Solcast
     */
    public void uploadAll() {
        long lastUploadedEpochTimestamp = measurementDao.getLastUploadedEpochTimestamp();
        List<CurrentPower> currentPowerNotUploaded = currentPowerDao.getStartingFrom(lastUploadedEpochTimestamp);
        doUpload(currentPowerNotUploaded);
    }

    private void doUpload(List<CurrentPower> currentPowerNotUploaded) {
        if (currentPowerNotUploaded.isEmpty()) {
            logger.info("Nothing to upload");
            return;
        }
        logger.debug("Converting list of {} CurrentPower to Measurement", currentPowerNotUploaded.size());
        List<Measurement> measurementsToUpload = convertToMeasurements(currentPowerNotUploaded);
        // Remove any measurements with a period end in the future
        measurementsToUpload = measurementsToUpload.stream()
                .filter(measurement -> measurement.getPeriod_end().isBefore(OffsetDateTime.now(ZoneOffset.UTC)))
                .collect(Collectors.toList());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(solcastApiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> httpEntity = new HttpEntity<>(new Measurements(measurementsToUpload), headers);

        String url = solcastApiBaseUrl + "/rooftop_sites/" + solcastSiteId + "/measurements";
        int numberOfMeasurements = measurementsToUpload.size();
        logger.info("Uploading {} solar generation measurement{} to {}", numberOfMeasurements,
                numberOfMeasurements > 1 ? "s" : "", url);

        // Docs say a 400 is returned if a single measurement is posted and is invalid
        // If multiple measurements are posted, a 200 is returned along with the valid measurements
        ResponseEntity<MeasurementResponse> measurementResponse = restTemplate.exchange(url,
                HttpMethod.POST, httpEntity, MeasurementResponse.class);

        if (handleErrors(measurementResponse)) return;
        logger.debug("Uploaded successfully");

        MeasurementResponse responseBody = measurementResponse.getBody();
        updateRepository(measurementsToUpload, responseBody == null ? new ArrayList<>() : responseBody.getMeasurements());
        logger.debug("Repository updated");
    }

    /**
     * Updates the repository for successfully uploaded CurrentPowers.
     *
     * @param measurementsToUpload    list of Measurements submitted for upload
     * @param returnedMeasurements    list of Measurements successfully uploaded
     */
    private void updateRepository(List<Measurement> measurementsToUpload, List<Measurement> returnedMeasurements) {
        Optional<Measurement> max = returnedMeasurements.stream()
                .max(Comparator.comparing(Measurement::getPeriod_end));
        if (max.isPresent()) {
            // Update the last uploaded epoch timestamp in the repository
            measurementDao.setLastUploadedEpochTimestamp(max.get().getPeriod_end().toEpochSecond());
            logger.debug("Updated last uploaded epoch timestamp to {}", max.get().getPeriod_end().toEpochSecond());
        } else {
            logger.warn("No measurements were returned from the upload, cannot update last uploaded timestamp");
        }
        // Log which measurements were in error
        measurementsToUpload.removeAll(returnedMeasurements);
        if (!measurementsToUpload.isEmpty()) {
            logger.warn("The following measurements were not uploaded successfully: {}", measurementsToUpload);
        }
    }

    /**
     * Handles error scenarios.
     *
     * @param measurementResponse response from measurements API
     * @return true if there were errors
     */
    private boolean handleErrors(ResponseEntity<MeasurementResponse> measurementResponse) {
        if (measurementResponse.getStatusCode().isError()) {
            logger.error("Error response from measurements API: {}", measurementResponse.getStatusCode());
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
                .collect(Collectors.groupingBy(currentPower -> currentPower.epochTimestamp() / groupBySeconds));
        return currentPowerByPeriod.entrySet().stream().map(listEntry -> {
            List<CurrentPower> powerList = listEntry.getValue();
            // Calculate average power generated for this 5 minute period
            // Note that CurrentPower generation is in Watts while Measurement is in Kilowatts, hence we divide by 1000
            double averageGeneration = powerList.stream().collect(Collectors.averagingDouble(CurrentPower::generation)) / 1000;
            // Create the Measurement for the *end* of the 5 minute period.
            // The key is first multiplied by groupBySeconds to get the epoch timestamp (because we divided by groupBySeconds above),
            // then we add groupBySeconds to get the end of the period.
            return new Measurement(Instant.ofEpochSecond(listEntry.getKey() * groupBySeconds)
                    .plusSeconds(groupBySeconds).atOffset(ZoneOffset.UTC), Duration.ofMinutes(5), averageGeneration, powerList);
        }).sorted(Comparator.comparing(Measurement::getPeriod_end))
                .collect(Collectors.toList());
    }

}
