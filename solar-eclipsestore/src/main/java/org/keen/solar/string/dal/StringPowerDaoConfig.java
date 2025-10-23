package org.keen.solar.string.dal;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class StringPowerDaoConfig {

    @Bean
    public StringPowerDaoEclipseStoreImpl stringPowerDaoEclipseStoreImpl(Path rootStorage) {
        return new StringPowerDaoEclipseStoreImpl(storageManager(rootStorage));
    }

    private EmbeddedStorageManager storageManager(Path rootStorage) {
        return EmbeddedStorage.Foundation(rootStorage.resolve("string-power")).start();
    }
}
