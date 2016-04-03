package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * An element of the JSON returned from the check in endpoint.
 */
public class CheckIn implements Serializable{
    private String id;
    private Calendar createdAt;
    private Calendar updatedAt;
    private Calendar date;
    private String note;
    private ArrayList<Trackable> conditions = new ArrayList<>();
    private ArrayList<Trackable> symptoms = new ArrayList<>();
    private ArrayList<Trackable> treatments = new ArrayList<>();

    public CheckIn(String id, Calendar date) {
        this.id = id;
        this.date = date;
    }

    public CheckIn(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.optString("id", null);
        this.createdAt = Date.stringToCalendar(jsonObject.optString("created_at", null));
        this.createdAt = Date.stringToCalendar(jsonObject.optString("updated_at", null));
        this.date = Date.stringToCalendar(jsonObject.optString("date", null));
        this.note = jsonObject.optString("note", null);
        this.conditions = createTrackableList(TrackableType.CONDITION, jsonObject.getJSONArray("conditions"));
        this.symptoms = createTrackableList(TrackableType.SYMPTOM, jsonObject.getJSONArray("symptoms"));
        this.treatments = createTrackableList(TrackableType.TREATMENT, jsonObject.getJSONArray("treatments"));
    }

    // TODO support tags.

    /**
     * Returns the JSON object representation of the Check In
     * @return
     */
    public JSONObject toJson() throws JSONException {
        JSONObject output = new JSONObject();

        output.put("id", this.id);
        output.put("created_at", Date.calendarToString(this.createdAt));
        output.put("updated_at", Date.calendarToString(this.updatedAt));
        output.put("date", Date.calendarToString(this.date));
        output.put("note", this.note);
        output.put("conditions", createTrackableJArray(this.conditions));
        output.put("symptoms", createTrackableJArray(this.symptoms));
        output.put("treatments", createTrackableJArray(this.treatments));

        return output;
    }

    private ArrayList<Trackable> createTrackableList(TrackableType type, JSONArray trackableJArray) throws JSONException {
        ArrayList<Trackable> output = new ArrayList<>();
        for (int i = 0; i < trackableJArray.length(); i++) {
            output.add(new Trackable(type, trackableJArray.getJSONObject(i)));
        }
        return output;
    }

    private JSONArray createTrackableJArray(ArrayList<Trackable> trackableList) throws JSONException {
        JSONArray output = new JSONArray();
        for (Trackable trackable : trackableList) {
            output.put(trackable.toJson());
        }
        return output;
    }

    /**
     * Has the user previously submitted a response for this check in.
     * @return True if the user has previously submitted a response for this check in.
     */
    public boolean hasResponse() {
        for (Trackable condition : conditions) {
            if(condition.getValue() != null)
                return true;
        }
        for (Trackable symptom : symptoms) {
            if(symptom.getValue() != null)
                return true;
        }
        for (Trackable treatment : treatments) {
            if(treatment.getValue() != null)
                return true;
        }
        return false;
    }

    //============ Getter's and Setters ===========

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

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<Trackable> getConditions() {
        return conditions;
    }

    public void setConditions(ArrayList<Trackable> conditions) {
        this.conditions = conditions;
    }

    public ArrayList<Trackable> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(ArrayList<Trackable> symptoms) {
        this.symptoms = symptoms;
    }

    public ArrayList<Trackable> getTreatments() {
        return treatments;
    }

    public void setTreatments(ArrayList<Trackable> treatments) {
        this.treatments = treatments;
    }

    /**
     * Returns an ArrayList of trackables for the specific trackable type.
     * @param trackableType The trackable type for the array returned.
     * @return ArrayLost of trackables for the specific trackable type.
     */
    public ArrayList<Trackable> getTrackables(TrackableType trackableType) {
       switch (trackableType) {
           case CONDITION:
               return getConditions();
           case SYMPTOM:
               return getSymptoms();
           case TREATMENT:
               return getTreatments();
       }
        return null;
    }
}
