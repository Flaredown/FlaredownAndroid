package com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.CheckIns;

import com.flaredown.flaredownApp.Helpers.APIv2_old.Helper.Date;
import com.flaredown.flaredownApp.Helpers.Observers.ObservableHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;

import rx.Subscriber;
import rx.functions.Action1;


/**
 * An element of the JSON returned from the check in endpoint.
 */
public class CheckIn implements Serializable{
    // Used for notifying changes to the check in (add/remove/value change of trackables etc.)
    private transient ObservableHelper<Void> checkInChangeObservable = new ObservableHelper<>();


    private String id;
    private Calendar createdAt;
    private Calendar updatedAt;
    private Calendar date;
    private String note;
    private transient ObservableHelper<String> noteObserverable = new ObservableHelper<>();
    private TrackableCollection<Trackable> conditions = new TrackableCollection<>();
    private TrackableCollection<Trackable> symptoms = new TrackableCollection<>();
    private TrackableCollection<Trackable> treatments = new TrackableCollection<>();
    private TagCollection<Tag> tags = new TagCollection<>();

    public CheckIn(String id, Calendar date) {
        this.id = id;
        this.date = date;
        readResolver();
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

        JSONArray tagIdJArray = jsonObject.getJSONArray("tag_ids");
        for (int i = 0; i < tagIdJArray.length(); i++) {
            this.tags.add(new Tag(tagIdJArray.getInt(i)));
        }
        readResolver();
    }

    /**
     * Run on construction and when object is dematerialized.
     * @return
     */
    public Object readResolver() {
        // Emits checkInChangeObservable when the data changes inside a trackable collection.
        for (TrackableType trackableType : TrackableType.trackableValues()) {
            TrackableCollection<Trackable> tc = getTrackables(trackableType);
            tc.getDataChangeObservable().subscribe(new Subscriber<Void>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Void aVoid) {
                    checkInChangeObservable.notifySubscribers(null);
                }
            });
        }

        // Emits checkInChangeObservable when the tags collection changes.
        tags.subscribeCollectionObservable(new Subscriber<ObservableHashSet.CollectionChange<Tag>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ObservableHashSet.CollectionChange<Tag> collectionChange) {
                checkInChangeObservable.notifySubscribers(null);
            }
        });

        // Emits checkInChangeObservable when the notes string changes.
        noteObserverable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                checkInChangeObservable.notifySubscribers(null);
            }
        });

        return this;
    }

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
        for (Tag tag : tags) {
            tagIdsJArray.put(tag.getId());
        }
        output.put("tag_ids", tagIdsJArray);
        return output;
    }

    private TrackableCollection<Trackable> createTrackableList(TrackableType type, JSONArray trackableJArray) throws JSONException {
        TrackableCollection<Trackable> output = new TrackableCollection<>();
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

    private JSONArray createTrackableJArray(TrackableCollection<Trackable> trackableList) throws JSONException {
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
        if(tags.size() > 0) return true;

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
        noteObserverable.notifySubscribers(note);
    }

    public TrackableCollection<Trackable> getConditions() {
        return conditions;
    }

    public void setConditions(TrackableCollection<Trackable> conditions) {
        this.conditions = conditions;
    }

    public TrackableCollection<Trackable> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(TrackableCollection<Trackable> symptoms) {
        this.symptoms = symptoms;
    }

    public TrackableCollection<Trackable> getTreatments() {
        return treatments;
    }

    public void setTreatments(TrackableCollection<Trackable> treatments) {
        this.treatments = treatments;
    }

    /**
     * Returns an ArrayList of trackables for the specific trackable type.
     * @param trackableType The trackable type for the array returned.
     * @return ArrayLost of trackables for the specific trackable type.
     */
    public TrackableCollection<Trackable> getTrackables(TrackableType trackableType) {
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
    public HashSet<Integer> getTrackableIds(TrackableType trackableType) {
        HashSet<Integer> result = new HashSet<>();
        if(trackableType.isTrackable()) {
            TrackableCollection<Trackable> trackables = getTrackables(trackableType);
            for (Trackable trackable : trackables) {
                result.add(trackable.getTrackableId());
            }
        } else if(TrackableType.TAG.equals(trackableType)) {
            for (Tag tag : tags) {
                result.add(tag.getId());
            }
        }
        return result;
    }

    public void attachMetaTrackables(TrackableType trackableType, MetaTrackable metaTrackable) {
        if(trackableType.equals(TrackableType.TAG)) {
            TagCollection<Tag> tags = getTags();
            for (Tag tag : tags) {
                if(metaTrackable.getId().equals(tag.getId())){
                    tag.setMetaTrackable(metaTrackable);
                }
            }
        } else {
            TrackableCollection<Trackable> trackables = getTrackables(trackableType);
            for (Trackable trackable : trackables) {
                if (metaTrackable.getId() == trackable.getTrackableId()) {
                    trackable.setMetaTrackable(metaTrackable);
                    return;
                }
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

        for (TrackableType trackableType : TrackableType.trackableValues()) {
            String name = trackableType.name().toLowerCase() + "s_attributes";
            JSONArray trackablesJArray = new JSONArray();
            TrackableCollection<Trackable> trackables = getTrackables(trackableType);
            for (Trackable trackable : trackables) {
                trackablesJArray.put(trackable.getResponseJson(this));
            }

            checkinJObject.put(name, trackablesJArray);
        }

        JSONArray tagIdsJArray = new JSONArray();
        for (Tag tag : tags) {
            tagIdsJArray.put(tag.getId());
        }
        checkinJObject.put("tag_ids", tagIdsJArray);

        return rootJObject;
    }

    /**
     * Get a list of tags for the check in.
     * @return A list of tags
     */
    public TagCollection<Tag> getTags() {
        return tags;
    }

    /**
     * Get a hash set of tag ids.
     * @return Tag ids.
     */
    public HashSet<Integer> getTagIds() {
        HashSet<Integer> results = new HashSet<>();
        for (Tag tag : tags) {
            results.add(tag.getId());
        }
        return results;
    }

    /**
     * Get the check in change observable... This observable emits when.
     *      - A trackable is added, removed or value is changed.
     * @return Observable helper for the check in change.
     */
    public ObservableHelper<Void> getCheckInChangeObservable() {
        return checkInChangeObservable;
    }

    /**
     * Set the list of tags for the check in.
     * @param tags The list of tags to be associated with the check in.
     */
    public void setTags(TagCollection<Tag> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }

    /**
     * Add a tag to the check in.
     * @param tag The tag to add to the check in.
     */
    public void addTag(Tag tag) {
        tags.add(tag);
    }

    /**
     * Remove a specific tag from the check in.
     * @param tag
     */
    public void removeTag(Tag tag) {
        tags.remove(tag);
    }
}
