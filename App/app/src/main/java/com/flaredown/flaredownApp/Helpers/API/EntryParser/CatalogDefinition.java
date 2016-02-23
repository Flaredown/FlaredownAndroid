package com.flaredown.flaredownApp.Helpers.API.EntryParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thunter on 19/02/16.
 */
public class CatalogDefinition {

    private String catalogName = "";
    private String definitionName = "";
    private InputKind kind;
    private List<Input> inputs = null;
    private Response response = null;

    /**
     * Creates a catalog definition from JSON object of a catalog definition.
     * @param jsonObject JSON object of a catalog definition.
     * @throws JSONException if JSON object cannot be parsed.
     */
    CatalogDefinition(JSONObject jsonObject, String catalogName) throws JSONException {
        this.catalogName = catalogName;
        this.definitionName = jsonObject.getString("name");
        this.kind = InputKind.toEnum(jsonObject.getString("kind"));

        JSONArray inputsJArray = jsonObject.optJSONArray("inputs");
        if (inputsJArray == null) inputsJArray = new JSONArray();

        for (int i = 0; i < inputsJArray.length(); i++) {
            JSONObject inputJObject = inputsJArray.optJSONObject(i);
            if (inputJObject != null) { // This should always be true, just incase.
                if (inputs == null) {
                    inputs = new ArrayList<>();
                }
                inputs.add(new Input(inputJObject));
            }
        }
    }

    /**
     * Get the JSON object representing this object.
     * @return JSON object representing this object.
     */
    public JSONObject toJSONObject() throws JSONException{
        JSONObject returnJObject = new JSONObject();
        returnJObject.put("name", definitionName);
        returnJObject.put("kind", kind.toString());

        if(inputs != null) {
            JSONArray inputJArray = new JSONArray();
            for (Input input : inputs) {
                inputJArray.put(input.toJSONObject());
            }
            returnJObject.put("inputs", inputJArray);
        }

        if(response != null)
            returnJObject.put("responses", response.toJSONObject());
        return returnJObject;
    }

    /**
     * Get the name of the catalog.
     * @return The name of the catalog.
     */
    public String getCatalogName() {
        return this.catalogName;
    }

    /**
     * Get the definition name for the catalog definition.
     * @return Definition Name.
     */
    public String getDefinitionName() {
        return definitionName;
    }

    /**
     * Set the definition name
     * @param definitionName definition name.
     */
    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }

    /**
     * Get the kind of input.
     * @return
     */
    public InputKind getKind() {
        return kind;
    }

    /**
     * Set the kind of input.
     * @param kind
     */
    public void setKind(InputKind kind) {
        this.kind = kind;
    }

    /**
     * Get the response for this catalog definition.
     * @return The response for this catalog definition.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Set the response for this catalog definitnion.
     * @param response The response for this catalog definition.
     */
    public void setResponse(Response response) {
        this.response = response;
    }
}
