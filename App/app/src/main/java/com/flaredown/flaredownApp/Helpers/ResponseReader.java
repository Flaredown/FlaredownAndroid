package com.flaredown.flaredownApp.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thunter on 11/12/2015.
 */
public class ResponseReader {
    private JSONArray responses;

    /**
     * Initialises the ResponseReader
     * @param responses JSON Array taken from the entry endpoint names responses.
     */
    public ResponseReader(JSONArray responses) {
        this.responses = responses;
        if(this.responses == null) this.responses = new JSONArray();
    }

    /**
     * Gets the previous response to a question, from a selected catalogue
     * @param catalogue Catalogue name.
     * @param name Question name.
     * @return The value of the previous response, returns "" if no previous response.
     * @throws JSONException In case of difficulty transversing JSON.
     */
    public String getResponse(String catalogue, String name) throws JSONException{
        for(int i = 0; i < responses.length(); i++) {
            JSONObject response = responses.getJSONObject(i);
            if(response.getString("catalog").equals(catalogue) && response.getString("name").equals(name))
                return response.getString("value");
        }
        return "";
    }

    /**
     * Checks if there is a previous response for the specific entry.
     * @return returns true if the responses jsonarray is empty.
     */
    public boolean isEmpty() {
        return responses.length() == 0;
    }
}
