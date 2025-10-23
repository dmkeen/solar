package org.keen.solar.solcast.forecast.dal;

import org.eclipse.store.gigamap.types.GigaMap;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.keen.solar.solcast.forecast.domain.GenerationForecast;

import java.util.Collection;
import java.util.Optional;

import static org.keen.solar.solcast.forecast.dal.GenerationForecastIndices.periodEndEpoch;

public class ForecastDaoEclipseStoreImpl implements ForecastDao {

    private final EmbeddedStorageManager storageManager;
    private final GigaMap<GenerationForecast> root;

    @SuppressWarnings("unchecked")
    public ForecastDaoEclipseStoreImpl(EmbeddedStorageManager storageManager) {
        this.storageManager = storageManager;

        if (storageManager.root() == null) {
            root = GigaMap.<GenerationForecast>Builder()
                    .withBitmapIdentityIndex(periodEndEpoch)
                    .build();
            storageManager.setRoot(root);
        } else {
            root = (GigaMap<GenerationForecast>) storageManager.root();
        }
    }

    @Override
    public void save(Collection<GenerationForecast> forecasts) {
        forecasts.forEach(forecast -> {
            Optional<GenerationForecast> existingForecast = root.query(periodEndEpoch.is(forecast.period_end_epoch())).findFirst();
            if (existingForecast.isPresent()) {
                root.replace(existingForecast.get(), forecast);
            } else {
                root.add(forecast);
            }
        });
        forecasts.forEach(storageManager::store);
        storageManager.store(root);
    }
}
