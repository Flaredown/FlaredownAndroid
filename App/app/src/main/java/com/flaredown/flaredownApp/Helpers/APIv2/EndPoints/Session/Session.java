package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Session;

import org.json.JSONObject;

/**
 * Represents the Session endpoint for the Flaredown API.
 */
public class Session {
    private String email = null;
    private String token = null;
    private String userId = null;
    private String id = null;

    public Session(JSONObject jsonObject){
        email = jsonObject.optString("email");
        token = jsonObject.optString("token");
        id = jsonObject.optString("id");
        userId = jsonObject.optString("user_id");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
