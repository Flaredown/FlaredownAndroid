package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Represents the json returned from the trackings endpoint.
 */
public class Trackings extends ArrayList<Tracking> {
    public Trackings(){}

    /**
     * Create a Trackings object from the JSON object representation.
     * @param jsonObject The JSON equivalent of the trackings endpoint.
     * @throws JSONException Throws if the JSON object cannot be parsed.
     */
    public Trackings(JSONObject jsonObject) throws JSONException {
        JSONArray trackings = jsonObject.getJSONArray("trackings");
        for (int i = 0; i < trackings.length(); i++) {
            this.add(new Tracking(trackings.getJSONObject(i)));
        }
    }

    public JSONObject toJson() throws JSONException{
        JSONObject output = new JSONObject();
        JSONArray trackingsArray = new JSONArray();

        for (Tracking tracking : this) {
            trackingsArray.put(tracking.toJson());
        }

        output.put("trackings", trackingsArray);

        return output;
    }
}
