package com.flaredown.flaredownApp.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class Treatment {
    private int id;
    private String created_at;
    private String updated_at;
    private String type;
    private int color_id;
    private String name;

    public Treatment(){}

    public Treatment(JSONObject treatment) throws JSONException{
        this.id = treatment.getInt("id");
        this.created_at = treatment.getString("created_at");
        this.updated_at = treatment.getString("updated_at");
        this.type = treatment.getString("type");
        this.color_id = treatment.getInt("color_id");
        this.name = treatment.getString("name");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getColor_id() {
        return color_id;
    }

    public void setColor_id(int color_id) {
        this.color_id = color_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
