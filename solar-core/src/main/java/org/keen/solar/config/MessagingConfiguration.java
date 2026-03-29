package org.keen.solar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MessagingConfiguration {

    @Bean
    public JacksonJsonHttpMessageConverter javascriptHttpMessageConverter(@Autowired JsonMapper mapper) {
        // Bean needed because the Fronius API returns a response with the incorrect Content-Type
        // of "text/javascript" instead of "application/json".
        JacksonJsonHttpMessageConverter converter = new JacksonJsonHttpMessageConverter(mapper);
        List<MediaType> supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(new MediaType("text", "javascript"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
        return converter;
    }

}
