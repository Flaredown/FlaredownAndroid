package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;


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
    private ArrayList<Integer> tagIds = new ArrayList<>();

    public CheckIn(String id, Calendar date) {
        this.id = id;
        this.date = date;
    }

    public CheckIn(JSONObject inputJsonObject) throws JSONException {
        JSONObject jsonObject = inputJsonObject;
        if(inputJsonObject.has("checkin")) {
            jsonObject = inputJsonObject.getJSONObject("checkin");
        }
        this.id = jsonObject.optString("id", null);
        this.createdAt = Date.stringToCalendar(jsonObject.optString("created_at", null));
        this.updatedAt = Date.stringToCalendar(jsonObject.optString("updated_at", null));
        this.date = Date.stringToCalendar(jsonObject.optString("date", null));
        this.note = jsonObject.optString("note", null);
        this.conditions = createTrackableList(TrackableType.CONDITION, jsonObject.getJSONArray("conditions"));
        this.symptoms = createTrackableList(TrackableType.SYMPTOM, jsonObject.getJSONArray("symptoms"));
        this.treatments = createTrackableList(TrackableType.TREATMENT, jsonObject.getJSONArray("treatments"));
    }

    // TODO support tags.

    /**
     * Returns the JSON object representation of the Check In
     * @return JSONObject of the checkin
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

        JSONArray tagIdsJArray = new JSONArray();
        for (Integer tagId : tagIds) {
            tagIdsJArray.put(tagId);
        }
        output.put("tag_ids", tagIdsJArray);
        return output;
    }

    private ArrayList<Trackable> createTrackableList(TrackableType type, JSONArray trackableJArray) throws JSONException {
        ArrayList<Trackable> output = new ArrayList<>();
        for (int i = 0; i < trackableJArray.length(); i++) {
            Trackable trackable;
            JSONObject trackableJObject = trackableJArray.getJSONObject(i);
            if(TrackableType.TREATMENT.equals(type))
                trackable = new TreatmentTrackable(trackableJObject);
            else
                trackable = new Trackable(type, trackableJObject);
            output.add(trackable);
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
            if(treatment instanceof TreatmentTrackable) {
                if (((TreatmentTrackable) treatment).getIsTaken())
                    return true;
            } else if(treatment.getValue() != null)
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

    public boolean removeTrackable(Trackable trackable){
        try{
            switch(trackable.getType()){
                case CONDITION:
                    return conditions.remove(trackable);
                case SYMPTOM:
                    return symptoms.remove(trackable);
                case TREATMENT:
                    return treatments.remove(trackable);
                default:
                    return false;
            }
        }
        catch (Exception e){
            return false;
        }
    }

    /**
     * Get the trackable ids for a specific trackable type.
     * @param trackableType The trackable type.
     * @return The ids inside the trackable type.
     */
    public ArrayList<Integer> getTrackableIds(TrackableType trackableType) {
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<Trackable> trackables = getTrackables(trackableType);
        for (Trackable trackable : trackables) {
            result.add(trackable.getTrackableId());
        }
        return result;
    }

    public void attachMetaTrackables(TrackableType trackableType, MetaTrackable metaTrackable) {
        ArrayList<Trackable> trackables = getTrackables(trackableType);
        for (Trackable trackable : trackables) {
            if(metaTrackable.getId() == trackable.getTrackableId()) {
                trackable.setMetaTrackable(metaTrackable);
                return;
            }
        }
    }

    /**
     * Get the response json for the entire check in.
     * @return The response json for the entire check in.
     * @throws JSONException
     */
    public JSONObject getResponseJson() throws JSONException {
        JSONObject rootJObject = new JSONObject();
        JSONObject checkinJObject = new JSONObject();
        rootJObject.put("checkin", checkinJObject);

        checkinJObject.put("date", Date.calendarToString(date));
        checkinJObject.put("note", note);

        for (TrackableType trackableType : TrackableType.values()) {
            String name = trackableType.name().toLowerCase() + "s_attributes";
            JSONArray trackablesJArray = new JSONArray();
            ArrayList<Trackable> trackables = getTrackables(trackableType);
            for (Trackable trackable : trackables) {
                trackablesJArray.put(trackable.getResponseJson());
            }

            checkinJObject.put(name, trackablesJArray);
        }

        JSONArray tagIdsJArray = new JSONArray();
        for (Integer tagId : tagIds) {
            tagIdsJArray.put(tagId);
        }
        checkinJObject.put("tag_ids", tagIdsJArray);

        return rootJObject;
    }
}
