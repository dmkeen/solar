package org.keen.solar.financial.dal;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class PowerCostDaoConfig {

    @Bean
    public PowerCostDaoEclipseStoreImpl powerCostDaoEclipseStoreImpl(Path rootStorage) {
        return new PowerCostDaoEclipseStoreImpl(storageManager(rootStorage));
    }

    private EmbeddedStorageManager storageManager(Path rootStorage) {
        return EmbeddedStorage.Foundation(rootStorage.resolve("power-cost")).start();
    }
}
