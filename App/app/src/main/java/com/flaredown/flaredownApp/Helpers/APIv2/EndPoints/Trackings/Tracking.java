package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An element of the JSON returned from the trackings endpoint.
 */
public class Tracking {
    private String id;
    private String created_at;
    private String updated_at;
    private int user_id;
    private String trackable_id;
    private String trackable_type;
    private String start_at;
    private String end_at;

    public Tracking(JSONObject jsonObject) throws JSONException{
        this.id = String.valueOf(jsonObject.getInt("id"));
        this.created_at = jsonObject.getString("created_at");
        this.updated_at = jsonObject.getString("updated_at");
        this.user_id = jsonObject.getInt("user_id");
        this.trackable_id = String.valueOf(jsonObject.getInt("trackable_id"));
        this.trackable_type = jsonObject.getString("trackable_type");
        this.start_at = jsonObject.getString("start_at");
        this.end_at = jsonObject.getString("end_at");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTrackable_id() {
        return trackable_id;
    }

    public void setTrackable_id(String trackable_id) {
        this.trackable_id = trackable_id;
    }

    public String getTrackable_type() {
        return trackable_type;
    }

    public void setTrackable_type(String trackable_type) {
        this.trackable_type = trackable_type;
    }

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }

    public String getEnd_at() {
        return end_at;
    }

    public void setEnd_at(String end_at) {
        this.end_at = end_at;
    }

    /**
     * Returns the JSON object representation of the Check In
     * @return JSON representation for Tracking object
     */
    public JSONObject toJson() throws JSONException {
        JSONObject output = new JSONObject();

        output.put("id", this.id);
        output.put("created_at", this.created_at);
        output.put("updated_at", this.updated_at);
        output.put("user_id", this.user_id);
        output.put("trackable_id", this.trackable_id);
        output.put("trackable_type", this.trackable_type);
        output.put("start_at", this.start_at);
        output.put("end_at", this.end_at);

        return output;
    }
}
