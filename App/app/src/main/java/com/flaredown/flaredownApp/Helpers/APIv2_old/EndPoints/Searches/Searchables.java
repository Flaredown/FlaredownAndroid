package com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.Searches;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by squiggie on 4/28/16.
 */
public class Searchables extends ArrayList<Searchable> implements Serializable {

    public Searchables(){}

    /**
     * Create a Searchables object from the JSON object representation.
     * @param inputJsonArray the json array of searchables
     * @throws JSONException Throws if the JSON array cannot be parsed.
     */
    public Searchables(JSONArray inputJsonArray) throws JSONException {
        for (int i = 0; i < inputJsonArray.length(); i++) {
            this.add(new Searchable(inputJsonArray.getJSONObject(i)));
        }
    }

    public JSONObject toJson() throws JSONException{
        JSONObject output = new JSONObject();
        JSONArray searchablesJArray = new JSONArray();

        for (Searchable searchable : this) {
            searchablesJArray.put(searchable.toJson());
        }

        output.put("searchables", searchablesJArray);

        return output;
    }
}
