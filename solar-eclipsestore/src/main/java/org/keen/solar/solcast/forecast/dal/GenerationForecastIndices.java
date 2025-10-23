package org.keen.solar.solcast.forecast.dal;

import org.eclipse.store.gigamap.types.IndexerLong;
import org.keen.solar.solcast.forecast.domain.GenerationForecast;

public class GenerationForecastIndices {

    private GenerationForecastIndices() {}

    public static final IndexerLong<GenerationForecast> periodEndEpoch = new IndexerLong.Abstract<>() {
        @Override
        protected Long getLong(GenerationForecast forecast) {
            return forecast.period_end_epoch();
        }


        @Override
        public String name() {
            return "forecastPeriodEndEpoch";
        }
    };
}
