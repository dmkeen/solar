package org.keen.solar.system.dal;

import org.eclipse.store.gigamap.types.GigaMap;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.keen.solar.system.domain.CurrentPower;

import java.util.List;

import static org.keen.solar.system.dal.CurrentPowerIndices.epochTimestamp;

public class CurrentPowerDaoEclipseStoreImpl implements CurrentPowerDao {

    private final EmbeddedStorageManager storageManager;
    private final GigaMap<CurrentPower> root;

    @SuppressWarnings("unchecked")
    public CurrentPowerDaoEclipseStoreImpl(EmbeddedStorageManager storageManager) {
        this.storageManager = storageManager;

        if (storageManager.root() == null) {
            root = GigaMap.<CurrentPower>Builder()
                    .withBitmapIdentityIndex(epochTimestamp)
                    .build();
            storageManager.setRoot(root);
        } else {
            root = (GigaMap<CurrentPower>) storageManager.root();
        }
    }

    @Override
    public List<CurrentPower> getStartingFrom(long fromEpochSeconds) {
        return root.query(epochTimestamp.greaterThanEqual(fromEpochSeconds)).toList();
    }

    @Override
    public void save(CurrentPower currentPower) {
        root.add(currentPower);
        storageManager.storeAll(currentPower, root);
    }

}
