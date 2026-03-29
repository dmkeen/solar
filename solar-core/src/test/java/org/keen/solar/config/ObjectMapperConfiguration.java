package org.keen.solar.config;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import org.keen.solar.system.domain.CurrentPower;
import org.keen.solar.system.fronius.GetPowerFlowRealtimeDataDeserializer;
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
    public JsonMapper objectMapper() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CurrentPower.class, new GetPowerFlowRealtimeDataDeserializer(CurrentPower.class));

        return JsonMapper.builder()
                .addModule(module)
                .build();
    }

}
