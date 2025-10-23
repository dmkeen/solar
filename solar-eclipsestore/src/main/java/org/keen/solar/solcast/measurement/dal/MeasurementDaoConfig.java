package org.keen.solar.solcast.measurement.dal;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class MeasurementDaoConfig {

    @Bean
    public MeasurementDaoEclipseStoreImpl measurementDaoEclipseStoreImpl(Path rootStorage) {
        return new MeasurementDaoEclipseStoreImpl(storageManager(rootStorage));
    }

    private EmbeddedStorageManager storageManager(Path rootStorage) {
        return EmbeddedStorage.Foundation(rootStorage.resolve("measurement")).start();
    }
}
