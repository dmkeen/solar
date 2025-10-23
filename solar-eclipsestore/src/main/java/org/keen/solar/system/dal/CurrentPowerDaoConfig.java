package org.keen.solar.system.dal;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class CurrentPowerDaoConfig {

    @Bean
    public CurrentPowerDaoEclipseStoreImpl currentPowerDaoEclipseStoreImpl(Path rootStorage) {
        return new CurrentPowerDaoEclipseStoreImpl(storageManager(rootStorage));
    }

    private EmbeddedStorageManager storageManager(Path rootStorage) {
        return EmbeddedStorage.Foundation(rootStorage.resolve("current-power")).start();
    }
}
