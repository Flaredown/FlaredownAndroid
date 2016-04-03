package com.flaredown.flaredownApp.Helpers.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Used to store post/get parameters and header attributes.
 */
public class WebAttributes extends HashMap<String, String> {

    public JSONObject getJSON() throws JSONException{
        return new JSONObject(this);
    }

}
