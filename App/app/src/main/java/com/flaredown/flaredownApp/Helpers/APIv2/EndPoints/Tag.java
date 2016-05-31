package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints;

import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Used to represent a tag.
 */
public class Tag implements Serializable{
    private int id;
    private String name;
    private String type;
    private Calendar created_at;
    private Calendar updated_at;

    /**
     * Create a new tag object.
     * @param name The name of the tag.
     * @param type The type of tag? normally tag.
     */
    public Tag(String name, String type) {
        this(0, name, type);
    }

    /**
     * Create a new tag object.
     * @param id The id of the tag.
     * @param name The name of the tag.
     * @param type The type of tag? normally tag.
     */
    public Tag(Integer id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.created_at = Calendar.getInstance();
        this.updated_at = Calendar.getInstance();
    }

    /**
     * Create a tag object from the json representation.
     * @param jObject The json object to initialise the tag fields.
     * @throws JSONException
     */
    public Tag(JSONObject jObject) throws JSONException {
        setId(jObject.getInt("id"));
        setName(jObject.optString("name"));
        setType(jObject.optString("type"));
        setUpdated_at(Date.stringToCalendar(jObject.optString("updated_at")));
        setCreated_at(Date.stringToCalendar(jObject.optString("created_at")));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Calendar getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Calendar created_at) {
        this.created_at = created_at;
    }

    public Calendar getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Calendar updated_at) {
        this.updated_at = updated_at;
    }

    /**
     * Convert an json array of tags to a list of tag objects.
     * @param jArray Json array of tag json objects.
     * @return List of tag objects.
     * @throws JSONException Thrown if parsing of json array fails.
     */
    public static List<Tag> convertList(JSONArray jArray) throws JSONException {
        List<Tag> data = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++) {
            data.add(new Tag(jArray.getJSONObject(i)));
        }
        return data;
    }
}
