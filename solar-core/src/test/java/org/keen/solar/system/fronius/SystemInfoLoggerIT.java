package org.keen.solar.system.fronius;

import org.junit.jupiter.api.Test;
import org.keen.solar.config.TestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import tools.jackson.core.JacksonException;

/**
 * Calls the actual Fronius inverter API
 */
@SpringJUnitConfig(classes = {TestConfiguration.class, SystemInfoLogger.class})
@TestPropertySource(locations = "/application.properties")
public class SystemInfoLoggerIT {

    @Autowired
    RestTemplateBuilder restTemplateBuilder;
    @Value("${app.inverter.host}")
    String inverterApiHost;

    @Test
    public void givenInverterIsOnline_whenLogInfo_thenInformationLogged() throws JacksonException {
        new SystemInfoLogger(restTemplateBuilder, inverterApiHost).logInfo();
    }
}
