package org.keen.solar.system.fronius;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keen.solar.config.TestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Calls the actual Fronius inverter API
 */
@ContextConfiguration(classes = {TestConfiguration.class, SystemInfoLogger.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "/application.properties")
public class SystemInfoLoggerIT {

    @Autowired
    RestTemplateBuilder restTemplateBuilder;
    @Autowired
    ObjectMapper objectMapper;
    @Value("${app.inverter.host}")
    String inverterApiHost;

    @Test
    public void givenInverterIsOnline_whenLogInfo_thenInformationLogged() throws JsonProcessingException {
        new SystemInfoLogger(restTemplateBuilder, objectMapper, inverterApiHost).logInfo();
    }
}
