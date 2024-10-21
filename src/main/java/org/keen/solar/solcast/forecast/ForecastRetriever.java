package org.keen.solar.solcast.forecast;

import org.keen.solar.solcast.forecast.domain.Forecasts;
import org.keen.solar.solcast.forecast.domain.GenerationForecast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Retrieves the solar panel forecast from Solcast.
 *
 * See <a href="https://docs.solcast.com.au/#forecasts-rooftop-site">Solcast API</a>
 */
@Service
public class ForecastRetriever {

    private final Logger logger = LoggerFactory.getLogger(ForecastRetriever.class);

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

        String url = solcastApiBaseUrl + "/rooftop_sites/" + solcastSiteId + "/forecasts?hours=168";
        logger.info("Retrieving solar forecast from " + url);

        ResponseEntity<Forecasts> forecastResponse = restTemplate.exchange(url,
                HttpMethod.GET, httpEntity, Forecasts.class);
        return forecastResponse.getBody().getForecasts();
    }
}
