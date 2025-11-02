package org.keen.solar.solcast.measurement.dal;

import org.eclipse.serializer.concurrency.LockedExecutor;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

public class MeasurementDaoEclipseStoreImpl implements MeasurementDao {

    private final EmbeddedStorageManager storageManager;
    private Long root;
    private final LockedExecutor executor;

    public MeasurementDaoEclipseStoreImpl(EmbeddedStorageManager storageManager) {
        this.storageManager = storageManager;
        this.executor = LockedExecutor.New();

        if (storageManager.root() == null) {
            root = 0L;
            storageManager.setRoot(root);
            storageManager.storeRoot();
        } else {
            root = (Long) storageManager.root();
        }
    }

    @Override
    public long getLastUploadedEpochTimestamp() {
        return executor.read(() -> root);
    }

    @Override
    public void setLastUploadedEpochTimestamp(long epochSeconds) {
        executor.write(() -> {
            root = epochSeconds;
            storageManager.setRoot(root);
            storageManager.storeRoot();
        });
    }
}
