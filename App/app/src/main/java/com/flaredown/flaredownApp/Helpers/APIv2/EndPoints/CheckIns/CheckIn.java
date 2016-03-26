package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;

/**
 * An element of the JSON returned from the check in endpoint.
 */
public class CheckIn {
    private String id;
    private Calendar createdAt;
    private Calendar updatedAt;
    private Calendar date;
    private String note;

    public CheckIn(String id, Calendar date) {
        this.id = id;
        this.date = date;
    }

    public CheckIn(JSONObject jsonObject) {
        this.id = jsonObject.optString("id", null);
        this.createdAt = Date.stringToCalendar(jsonObject.optString("created_at", null));
        this.createdAt = Date.stringToCalendar(jsonObject.optString("updated_at", null));
        this.date = Date.stringToCalendar(jsonObject.optString("date", null));
        this.note = jsonObject.optString("note", null);
    }

    // TODO support tags.

    // TODO support conditions.

    // TODO support symptoms.

    // TODO support treatments.

    /**
     * Returns the JSON object representation of the CheckIn
     * @return
     */
    public JSONObject toJson() throws JSONException {
        JSONObject output = new JSONObject();

        output.put("id", this.id);
        output.put("created_at", Date.calendarToString(this.createdAt));
        output.put("updated_at", Date.calendarToString(this.updatedAt));
        output.put("date", Date.calendarToString(this.date));
        output.put("note", this.note);

        return output;
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
}
