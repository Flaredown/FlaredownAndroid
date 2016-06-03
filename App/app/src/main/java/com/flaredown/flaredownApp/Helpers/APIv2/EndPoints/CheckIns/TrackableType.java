package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import com.flaredown.flaredownApp.R;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import io.realm.RealmObject;

/**
 * The type of trackable.
 */
public enum TrackableType {
    CONDITION(R.string.locales_conditions, R.string.locales_conditions_question_prompt, true),
    SYMPTOM(R.string.locales_symptoms, R.string.locales_how_active_were_your_symptoms, true),
    TREATMENT(R.string.locales_treatments, R.string.locales_which_treatments_taken_today, true),
    TAG(0, 0, false);

    private int nameResId;
    private int questionResId;
    private boolean isTrackable;

    /**
     * Lists all enums that are trackable (Condition, Symptom and Treatment)
     * @return Array of trackable enums.
     */
    public static TrackableType[] trackableValues() {
        List<TrackableType> list = new LinkedList<>();
        for (TrackableType trackableType : TrackableType.values()) {
            if(trackableType.isTrackable())
                list.add(trackableType);
        }
        return list.toArray(new TrackableType[list.size()]);
    }

    TrackableType(@android.support.annotation.StringRes int nameResId, @android.support.annotation.StringRes int questionResId, boolean isTrackable) {
        this.nameResId = nameResId;
        this.questionResId = questionResId;
        this.isTrackable = isTrackable;
    }

    public boolean isTrackable() {
        return isTrackable;
    }

    public int getNameResId() {
        return nameResId;
    }

    public int getQuestionResId() {
        return questionResId;
    }

    /**
     * Get the Json key for the trackable id (for example. condition_id, symptom_id, treatment_id).
     * @return The json key for the trackable id.
     */
    public String getTrackableIdKey() {
        return this.name().toLowerCase() + "_id";
    }

    /**
     * Allows to search for plurals.
     * @param name The name to search for.
     */
    public static TrackableType valueOfs(String name) throws IllegalArgumentException {
        try {
            return TrackableType.valueOf(name.toUpperCase());
        } catch (Exception e) {
            return TrackableType.valueOf(name.toUpperCase().substring(0, name.length() - 1));
        }
    }

    /**
     * Formats the type with a capital letter like the /trackings endpoint wants
     * @return String
     */
    public String getTrackingsFormattedType(){
        return Character.toUpperCase(this.name().charAt(0)) + this.name().substring(1).toLowerCase();
    }
}
