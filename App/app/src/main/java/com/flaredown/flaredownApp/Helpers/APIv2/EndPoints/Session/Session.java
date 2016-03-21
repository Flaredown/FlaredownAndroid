package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Session;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents the Session endpoint for the Flaredown API.
 */
public class Session {
    private String email = null;
    private String token = null;

    public Session(JSONObject jsonObject){
        email = jsonObject.optString("email");
        token = jsonObject.optString("token");
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
}
