package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Used as a base to represent the trackables (Symptoms, Conditions & Treatments).
 */
public class Trackable implements Serializable {
    private TrackableType type;
    private String id;
    private Calendar createdAt;
    private Calendar updatedAt;
    private String checkInId;
    private String value;
    private Integer trackableId;
    private String colourId;
    private MetaTrackable metaTrackable = null;
    private String destroy;

    /**

     * Default constructor for the trackable object.
     * @param type The type of trackable (condition, symptom, treatment).
     */
    public Trackable(TrackableType type) {
        this.type = type;
    }

    /**
     * Create a Trackable object from a JSON representing a trackable for the check in endpoint.
     * @param type The type of trackable (symptom, condition, treatment).
     * @param jsonObject Representing a trackable.
     */
    public Trackable(TrackableType type, JSONObject jsonObject) {
        this.type = type;
        this.id = jsonObject.optString("id", null);
        this.createdAt = Date.stringToCalendar(jsonObject.optString("created_at", null));
        this.updatedAt = Date.stringToCalendar(jsonObject.optString("updated_at", null));
        this.checkInId = jsonObject.optString("checkin_id", null);
        this.value = (jsonObject.has("value") && !jsonObject.isNull("value"))? jsonObject.optString("value") : null;
        this.colourId = jsonObject.optString("color_id", null);
        this.trackableId = (jsonObject.has(type.getTrackableIdKey()))? jsonObject.optInt(type.getTrackableIdKey()) : null;
        this.destroy = (jsonObject.has("_destroy")) ? jsonObject.optString("_destroy") : null;
    }

    public JSONObject toJson() throws JSONException{
        JSONObject output = new JSONObject();

        output.put("id", this.id);
        output.put("created_at", this.createdAt);
        output.put("updated_at", this.updatedAt);
        output.put("checkin_id", this.checkInId);
        output.put("value", this.value);
        output.put("color_id", this.colourId);
        output.put(this.type.getTrackableIdKey(), this.trackableId);

        return output;
    }

    /**
     * Get the response json for a single trackable.
     * @return The response json for a single trackable.
     * @throws JSONException
     */
    public JSONObject getResponseJson() throws JSONException {
        JSONObject rootJObject = new JSONObject();
        rootJObject.put("_destroy", destroy);
        rootJObject.put("checkin_id", checkInId);
        rootJObject.put("color_id", colourId);
        rootJObject.put(this.type.getTrackableIdKey(), trackableId);
        rootJObject.put("id", id);
        rootJObject.put("value", value);
        return rootJObject;
    }

    // ======== Getters and Setters ========

    public TrackableType getType() {
        return type;
    }

    public void setType(TrackableType type) {
        this.type = type;
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

    public String getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(String checkInId) {
        this.checkInId = checkInId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDestroy() {
        return destroy;
    }

    public void setDestroy(String destroy) {
        this.destroy = destroy;
    }

    public Integer getTrackableId() {
        return trackableId;
    }

    public void setTrackableId(Integer trackableId) {
        this.trackableId = trackableId;
    }

    public String getColourId() {
        return colourId;
    }

    public void setColourId(String colourId) {
        this.colourId = colourId;
    }

    public MetaTrackable getMetaTrackable() {
        return metaTrackable;
    }

    public void setMetaTrackable(MetaTrackable metaTrackable) {
        this.metaTrackable = metaTrackable;
    }
}
