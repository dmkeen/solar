package org.keen.solar.string.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.keen.solar.string.fronius.StringPowersDeserializer;

import java.util.List;

/**
 * Class to simplify deserialization of response from Fronius GetArchiveData.cgi call
 */
@JsonDeserialize(using = StringPowersDeserializer.class)
public class StringPowers {

    private final List<StringPower> stringPowerList;

    public StringPowers(List<StringPower> stringPowerList) {
        this.stringPowerList = stringPowerList;
    }

    public List<StringPower> getStringPowerList() {
        return stringPowerList;
    }
}
