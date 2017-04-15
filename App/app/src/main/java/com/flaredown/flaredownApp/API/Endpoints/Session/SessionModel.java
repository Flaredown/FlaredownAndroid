package com.flaredown.flaredownApp.API.Endpoints.Session;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.flaredown.flaredownApp.API.Sync.ServerModel;
import com.flaredown.flaredownApp.Helpers.GsonHelper;
import com.flaredown.flaredownApp.Helpers.PreferenceKeys;
import com.google.gson.JsonParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.util.Calendar;

/**
 * Created by thunter on 15/03/2017.
 */

public class SessionModel extends ServerModel {

    /**
     * Cached variable of the object stored in shared preferences using the key {@link PreferenceKeys#USER_SESSION_JSON}
     */
    private static SessionModel storedUserObject;


    public String email;
    public String token;
    public String userId;
    public String id;
    public Calendar createdAt;
    public Calendar updatedAt;


    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getId() {
        return id;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public Calendar getUpdatedAt() {
        return updatedAt;
    }


    /**
     * Save this object as a json into the shared preferences, this will be used as the logged in object.
     * Using {@link SharedPreferences} key {@link PreferenceKeys#USER_SESSION_JSON}
     * @param context
     */
    public void saveToSharedPreferences(Context context) {
        SharedPreferences.Editor editor = PreferenceKeys.getSharedPreferences(context).edit();

        String json = GsonHelper.toJson(this);

        editor.putString(PreferenceKeys.USER_SESSION_JSON, json);
        // Update the static variable caching the result, reducing JSON parsing.
        storedUserObject = this;

        editor.apply();
    }

    /**
     * Get the current 'logged in' Session object from SharedPreferences.
     * @param context
     * @param ignoreCachedVariable If true method will directly try SharedPreferences instead of using a cached field.
     * @return The current 'logged in' Session object, null if there isn't one.
     */
    @Nullable
    public static SessionModel getFromSharedPreferences(Context context, boolean ignoreCachedVariable) {
        if(storedUserObject != null && storedUserObject.isRequiredFieldsSet() && !ignoreCachedVariable) {
            // Already loaded no point loading twice.
            return storedUserObject;
        }


        // Otherwise get the object from shared preferences (if exists) and then parse.

        SharedPreferences sp = PreferenceKeys.getSharedPreferences(context);
        String json = sp.contains(PreferenceKeys.USER_SESSION_JSON) ? sp.getString(PreferenceKeys.USER_SESSION_JSON, null) : null;

        if(json == null || "".equals(json)) {
            // Json empty, could not find logged in user return null..
            return null;
        }

        // Attempt to parse json.
        SessionModel parsedObject;
        try {
            parsedObject = GsonHelper.getFromJson(json, SessionModel.class);
        } catch (JsonParseException e) {
            // Failed to parse, return null.
            return null;
        }

        // Validate the object, ensure essential fields are set.
        if(parsedObject.isRequiredFieldsSet()) {
            return storedUserObject = parsedObject;
        }
        return null;
    }

    /**
     * Check to see if all the required fields are set to make the object valid.
     * @return True if all the required fields are set.
     */
    private boolean isRequiredFieldsSet() {
        return StringUtils.isNoneBlank(getId(), getToken(), getEmail());
    }
}
