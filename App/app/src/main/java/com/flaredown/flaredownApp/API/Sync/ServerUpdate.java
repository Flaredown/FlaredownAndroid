package com.flaredown.flaredownApp.API.Sync;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Predicate;
import com.flaredown.flaredownApp.Helpers.Volley.WebAttributes;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Used for updating the API, while also updating the ServerModel. Requests that fail due to
 * connectivity issues (no wifi etc) will be sent at a later point when connectivity is resumed.
 *
 * Updates may be stored in realm, if the request fails and is need to be sent again.
 */

public abstract class ServerUpdate<M extends ServerModel> {
    private String id = null;
    private long updateStart;

    public ServerUpdate() {
        updateStart = System.currentTimeMillis(); // Record the time the request was started.
        id = createPrimaryKey();
    }

    /**
     * Used for converting the json response sent from the server to a Object.
     * @param json The json string to convert to an object.
     * @return The server model object which is generated from the json.
     */
    public abstract M generateServerModel(String json);

    /**
     * Get the endpoint string for this update object.
     * @return String The endpoint string for this update object.
     */
    public abstract String getEndpoint();

    /**
     * Get the request method for this update object.
     * @return The request method for this update object
     */
    public abstract RequestMethod getRequestMethod();



    /**
     * Creates a primary key for the realm object.
     * @return A unique identifier for the realm object.
     */
    public String createPrimaryKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * A protected convenience method to allow subclasses to set the behaviour of the update object.
     * When this method returns true (which is the default behaviour) any updates that fail due to
     * connectivity issues will be stored and attempted later on, when connectivity is restored.
     * @return true if the model is stored, default true.
     */
    protected boolean allowStorage() {
        return true;
    }

    /**
     * Returns a WebAttributes object containing all the web attributes for this server update.
     * @return WebAttributes object containing all the web attributes.
     */
    public WebAttributes getWebAttributes() {
        final WebAttributes webAttributes = new WebAttributes();
        Stream.of(this.getClass().getDeclaredFields()).forEach(new Consumer<Field>() {
            @Override
            public void accept(Field field) {
                if(field.isAnnotationPresent(WebParameter.class)) {
                    try {
                        String key = field.getAnnotation(WebParameter.class).keyValue();

                        // Default behaviour is to use the field name.
                        if(key == null || "".equals(key)) {
                            key = field.getName();
                        }

                        String value = String.valueOf(field.get(ServerUpdate.this));

                        webAttributes.put(key, value);

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return webAttributes;
    }
}
