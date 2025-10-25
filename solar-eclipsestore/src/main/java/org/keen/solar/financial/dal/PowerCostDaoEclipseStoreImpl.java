package org.keen.solar.financial.dal;

import org.eclipse.store.gigamap.types.GigaMap;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.keen.solar.financial.domain.PowerCost;

import java.util.List;

import static org.keen.solar.financial.dal.PowerCostIndices.periodEndEpoch;

public class PowerCostDaoEclipseStoreImpl implements PowerCostDao {

    private final EmbeddedStorageManager storageManager;
    private final GigaMap<PowerCost> root;

    @SuppressWarnings("unchecked")
    public PowerCostDaoEclipseStoreImpl(EmbeddedStorageManager storageManager) {
        this.storageManager = storageManager;

        if (storageManager.root() == null) {
            root = GigaMap.<PowerCost>Builder()
                    .withBitmapIdentityIndex(periodEndEpoch)
                    .build();
            storageManager.setRoot(root);
        } else {
            root = (GigaMap<PowerCost>) storageManager.root();
        }
    }

    @Override
    public void save(PowerCost powerCost) {
        root.add(powerCost);
        storageManager.storeAll(powerCost, root);
    }

    @Override
    public List<PowerCost> getPowerCosts(long fromEpochTime, long toEpochTime) {
        return root.query(periodEndEpoch.greaterThanEqual(fromEpochTime)
                .and(periodEndEpoch.lessThan(toEpochTime)))
                .toList();
    }
}
