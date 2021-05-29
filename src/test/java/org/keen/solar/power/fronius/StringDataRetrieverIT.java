package org.keen.solar.power.fronius;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keen.solar.TestConfiguration;
import org.keen.solar.power.domain.StringPower;
import org.keen.solar.power.domain.StringPowers;
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
@ContextConfiguration(classes = {TestConfiguration.class, StringDataRetriever.class})
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "/application.properties")
public class StringDataRetrieverIT {

    @Autowired
    private StringDataRetriever retriever;

    @Test
    public void givenInverterIsOnline_whenRetrieveToday_thenStringPowersReturned() {
        StringPowers stringPowers = retriever.retrieveToday();

        Assertions.assertNotNull(stringPowers);
        List<StringPower> stringPowerList = stringPowers.getStringPowerList();
        Assertions.assertNotNull(stringPowerList);
        Assertions.assertFalse(stringPowerList.isEmpty());

        Collections.sort(stringPowerList, Comparator.comparing(StringPower::getPeriodEnd));
        Assertions.assertEquals(OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS), stringPowerList.get(0).getPeriodEnd());

        System.out.println(stringPowerList);
    }

    @Test
    public void givenInverterIsOnline_whenRetrieveLatest_thenLatestStringPowerReturned() {
        StringPower stringPower = retriever.getLatest();

        Assertions.assertNotNull(stringPower);
        Assertions.assertTrue(stringPower.getPeriodEnd().isAfter(OffsetDateTime.now().minusMinutes(6)));

        System.out.println(stringPower);
    }
}
