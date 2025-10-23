package org.keen.solar.financial.dal;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class TariffDaoConfig {

    @Bean
    public TariffDaoEclipseStoreImpl tariffDaoEclipseStoreImpl(Path rootStorage) {
        return new TariffDaoEclipseStoreImpl(storageManager(rootStorage));
    }

    private EmbeddedStorageManager storageManager(Path rootStorage) {
        return EmbeddedStorage.Foundation(rootStorage.resolve("tariff")).start();
    }
}
