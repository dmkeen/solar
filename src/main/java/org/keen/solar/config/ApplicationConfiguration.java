package org.keen.solar.config;

import org.keen.solar.power.fronius.CurrentPowerPersister;
import org.keen.solar.power.fronius.StringPowerPersister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
public class ApplicationConfiguration {

    @Bean
    public CurrentPowerPersister currentPowerPersister() {
        return new CurrentPowerPersister();
    }

    @Bean
    public StringPowerPersister stringPowerPersister() {
        return new StringPowerPersister();
    }
}
