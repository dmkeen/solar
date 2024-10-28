package org.keen.solar.system.fronius;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Profile("!test")
@Configuration
@EnableScheduling
@EnableAsync
public class CurrentPowerConfiguration {

    @Bean
    public CurrentPowerPersister currentPowerPersister() {
        return new CurrentPowerPersister();
    }

}
