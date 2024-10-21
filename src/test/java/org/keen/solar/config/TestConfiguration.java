package org.keen.solar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Import({ObjectMapperConfiguration.class,MessagingConfiguration.class})
public class TestConfiguration {

    @Autowired
    private HttpMessageConverter<Object> converter;

    /**
     * Creates a RestTemplateBuilder with a request/response logging interceptor.
     */
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        // Using a BufferingClientHttpRequestFactory so that the logging interceptor doesn't use up the stream.
        // Code from https://objectpartners.com/2018/03/01/log-your-resttemplate-request-and-response-without-destroying-the-body/
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));

        restTemplate.getMessageConverters().add(converter);

        RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        return restTemplateBuilder;
    }
}
