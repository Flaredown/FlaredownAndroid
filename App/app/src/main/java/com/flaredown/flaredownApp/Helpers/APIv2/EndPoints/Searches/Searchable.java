package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Searches;

import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;

public class Searchable implements Serializable {
    private String id;
    private Calendar createdAt;
    private Calendar updatedAt;
    private String type;
    private String name;

    public Searchable(JSONObject inputJsonObject) throws JSONException {
        this.id = inputJsonObject.optString("id",null);
        this.createdAt = Date.stringToCalendar(inputJsonObject.optString("created_at", null));
        this.updatedAt = Date.stringToCalendar(inputJsonObject.optString("updated_at", null));
        this.type = inputJsonObject.optString("type",null);
        this.name = inputJsonObject.optString("name",null);
    }

    /**
     * Returns the JSON object representation of the Searchable
     * @return JSONObject of the searchable
     */
    public JSONObject toJson() throws JSONException {
        JSONObject output = new JSONObject();

        output.put("id", this.id);
        output.put("created_at", Date.calendarToString(this.createdAt));
        output.put("updated_at", Date.calendarToString(this.updatedAt));
        output.put("type", this.type);
        output.put("name", this.name);

        return output;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
