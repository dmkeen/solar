package org.keen.solar.system.fronius;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keen.solar.config.TestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
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
    SystemInfoLogger infoLogger;

    @Test
    public void givenInverterIsOnline_whenLogInfo_thenInformationLogged() throws JsonProcessingException {
        infoLogger.logInfo();
    }
}
