package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Provides extra information about a trackable.
 */
public class MetaTrackable implements Serializable {
    private int colorId;
    private int id;
    private String name;
    private TrackableType type;
    private Calendar createdAt;
    private Calendar updatedAt;


    public MetaTrackable(JSONObject jObject) throws JSONException{
        this.colorId = jObject.optInt("color_id", 1);
        this.id = jObject.getInt("id");
        this.name = jObject.getString("name");
        this.type = TrackableType.valueOfs(jObject.getString("type"));
        this.createdAt = Date.stringToCalendar(jObject.optString("created_at", null));
        this.updatedAt = Date.stringToCalendar(jObject.optString("updated_at", null));
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TrackableType getType() {
        return type;
    }

    public void setType(TrackableType type) {
        this.type = type;
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
