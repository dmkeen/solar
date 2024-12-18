package org.keen.solar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides an ObjectMapper for test purposes. Put in a separate config class
 * in order to avoid a circular dependency between {@link TestConfiguration}
 * and {@link MessagingConfiguration}.
 */
@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
