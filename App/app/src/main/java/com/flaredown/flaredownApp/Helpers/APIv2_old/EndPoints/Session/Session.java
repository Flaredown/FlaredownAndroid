package com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.Session;

import com.flaredown.flaredownApp.Helpers.APIv2_old.Helper.Date;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Represents the Session endpoint for the Flaredown API.
 */
public class Session {
    private String email = null;
    private String token = null;
    private String userId = null;
    private String id = null;
    private Calendar createdAt = null;
    private Calendar updatedAt = null;

    public Session(JSONObject jsonObject){
        email = jsonObject.optString("email");
        token = jsonObject.optString("token");
        id = jsonObject.optString("id");
        userId = jsonObject.optString("user_id");
        createdAt = Date.stringToCalendar(jsonObject.optString("created_at"), Date.API_DATE_TIME_FORMAT);
        updatedAt = Date.stringToCalendar(jsonObject.optString("updated_at"), Date.API_DATE_TIME_FORMAT);
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

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public Calendar getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Calendar updatedAt) {
        this.updatedAt = updatedAt;
    }
}
