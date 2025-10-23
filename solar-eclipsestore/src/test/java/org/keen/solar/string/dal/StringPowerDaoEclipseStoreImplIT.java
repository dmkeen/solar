package org.keen.solar.string.dal;

import org.eclipse.store.gigamap.types.GigaMap;
import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.keen.solar.string.domain.StringPower;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Integration test for StringPowerDaoEclipseStoreImpl.<br/>
 * <br/>
 * EclipseStore requires: --add-exports java.base/jdk.internal.misc=ALL-UNNAMED
 */
class StringPowerDaoEclipseStoreImplIT {

    private static EmbeddedStorageManager storageManager;

    @BeforeAll
    static void setup(@TempDir Path tempDir) {
        storageManager = EmbeddedStorage.start(tempDir);
    }

    @Test
    void givenStringPower_whenSave_thenCanBeRetrieved() {
        // Given
        StringPowerDaoEclipseStoreImpl store = new StringPowerDaoEclipseStoreImpl(storageManager);

        long epochTimestamp = 1752390721;
        StringPower stringPower1 = new StringPower(epochTimestamp, 300, BigDecimal.valueOf(200), BigDecimal.valueOf(10),
                BigDecimal.valueOf(300), BigDecimal.valueOf(15));

        // When
        store.save(stringPower1);

        // Then
        @SuppressWarnings("unchecked")
        GigaMap<StringPower> root = (GigaMap<StringPower>) storageManager.root();
        Assertions.assertNotNull(root);
        Optional<StringPower> optionalStringPower = root.query(StringPowerIndices.periodEndEpoch.is(epochTimestamp)).findFirst();
        Assertions.assertTrue(optionalStringPower.isPresent());
        Assertions.assertEquals(stringPower1, optionalStringPower.get());
    }
}
