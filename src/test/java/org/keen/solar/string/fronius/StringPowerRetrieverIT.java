package org.keen.solar.string.fronius;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keen.solar.config.TestConfiguration;
import org.keen.solar.string.domain.StringPower;
import org.keen.solar.string.domain.StringPowers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Calls the actual Fronius inverter API
 */
@ContextConfiguration(classes = {TestConfiguration.class, StringPowerRetriever.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "/application.properties")
public class StringPowerRetrieverIT {

    @Autowired
    private StringPowerRetriever retriever;

    @Test
    public void givenInverterIsOnline_whenRetrieveToday_thenStringPowersReturned() {
        StringPowers stringPowers = retriever.retrieveToday();

        Assertions.assertNotNull(stringPowers);
        List<StringPower> stringPowerList = stringPowers.getStringPowerList();
        Assertions.assertNotNull(stringPowerList);
        Assertions.assertFalse(stringPowerList.isEmpty());

        Collections.sort(stringPowerList, Comparator.comparing(StringPower::getPeriodEndEpoch));
        Assertions.assertEquals(OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS).toEpochSecond(), stringPowerList.get(0).getPeriodEndEpoch());

        System.out.println(stringPowerList);
    }

    @Test
    public void givenInverterIsOnline_whenRetrieveLatest_thenLatestStringPowerReturned() {
        StringPower stringPower = retriever.getLatest();

        Assertions.assertNotNull(stringPower);
        Assertions.assertTrue(stringPower.getPeriodEndEpoch() > OffsetDateTime.now().minusMinutes(6).toEpochSecond());

        System.out.println(stringPower);
    }
}
