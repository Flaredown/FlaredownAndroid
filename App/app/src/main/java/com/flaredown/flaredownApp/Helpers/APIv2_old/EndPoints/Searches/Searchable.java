package com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.Searches;

import android.support.annotation.Nullable;

import com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.Helpers.APIv2_old.Helper.Date;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;

public class Searchable implements Serializable {
    private int id;
    private Calendar createdAt;
    private Calendar updatedAt;
    private TrackableType type;
    private int color_id;
    private int users_count;
    private String name;

    public Searchable(JSONObject inputJsonObject) throws JSONException {
        this.id = inputJsonObject.optInt("id",0);
        this.createdAt = Date.stringToCalendar(inputJsonObject.optString("created_at", null));
        this.updatedAt = Date.stringToCalendar(inputJsonObject.optString("updated_at", null));
        this.color_id = inputJsonObject.optInt("color_id",0);
        this.users_count = inputJsonObject.optInt("users_count",0);
        this.type = determineType(inputJsonObject.optString("type",null));
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
        output.put("color_id", this.color_id);
        output.put("users_count",this.users_count);
        output.put("type", this.type.toString());
        output.put("name", this.name);

        return output;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public TrackableType getType() {
        return type;
    }

    public void setType(TrackableType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor_id() {
        return color_id;
    }

    public void setColor_id(int color_id) {
        this.color_id = color_id;
    }

    public int getUsers_count() {
        return users_count;
    }

    public void setUsers_count(int users_count) {
        this.users_count = users_count;
    }

    @Nullable
    private TrackableType determineType(String type){
        switch (type.toLowerCase()){
            case "condition":
                return TrackableType.CONDITION;
            case "symptom":
                return TrackableType.SYMPTOM;
            case "treatment":
                return TrackableType.TREATMENT;
            default:
                return null;
        }
    }

}
