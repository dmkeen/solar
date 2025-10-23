package org.keen.solar.solcast.measurement.dal;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

/**
 * Integration test for MeasurementDaoEclipseStoreImpl.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MeasurementDaoEclipseStoreImplIT {

    private static EmbeddedStorageManager storageManager;

    @BeforeAll
    static void setup(@TempDir Path tempDir) {
        storageManager = EmbeddedStorage.start(tempDir);
    }

    @Test
    @Order(1)
    void givenUninitialisedStore_whenGetLastUploadedEpochTimestamp_thenZeroReturned() {
        // Given
        MeasurementDaoEclipseStoreImpl store = new MeasurementDaoEclipseStoreImpl(storageManager);

        // When
        long lastUploadedEpochTimestamp = store.getLastUploadedEpochTimestamp();

        // Then
        Assertions.assertEquals(0, lastUploadedEpochTimestamp);
    }

    @Test
    void givenTimestamp_whenSetLastUploadedEpochTimestamp_thenValueIsStored() {
        // Given
        MeasurementDaoEclipseStoreImpl store = new MeasurementDaoEclipseStoreImpl(storageManager);
        long epochTimestamp = 1752390721;

        // When
        store.setLastUploadedEpochTimestamp(epochTimestamp);

        // Then
        Long root = (Long) storageManager.root();
        Assertions.assertNotNull(root);
        Assertions.assertEquals(epochTimestamp, root);
    }

    @Test
    void givenPreviouslySetTimestamp_whenGetLastUploadedEpochTimestamp_thenValueIsRetrieved() {
        // Given
        MeasurementDaoEclipseStoreImpl store = new MeasurementDaoEclipseStoreImpl(storageManager);
        long epochTimestamp = 1752390721;
        store.setLastUploadedEpochTimestamp(epochTimestamp);

        // When
        long lastUploadedEpochTimestamp = store.getLastUploadedEpochTimestamp();

        // Then
        Assertions.assertEquals(epochTimestamp, lastUploadedEpochTimestamp);
    }
}
