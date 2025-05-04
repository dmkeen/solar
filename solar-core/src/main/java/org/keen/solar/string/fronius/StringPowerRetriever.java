package org.keen.solar.string.fronius;

import org.keen.solar.string.domain.StringPower;
import org.keen.solar.string.domain.StringPowers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;

/**
 * Retrieves the power output per "string" of panels
 */
@Service
public class StringPowerRetriever {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final RestTemplate restTemplate;

    @Value("${app.inverter.host}")
    private String inverterApiHost;

    public StringPowerRetriever(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public StringPowers retrieveToday() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime startOfToday = now.truncatedTo(ChronoUnit.DAYS);
        OffsetDateTime endOfToday = startOfToday.plusDays(1).minusSeconds(1);
        return retrieve(startOfToday, endOfToday);
    }

    public StringPower getLatest() {
        OffsetDateTime now = OffsetDateTime.now();
        // As per the API documentation, data is recorded in intervals between 5 and 30 minutes.
        // We retrieve the last 31 minutes to ensure we get at least one data point.
        OffsetDateTime thirtyOneMinutesAgo = now.minusMinutes(31);
        StringPowers stringPowers = retrieve(thirtyOneMinutesAgo, now);
        Optional<StringPower> latestStringPower = stringPowers.getStringPowerList()
                .stream().max(Comparator.comparing(StringPower::getPeriodEndEpoch));
        if (latestStringPower.isPresent()) {
            return latestStringPower.get();
        }
        throw new RuntimeException("No data returned by API");
    }

    public StringPowers retrieve(OffsetDateTime start, OffsetDateTime end) {
        return restTemplate.getForObject("http://" + inverterApiHost + "/solar_api/v1/GetArchiveData.cgi" +
                        "?Scope=System&StartDate={start}&EndDate={end}&Channel=Current_DC_String_1" +
                        "&Channel=Current_DC_String_2&Channel=Voltage_DC_String_1&Channel=Voltage_DC_String_2",
                StringPowers.class, FORMATTER.format(start), FORMATTER.format(end));
    }

}
