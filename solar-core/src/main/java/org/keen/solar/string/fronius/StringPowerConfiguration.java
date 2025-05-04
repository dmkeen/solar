package org.keen.solar.string.fronius;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
public class StringPowerConfiguration {

    @Bean
    public StringPowerPersister stringPowerPersister() {
        return new StringPowerPersister();
    }
}
