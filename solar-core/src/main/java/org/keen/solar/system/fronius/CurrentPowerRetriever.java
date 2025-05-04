package org.keen.solar.system.fronius;

import org.keen.solar.system.domain.CurrentPower;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Retrieves the current power values from the inverter
 */
@Service
public class CurrentPowerRetriever {

    private final RestTemplate restTemplate;

    @Value("${app.inverter.host}")
    private String inverterApiHost;

    public CurrentPowerRetriever(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public CurrentPower retrieve() {
        return restTemplate.getForObject("http://" + inverterApiHost + "/solar_api/v1/GetPowerFlowRealtimeData.fcgi",
                CurrentPower.class);
    }
}
