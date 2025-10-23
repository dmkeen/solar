package org.keen.solar.string.dal;

import org.eclipse.store.gigamap.types.GigaMap;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.keen.solar.string.domain.StringPower;

import static org.keen.solar.string.dal.StringPowerIndices.periodEndEpoch;

public class StringPowerDaoEclipseStoreImpl implements StringPowerDao {

    private final EmbeddedStorageManager storageManager;
    private final GigaMap<StringPower> root;

    @SuppressWarnings("unchecked")
    public StringPowerDaoEclipseStoreImpl(EmbeddedStorageManager storageManager) {
        this.storageManager = storageManager;

        if (storageManager.root() == null) {
            root = GigaMap.<StringPower>Builder()
                    .withBitmapIdentityIndex(periodEndEpoch)
                    .build();
            storageManager.setRoot(root);
        } else {
            root = (GigaMap<StringPower>) storageManager.root();
        }
    }

    @Override
    public void save(StringPower stringPower) {
        root.add(stringPower);
        storageManager.storeAll(stringPower, root);
    }
}
