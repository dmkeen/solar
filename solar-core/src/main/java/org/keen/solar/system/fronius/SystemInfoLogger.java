package org.keen.solar.system.fronius;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * Logs various information about the PV system, both at application startup
 * and daily at midnight.
 */
@Profile("!test")
@Service
public class SystemInfoLogger {

    private final Logger logger;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.inverter.host}")
    private String inverterApiHost;

    public SystemInfoLogger(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
        this.logger = LoggerFactory.getLogger(SystemInfoLogger.class);
    }

    @Async
    @EventListener(classes = ApplicationReadyEvent.class)
    @Scheduled(cron = "@daily")
    public void logInfo() throws JsonProcessingException {
        OffsetDateTime currentApplicationTime = OffsetDateTime.now();
        String response = restTemplate.getForObject("http://" + inverterApiHost + "/solar_api/v1/GetLoggerInfo.cgi", String.class);
        JsonNode jsonNode = objectMapper.readTree(response);
        String timezone = jsonNode.at("/Body/LoggerInfo/TimezoneName").textValue();
        int utcOffset = jsonNode.at("/Body/LoggerInfo/UTCOffset").intValue();
        String dataManagerSoftwareVersion = jsonNode.at("/Body/LoggerInfo/SWVersion").textValue();
        OffsetDateTime inverterTime = OffsetDateTime.parse(jsonNode.at("/Head/Timestamp").textValue());
        long timeDifferenceSeconds = Duration.between(currentApplicationTime, inverterTime).getSeconds();

        logger.info("""
                        Inverter info:
                        Timezone: {}
                        UTC offset (seconds): {}
                        Datamanager software version: {}
                        Time difference to application (seconds): {}{}""",
                timezone, utcOffset, dataManagerSoftwareVersion,
                timeDifferenceSeconds > 0 ? "+" : "",
                timeDifferenceSeconds);
    }
}
