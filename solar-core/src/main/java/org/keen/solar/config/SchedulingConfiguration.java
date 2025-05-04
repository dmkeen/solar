package org.keen.solar.config;

import org.springframework.boot.task.SimpleAsyncTaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

@Configuration
public class SchedulingConfiguration {

    @Bean
    public SimpleAsyncTaskScheduler solarAppAsyncScheduler(SimpleAsyncTaskSchedulerBuilder builder) {
        return builder
                .threadNamePrefix("solar-async-")
                .build();
    }
}
