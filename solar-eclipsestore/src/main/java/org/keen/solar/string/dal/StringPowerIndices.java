package org.keen.solar.string.dal;

import org.eclipse.store.gigamap.types.IndexerLong;
import org.keen.solar.string.domain.StringPower;

public class StringPowerIndices {

    private StringPowerIndices() {}

    public static final IndexerLong<StringPower> periodEndEpoch = new IndexerLong.Abstract<>() {
        @Override
        protected Long getLong(StringPower stringPower) {
            return stringPower.periodEndEpoch();
        }

        @Override
        public String name() {
            return "stringPowerPeriodEndEpoch";
        }
    };
}
