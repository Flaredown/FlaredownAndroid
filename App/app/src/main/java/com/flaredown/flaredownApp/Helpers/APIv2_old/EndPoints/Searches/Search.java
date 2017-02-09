package com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.Searches;

import com.flaredown.flaredownApp.Helpers.APIv2_old.Helper.Date;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;

public class Search implements Serializable {
    private String id;
    private Calendar createdAt;
    private Calendar updatedAt;
    private Searchables searchables;

    public Search(JSONObject inputJsonObject) throws JSONException{
        JSONObject search = inputJsonObject.getJSONObject("search");
        this.id = search.optString("id",null);
        this.createdAt = Date.stringToCalendar(search.optString("created_at", null));
        this.updatedAt = Date.stringToCalendar(search.optString("updated_at", null));
        this.searchables = new Searchables(search.getJSONArray("searchables"));
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

    public Searchables getSearchables() {
        return searchables;
    }

    public void setSearchables(Searchables searchables) {
        this.searchables = searchables;
    }
}
