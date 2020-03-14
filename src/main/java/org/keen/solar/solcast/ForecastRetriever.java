package org.keen.solar.solcast;

import org.keen.solar.domain.Forecasts;
import org.keen.solar.domain.GenerationForecast;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class ForecastRetriever {

    private final RestTemplate restTemplate;

    @Value("${app.solcast.base-url}")
    private String solcastApiBaseUrl;

    @Value("${app.solcast.site-id}")
    private String solcastSiteId;

    @Value("${app.solcast.api-key}")
    private String solcastApiKey;

    public ForecastRetriever(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<GenerationForecast> retrieve() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(solcastApiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<Forecasts> forecastResponse = restTemplate.exchange(solcastApiBaseUrl + "/rooftop_sites/" + solcastSiteId + "/forecasts",
                HttpMethod.GET, httpEntity, Forecasts.class);
        return forecastResponse.getBody().getForecasts();
    }
}
