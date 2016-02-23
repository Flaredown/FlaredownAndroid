package com.flaredown.flaredownApp.Helpers.API.EntryParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thunter on 21/02/16.
 */
public class Input {
    Object value;
    String label;
    String metaLabel;
    String helper;

    /**
     * Default consturctor for input.
     */
    public Input() {

    }

    /**
     * Create Input object from JSON Object representation.
     * @param inputJObject Input object JSON Object representation.
     * @throws JSONException
     */
    public Input(JSONObject inputJObject) throws JSONException{
        this.value = inputJObject.get("value");
        this.label = inputJObject.optString("label", null);
        this.metaLabel = inputJObject.optString("meta_label", null);
        this.helper = inputJObject.optString("helper", null);
    }

    /**
     * Convert object to JSON object representation.
     * @return JSON object representing this Input.
     * @throws JSONException
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject returnJObject = new JSONObject();

        returnJObject.put("value", value);
        returnJObject.putOpt("label", label);
        returnJObject.putOpt("meta_label", metaLabel);
        returnJObject.putOpt("helper", helper);

        return returnJObject;
    }


}
