package org.keen.solar.system.dal;

import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.keen.solar.system.domain.CurrentPower;

import java.nio.file.Path;
import java.util.List;

/**
 * Integration test for CurrentPowerDaoEclipseStoreImpl.<br/>
 * <br/>
 * EclipseStore requires: --add-exports java.base/jdk.internal.misc=ALL-UNNAMED
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CurrentPowerDaoEclipseStoreImplIT {

    private static EmbeddedStorageManager storageManager;

    @TempDir static Path tempDir;

    @BeforeAll
    static void setup() {
        storageManager = EmbeddedStorage.start(tempDir);
    }

    @Order(1)
    @Test
    void givenStorageManager_whenShutdownAndRestart_thenObjectsAreReloaded() {
        // Given
        CurrentPowerDaoEclipseStoreImpl store = new CurrentPowerDaoEclipseStoreImpl(storageManager);
        int epochTimestamp = 1752390721;
        store.save(new CurrentPower(epochTimestamp, 0, 450));

        // When
        storageManager.shutdown();
        storageManager = EmbeddedStorage.start(tempDir);

        // Then
        store = new CurrentPowerDaoEclipseStoreImpl(storageManager);
        List<CurrentPower> currentPowers = store.getStartingFrom(epochTimestamp);
        Assertions.assertEquals(1, currentPowers.size());
    }

    @Order(2)
    @Test
    void givenMultipleCurrentPowers_whenSave_thenCanGetStartingFrom() {
        // Given
        CurrentPowerDaoEclipseStoreImpl store = new CurrentPowerDaoEclipseStoreImpl(storageManager);

        int epochTimestamp = 1752390721;
        CurrentPower currentPower1 = new CurrentPower(epochTimestamp, 0, 450);
        CurrentPower currentPower2 = new CurrentPower(epochTimestamp + 1, 10, 450);
        CurrentPower currentPower3 = new CurrentPower(epochTimestamp + 2, 20, 450);
        CurrentPower currentPower4 = new CurrentPower(epochTimestamp + 3, 30, 450);
        CurrentPower currentPower5 = new CurrentPower(epochTimestamp + 4, 40, 450);

        // When
        store.save(currentPower1);
        store.save(currentPower2);
        store.save(currentPower3);
        store.save(currentPower4);
        store.save(currentPower5);

        // Then
        List<CurrentPower> currentPowerList = store.getStartingFrom(1752390721 + 1);
        Assertions.assertEquals(4, currentPowerList.size());
        Assertions.assertEquals(currentPower2, currentPowerList.get(0));
        Assertions.assertEquals(currentPower3, currentPowerList.get(1));
        Assertions.assertEquals(currentPower4, currentPowerList.get(2));
        Assertions.assertEquals(currentPower5, currentPowerList.get(3));
    }
}
