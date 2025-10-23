package org.keen.solar.financial.dal;

import org.eclipse.store.gigamap.types.IndexerLong;
import org.keen.solar.financial.domain.PowerCost;

public class PowerCostIndices {

    private PowerCostIndices() {}

    public static final IndexerLong<PowerCost> periodEndEpoch = new IndexerLong.Abstract<>() {
        @Override
        protected Long getLong(PowerCost powerCost) {
            return powerCost.periodEndEpoch();
        }

        @Override
        public String name() {
            return "powerCost";
        }
    };
}
