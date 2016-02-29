package com.flaredown.flaredownApp.Helpers.API.EntryParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by thunter on 28/02/16.
 */
public class Responses extends ArrayList<Response> {


    /**
     * Convert Responses to a JSON object, { response : [...]}
     * @return JSON object representing responses.
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject resultJObject = new JSONObject();
        resultJObject.put("responses", this.toJSONArray());
        return resultJObject;
    }


    /**
     * Convert Response to a JSON array.
     * @return JSON array representing responses.
     */
    public JSONArray toJSONArray() throws JSONException{
        JSONArray resultJArray = new JSONArray();
        for (Response response : this) {
            resultJArray.put(response.toJSONObject());
        }
        return resultJArray;
    }
}
