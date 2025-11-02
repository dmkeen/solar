package org.keen.solar.string.dal;

import org.eclipse.serializer.concurrency.LockedExecutor;
import org.eclipse.store.gigamap.types.GigaMap;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.keen.solar.string.domain.StringPower;

import java.util.List;

import static org.keen.solar.string.dal.StringPowerIndices.periodEndEpoch;

public class StringPowerDaoEclipseStoreImpl implements StringPowerDao {

    private final EmbeddedStorageManager storageManager;
    private final GigaMap<StringPower> root;
    private final LockedExecutor executor;

    @SuppressWarnings("unchecked")
    public StringPowerDaoEclipseStoreImpl(EmbeddedStorageManager storageManager) {
        this.storageManager = storageManager;
        this.executor = LockedExecutor.New();

        if (storageManager.root() == null) {
            root = GigaMap.<StringPower>Builder()
                    .withBitmapIdentityIndex(periodEndEpoch)
                    .build();
            storageManager.setRoot(root);
            storageManager.storeRoot();
        } else {
            root = (GigaMap<StringPower>) storageManager.root();
        }
    }

    @Override
    public void save(StringPower stringPower) {
        executor.write(() -> {
            root.add(stringPower);
            storageManager.storeAll(stringPower, root);
        });
    }

    @Override
    public List<StringPower> getStringPowers(long fromEpochSeconds, long toEpochSeconds) {
        return executor.read(() -> root.query(periodEndEpoch.greaterThanEqual(fromEpochSeconds)
                .and(periodEndEpoch.lessThan(toEpochSeconds)))
                .toList());
    }
}
