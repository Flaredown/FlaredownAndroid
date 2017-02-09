package com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.Profile;

import com.flaredown.flaredownApp.Helpers.APIv2_old.Helper.Date;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Country {
    private String id;
    private Calendar created_at;
    private Calendar updated_at;
    private String name;

    public Country(JSONObject country) throws JSONException{
        this.id = country.getString("id");
        this.created_at = Date.stringToCalendar(country.optString("created_at", null));
        this.updated_at = Date.stringToCalendar(country.optString("updated_at", null));
        this.name = country.getString("name");

    }

    public String getId() {
        return id;
    }

    public Calendar getCreated_at() {
        return created_at;
    }

    public Calendar getUpdated_at() {
        return updated_at;
    }

    public String getName() {
        return name;
    }
}
