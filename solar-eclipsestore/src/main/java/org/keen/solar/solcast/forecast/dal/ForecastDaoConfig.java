package org.keen.solar.solcast.forecast.dal;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class ForecastDaoConfig {

    @Bean
    public ForecastDaoEclipseStoreImpl forecastDaoEclipseStoreImpl(Path rootStorage) {
        return new ForecastDaoEclipseStoreImpl(storageManager(rootStorage));
    }

    private EmbeddedStorageManager storageManager(Path rootStorage) {
        return EmbeddedStorage.Foundation(rootStorage.resolve("forecast")).start();
    }
}
