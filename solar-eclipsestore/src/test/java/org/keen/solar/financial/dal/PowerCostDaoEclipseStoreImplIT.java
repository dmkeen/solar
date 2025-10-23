package org.keen.solar.financial.dal;

import org.eclipse.store.gigamap.types.GigaMap;
import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.keen.solar.financial.domain.PowerCost;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Integration test for PowerCostDaoEclipseStoreImpl.<br/>
 * <br/>
 * EclipseStore requires: --add-exports java.base/jdk.internal.misc=ALL-UNNAMED
 */
class PowerCostDaoEclipseStoreImplIT {

    private static EmbeddedStorageManager storageManager;

    @BeforeAll
    static void setup(@TempDir Path tempDir) {
        storageManager = EmbeddedStorage.start(tempDir);
    }

    @Test
    void givenPowerCost_whenSave_thenCanBeRetrieved() {
        // Given
        PowerCostDaoEclipseStoreImpl store = new PowerCostDaoEclipseStoreImpl(storageManager);

        long epochTimestamp = 1752390721;
        PowerCost powerCost = new PowerCost(BigDecimal.valueOf(1, 5), epochTimestamp, 60);

        // When
        store.save(powerCost);

        // Then
        @SuppressWarnings("unchecked")
        GigaMap<PowerCost> root = (GigaMap<PowerCost>) storageManager.root();
        Assertions.assertNotNull(root);
        Optional<PowerCost> optionalPowerCost = root.query(PowerCostIndices.periodEndEpoch.is(epochTimestamp)).findFirst();
        Assertions.assertTrue(optionalPowerCost.isPresent());
        Assertions.assertEquals(powerCost, optionalPowerCost.get());
    }
}
