package org.keen.solar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MessagingConfiguration {

    @Autowired
    private ObjectMapper mapper;

    @Bean
    public MappingJackson2HttpMessageConverter javascriptHttpMessageConverter() {
        // Bean needed because the Fronius API returns a response with the incorrect Content-Type
        // of "text/javascript" instead of "application/json".
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
        List<MediaType> supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(new MediaType("text", "javascript"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
        return converter;
    }

}
