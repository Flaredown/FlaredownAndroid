package com.flaredown.flaredownApp.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class Treatment {
    private String id;
    private String name;
    private int quantity;
    private String unit;

    public Treatment(JSONObject treatment) throws JSONException{
        this.id = treatment.getString("id");
        this.name = treatment.getString("name");
        if (treatment.get("quantity").toString().contains("null")){
            this.quantity = 0;
        } else {
            this.quantity = treatment.getInt("quantity");
        }
        if (treatment.get("unit").toString().contains("null")){
            this.unit = "";
        } else {
            this.unit = treatment.getString("unit");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
