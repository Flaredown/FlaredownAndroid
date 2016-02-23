package com.flaredown.flaredownApp.Helpers.API.EntryParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thunter on 19/02/16.
 */
public class Response {
    private String catalogName;
    private Object value;
    private String definitionName;

    /**
     * Create response object with the JSON representation.
     * @param responseJObject JSON representation.
     * @throws JSONException
     */
    Response(JSONObject responseJObject) throws JSONException{
        this.catalogName = responseJObject.getString("catalog");
        this.value = responseJObject.get("value");
        this.definitionName = responseJObject.getString("name");
    }

    /**
     * Default constructor for the Response object.
     * @param catalogName The catalog name for the response.
     * @param definitionName The definition name for the response.
     * @param value The value for the response.
     */
    Response(String catalogName, String definitionName, Object value) {
        this.catalogName = catalogName;
        this.definitionName = definitionName;
        this.value = value;
    }

    /**
     * Get the JSON representation for this object.
     * @return The JSON representation for this object.
     */
    public JSONObject toJSONObject() throws JSONException{
        JSONObject returnJObject = new JSONObject();
        returnJObject.put("name", this.definitionName);
        returnJObject.put("catalog", this.catalogName);
        returnJObject.putOpt("value", this.value);
        return returnJObject;
    }

    /**
     * Get the response value.
     * @return The response value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Get the catalog name.
     * @return The catalog name.
     */
    public String getCatalogName() {

        return catalogName;
    }

    /**
     * Get the definition name.
     * @return The definition name.
     */
    public String getDefinitionName() {
        return definitionName;
    }
}
