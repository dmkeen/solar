package org.keen.solar.solcast.measurement.dal;

import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

public class MeasurementDaoEclipseStoreImpl implements MeasurementDao {

    private final EmbeddedStorageManager storageManager;
    private Long root;

    public MeasurementDaoEclipseStoreImpl(EmbeddedStorageManager storageManager) {
        this.storageManager = storageManager;

        if (storageManager.root() == null) {
            root = 0L;
            storageManager.setRoot(root);
        } else {
            root = (Long) storageManager.root();
        }
    }

    @Override
    public long getLastUploadedEpochTimestamp() {
        return root;
    }

    @Override
    public void setLastUploadedEpochTimestamp(long epochSeconds) {
        root = epochSeconds;
        storageManager.setRoot(root);
    }
}
