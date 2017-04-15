package com.flaredown.flaredownApp.API.Sync;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * All data received from the API is placed into a class that extends the ServerModel.
 *
 * The model may be also stored (cached) in realm. Allowing for offline data access.
 * On data retrieval the cache will be displayed first. If there is any difference between
 * the model and cache the model will notify the view via observer to update.
 *
 * Pending sent data is also stored in the model.
 */

public abstract class ServerModel {
//    @PrimaryKey
    private String realmId;

    private long realmCreationTime;


    public ServerModel() {
        realmCreationTime = System.currentTimeMillis();
        realmId = createPrimaryKey();
    }

    /**
     * Creates a primary key for the realm object.
     * @return A unique identifier for the realm object.
     */
    public String createPrimaryKey() {
        return UUID.randomUUID().toString();
    }
}
