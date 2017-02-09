package com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.CheckIns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the json returned from the check in endpoint.
 */
public class CheckIns extends ArrayList<CheckIn> implements Serializable {
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

    public JSONObject toJson() throws JSONException{
        JSONObject output = new JSONObject();
        JSONArray checkinsJArray = new JSONArray();

        for (CheckIn checkIn : this) {
            checkinsJArray.put(checkIn.toJson());
        }

        output.put("checkins", checkinsJArray);

        return output;
    }
}
