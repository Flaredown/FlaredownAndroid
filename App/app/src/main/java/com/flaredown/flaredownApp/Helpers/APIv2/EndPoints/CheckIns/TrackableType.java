package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

/**
 * The type of trackable.
 */
public enum TrackableType {
    CONDITION, SYMPTOM, TREATMENT;

    /**
     * Get the Json key for the trackable id (for example. condition_id, symptom_id, treatment_id).
     * @return The json key for the trackable id.
     */
    public String getTrackableIdKey() {
        return this.name().toLowerCase() + "_id";
    }
}
