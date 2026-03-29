package org.keen.solar.financial.dal;

import org.eclipse.serializer.persistence.types.PersistenceFieldEvaluator;
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
        // Use a PersistenceFieldEvaluator that stores all fields of an object,
        // so that EnumMap can be stored and reloaded successfully.
        // EnumMap contains a number of transient fields that EclipseStore
        // would not otherwise store, but this leads to a NPE after an
        // application restart.
        PersistenceFieldEvaluator fieldEvaluator = (_, _) -> true;
        return EmbeddedStorage.Foundation(rootStorage.resolve("tariff"))
                .onConnectionFoundation(c -> c.setFieldEvaluatorPersistable(fieldEvaluator))
                .createEmbeddedStorageManager()
                .start();
    }
}
