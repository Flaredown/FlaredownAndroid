package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    private int colourId;
    private String destroy;
    private transient MetaTrackable metaTrackable = null;

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
        this.id = (jsonObject.has("id")) ? jsonObject.optString("id") : null;
        this.createdAt = Date.stringToCalendar(jsonObject.optString("created_at", null));
        this.updatedAt = Date.stringToCalendar(jsonObject.optString("updated_at", null));
        this.checkInId = jsonObject.optString("checkin_id", null);
        this.value = (jsonObject.has("value") && !jsonObject.isNull("value"))? jsonObject.optString("value") : null;
        this.colourId = jsonObject.optInt("color_id", 0);
        this.trackableId = (jsonObject.has(type.getTrackableIdKey()))? jsonObject.optInt(type.getTrackableIdKey()) : null;
        this.destroy = (jsonObject.has("_destroy")) ? jsonObject.optString("_destroy") : null;
    }

    /**
     * Create a Trackable object from a JSON representing a trackable for the check in endpoint.
     * @param type The type of trackable (symptom, condition, treatment).
     * @param jsonObject Representing a trackable.
     * @param meta meta for the trackable
     */
    public Trackable(TrackableType type, JSONObject jsonObject, MetaTrackable meta) {
        this.type = type;
        this.id = (jsonObject.has("id")) ? jsonObject.optString("id") : null;
        this.createdAt = Date.stringToCalendar(jsonObject.optString("created_at", null));
        this.updatedAt = Date.stringToCalendar(jsonObject.optString("updated_at", null));
        this.checkInId = jsonObject.optString("checkin_id", null);
        this.value = (jsonObject.has("value") && !jsonObject.isNull("value"))? jsonObject.optString("value") : null;
        this.colourId = jsonObject.optInt("color_id", 0);
        this.trackableId = (jsonObject.has(type.getTrackableIdKey()))? jsonObject.optInt(type.getTrackableIdKey()) : null;
        this.destroy = (jsonObject.has("_destroy")) ? jsonObject.optString("_destroy") : null;
        this.metaTrackable = meta;
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
    public JSONObject getResponseJson(CheckIn checkIn) throws JSONException {
        JSONObject rootJObject = new JSONObject();
        rootJObject.put("_destroy", destroy);
        rootJObject.put("checkin_id", checkIn.getId());
        rootJObject.put("color_id", colourId);
        rootJObject.put(this.type.getTrackableIdKey(), trackableId);
        rootJObject.put("id", this.id);
        rootJObject.put("value", this.value);
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

    public int getColourId() {
        return colourId;
    }

    public void setColourId(int colourId) {
        this.colourId = colourId;
    }

    public MetaTrackable getMetaTrackable() {
        return metaTrackable;
    }

    public void setMetaTrackable(MetaTrackable metaTrackable) {
        this.metaTrackable = metaTrackable;
    }

    private final static String MT_ID = "mt_id";
    private final static String MT_NAME = "mt_name";
    private final static String MT_TYPE = "mt_type";
    private final static String MT_CREATED_AT = "mt_createdAt";
    private final static String MT_UPDATED_AT = "mt_updatedAt";
    private final static String MT_CACHED_AT = "mt_cachedAt";

    // Overriding java's serialization, this is because the realm database does not allow serialisation
    // and a MetaTrackable is a serializable object
    private void writeObject(ObjectOutputStream oos) throws IOException {
        // Default serialzation.
        oos.defaultWriteObject();

        Map<String, Object> data = new HashMap<>();
        data.put(MT_ID, metaTrackable.getId());
        data.put(MT_NAME, metaTrackable.getName());
        data.put(MT_TYPE, metaTrackable.getTypeRaw());
        data.put(MT_CREATED_AT, metaTrackable.getCreatedAtRaw());
        data.put(MT_UPDATED_AT, metaTrackable.getUpdatedAtRaw());
        data.put(MT_CACHED_AT, metaTrackable.getCachedAtRaw());
        oos.writeObject(data);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        // Default deserializarion.
        ois.defaultReadObject();

        Map<String, Object> data = (HashMap<String, Object>) ois.readObject();
        if(data.containsKey(MT_ID)) {
            if(metaTrackable == null) metaTrackable = new MetaTrackable();
            metaTrackable.setId((Integer) data.get(MT_ID));
            metaTrackable.setName((String) data.get(MT_NAME));
            metaTrackable.setTypeRaw((String) data.get(MT_TYPE));
            metaTrackable.setCreatedAtRaw((Long) data.get(MT_CREATED_AT));
            metaTrackable.setUpdatedAtRaw((Long) data.get(MT_UPDATED_AT));
            metaTrackable.setCachedAtRaw((Long) data.get(MT_CACHED_AT));
        }
    }
}
