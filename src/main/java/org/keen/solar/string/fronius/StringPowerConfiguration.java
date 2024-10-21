package org.keen.solar.string.fronius;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
public class StringPowerConfiguration {

    @Bean
    public StringPowerPersister stringPowerPersister() {
        return new StringPowerPersister();
    }
}
