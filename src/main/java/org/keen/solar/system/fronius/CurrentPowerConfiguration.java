package org.keen.solar.system.fronius;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
public class CurrentPowerConfiguration {

    @Bean
    public CurrentPowerPersister currentPowerPersister() {
        return new CurrentPowerPersister();
    }

}
