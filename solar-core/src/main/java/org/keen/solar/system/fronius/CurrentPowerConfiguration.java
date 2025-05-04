package org.keen.solar.system.fronius;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
public class CurrentPowerConfiguration {

    @Bean
    public CurrentPowerPersister currentPowerPersister() {
        return new CurrentPowerPersister();
    }

}
