package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Represents the json returned from the check in endpoint.
 */
public class CheckIns extends ArrayList<CheckIn> {
    public CheckIns() {

    }

    /**
     * Create a CheckIns object from the JSON object representation.
     * @param jsonObject The JSON equivalent of the check in endpoint.
     * @throws JSONException Throws if the JSON object cannot be parsed.
     */
    public CheckIns(JSONObject jsonObject) throws JSONException {
        JSONArray checkInsJArray = jsonObject.getJSONArray("checkins");
        for (int i = 0; i < checkInsJArray.length(); i++) {
            this.add(new CheckIn(checkInsJArray.getJSONObject(i)));
        }
    }
}
