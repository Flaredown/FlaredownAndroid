package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Used as a base to represent a treatment trackable
 */
public class TreatmentTrackable extends Trackable implements Serializable{

    private Boolean isTaken = false;

    /**
     * Default constructor for the treatment trackable object.
     */
    public TreatmentTrackable() {
        super(TrackableType.TREATMENT);
    }

    /**
     * Create a treatment object from a JSON representing a treatment object.
     * @param jsonObject Representing a treatment object.
     */
    public TreatmentTrackable(JSONObject jsonObject) {
        super(TrackableType.TREATMENT, jsonObject);
        this.isTaken = jsonObject.optBoolean("is_taken", false);
    }

    public Boolean getIsTaken() {
        return isTaken;
    }

    public void setIsTaken(Boolean taken) {
        isTaken = taken;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject rootJObject = super.toJson();
        rootJObject.put("is_taken", isTaken);
        return rootJObject;
    }

    /**
     * Get the response json for a single trackable.
     * @return The response json for a single trackable.
     * @throws JSONException
     */
    @Override
    public JSONObject getResponseJson() throws JSONException {
        JSONObject rootJObject = super.getResponseJson();
        rootJObject.put("is_taken", isTaken);
        return rootJObject;
    }
}
