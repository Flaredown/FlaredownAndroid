package com.flaredown.flaredownApp.API.Sync;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Used for updating the API, while also updating the ServerModel. Requests that fail due to
 * connectivity issues (no wifi etc) will be sent at a later point when connectivity is resumed.
 *
 * Updates may be stored in realm, if the request fails and is need to be sent again.
 */

public abstract class ServerUpdate extends RealmObject {
    @PrimaryKey
    private String id = null;
    private long updateStart;

    public ServerUpdate() {
        updateStart = System.currentTimeMillis(); // Record the time the request was started.
        id = createPrimaryKey();
    }



    /**
     * Creates a primary key for the realm object.
     * @return A unique identifier for the realm object.
     */
    public String createPrimaryKey() {
        return UUID.randomUUID().toString();
    }
}
