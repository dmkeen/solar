package org.keen.solar.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Import({ObjectMapperConfiguration.class, MessagingConfiguration.class})
public class TestConfiguration {

    @Bean
    public RestTemplateBuilder restTemplateBuilder(HttpMessageConverter<Object> converter,
                                                   @Value("${test.debug.logging.enable}") boolean enableLogging) {
        return new RestTemplateBuilder(restTemplateCustomizer(converter, enableLogging));
    }

    @Bean
    public static RestTemplateCustomizer restTemplateCustomizer(HttpMessageConverter<Object> converter,
                                                                @Value("${test.debug.logging.enable}") boolean enableLogging) {
        return restTemplate -> {
            if (enableLogging) {
                LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
                Logger logger = loggerContext.getLogger(RequestResponseLoggingInterceptor.class);
                logger.setLevel(Level.DEBUG);
                // Using a BufferingClientHttpRequestFactory so that the logging interceptor doesn't use up the stream.
                // Code from https://objectpartners.com/2018/03/01/log-your-resttemplate-request-and-response-without-destroying-the-body/
                restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
                restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
            }
            List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
            messageConverters = messageConverters.stream()
                    .filter(messageConverter -> !(messageConverter instanceof MappingJackson2HttpMessageConverter))
                    .collect(Collectors.toCollection(ArrayList::new));
            messageConverters.add(converter);
            restTemplate.setMessageConverters(messageConverters);
        };
    }
}
