package org.keen.solar.system.fronius;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.restclient.RestTemplateBuilder;
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
    private final String url;

    public SystemInfoLogger(RestTemplateBuilder restTemplateBuilder,
                            @Value("${app.inverter.host}") String inverterApiHost) {
        this.restTemplate = restTemplateBuilder.build();
        this.logger = LoggerFactory.getLogger(SystemInfoLogger.class);
        this.url = "http://" + inverterApiHost + "/solar_api/v1/GetLoggerInfo.cgi";
    }

    @Async
    @EventListener(classes = ApplicationReadyEvent.class)
    @Scheduled(cron = "@daily")
    public void logInfo() {
        OffsetDateTime currentApplicationTime = OffsetDateTime.now();
        Info response = restTemplate.getForObject(url, Info.class);
        if (response == null) {
            logger.info("No info found");
            return;
        }
        String timezone = response.Body.LoggerInfo.TimezoneName;
        int utcOffset = response.Body.LoggerInfo.UTCOffset;
        String dataManagerSoftwareVersion = response.Body.LoggerInfo.SWVersion;
        OffsetDateTime inverterTime = OffsetDateTime.parse(response.Head.Timestamp);
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

    record Info(Body Body, Head Head) {}
    record Body(LoggerInfo LoggerInfo) {}
    record LoggerInfo(String SWVersion, String TimezoneName, int UTCOffset) {}
    record Head(String Timestamp) {}
}
