package org.keen.solar.forecast.solcast;

import org.junit.jupiter.api.Test;
import org.keen.solar.config.ApplicationConfiguration;
import org.keen.solar.forecast.dal.ForecastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

/**
 * Calls the actual Solcast forecast API and inserts data into the real database.
 * Note that there is a limited number of calls allowed per day.
 */
//@ActiveProfiles("test-mysql")
@TestPropertySource(locations = "/application-test-mysql.properties")
//@DataJdbcTest(properties = "spring.test.database.replace=none")
@SpringBootTest
@EnableAutoConfiguration(exclude = ApplicationConfiguration.class)
public class ForecastPersisterIT {

    @org.springframework.boot.test.context.TestConfiguration
    public static class testConfig {

        @Bean
        public ForecastPersister forecastPersister() {
            return new ForecastPersister();
        }
    }

    @Autowired
    private ForecastPersister persister;

    @Autowired
    private ForecastRepository repository;

    @Test
    public void givenSolcastAPIAndDatabaseOnline_whenRetrieveAndPersist_thenForecastIsPersisted() {
        persister.retrieveAndPersist();
    }

}
