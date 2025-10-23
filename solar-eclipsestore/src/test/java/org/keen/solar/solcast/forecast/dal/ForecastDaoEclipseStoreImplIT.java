package org.keen.solar.solcast.forecast.dal;

import org.eclipse.store.gigamap.types.GigaMap;
import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.keen.solar.solcast.forecast.domain.GenerationForecast;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Integration test for ForecastDaoEclipseStoreImpl.<br/>
 * <br/>
 * EclipseStore requires: --add-exports java.base/jdk.internal.misc=ALL-UNNAMED
 */
class ForecastDaoEclipseStoreImplIT {

    private static EmbeddedStorageManager storageManager;

    @BeforeAll
    static void setup(@TempDir Path tempDir) {
        storageManager = EmbeddedStorage.start(tempDir);
    }

    @Test
    void givenForecast_whenSave_thenCanBeRetrieved() {
        // Given
        ForecastDaoEclipseStoreImpl store = new ForecastDaoEclipseStoreImpl(storageManager);

        long epochTimestamp = 1752390721;
        GenerationForecast forecast = new GenerationForecast(1840, 1540, 2140,
                epochTimestamp, 3600);

        // When
        store.save(List.of(forecast));

        // Then
        @SuppressWarnings("unchecked")
        GigaMap<GenerationForecast> root = (GigaMap<GenerationForecast>) storageManager.root();
        Assertions.assertNotNull(root);
        Optional<GenerationForecast> optionalForecast = root.query(GenerationForecastIndices.periodEndEpoch.is(epochTimestamp)).findFirst();
        Assertions.assertTrue(optionalForecast.isPresent());
        Assertions.assertEquals(forecast, optionalForecast.get());
    }

    @Test
    void givenExistingForecast_whenSave_thenForecastIsUpdated() {
        // Given
        ForecastDaoEclipseStoreImpl store = new ForecastDaoEclipseStoreImpl(storageManager);

        long epochTimestamp = 1752390721;
        GenerationForecast forecast = new GenerationForecast(1840, 1540, 2140,
                epochTimestamp, 3600);
        store.save(List.of(forecast));

        forecast = new GenerationForecast(1840, 1640, 2040,
                epochTimestamp, 3600);

        // When
        store.save(List.of(forecast));

        // Then
        @SuppressWarnings("unchecked")
        GigaMap<GenerationForecast> root = (GigaMap<GenerationForecast>) storageManager.root();
        Assertions.assertNotNull(root);
        Optional<GenerationForecast> optionalForecast = root.query(GenerationForecastIndices.periodEndEpoch.is(epochTimestamp)).findFirst();
        Assertions.assertTrue(optionalForecast.isPresent());
        Assertions.assertEquals(forecast, optionalForecast.get());
    }
}
