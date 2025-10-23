package org.keen.solar.system.dal;

import org.eclipse.store.gigamap.types.IndexerLong;
import org.keen.solar.system.domain.CurrentPower;

public class CurrentPowerIndices {

    private CurrentPowerIndices() {}

    public static final IndexerLong<CurrentPower> epochTimestamp = new IndexerLong.Abstract<>() {
        @Override
        protected Long getLong(CurrentPower currentPower) {
            return currentPower.epochTimestamp();
        }

        @Override
        public String name() {
            return "epochTimestamp";
        }
    };
}
