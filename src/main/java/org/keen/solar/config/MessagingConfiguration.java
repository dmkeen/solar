package org.keen.solar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;

@Configuration
public class MessagingConfiguration {

    @Bean
    public HttpMessageConverter<Object> javascriptHttpMessageConverter() {
        // Bean needed because the Fronius API returns a response with the incorrect Content-Type
        // of "text/javascript" instead of "application/json".
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.parseMediaType("text/javascript")));
        return messageConverter;
    }

}
