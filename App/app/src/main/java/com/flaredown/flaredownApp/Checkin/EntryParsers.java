package com.flaredown.flaredownApp.Checkin;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by thunter on 25/01/16.
 */
public class EntryParsers {

    public static final List<String> CATALOG_NAMES = new ArrayList<>(Arrays.asList("symptoms", "conditions", "treatments"));


    /**
     * Gets a list of Response objects created from the responses JSONArray.
     * @param reponsesJsonArray JsonArray of responses.
     * @return List of response objects.
     * @throws JSONException
     */
    public static List<Response> getResponses(JSONArray reponsesJsonArray) throws JSONException{
        List<Response> responses = new ArrayList<>();
        for(int i = 0; i < reponsesJsonArray.length(); i++) {
            responses.add(new Response(reponsesJsonArray.getJSONObject(i)));
        }
        return responses;
    }

    /**
     * Gets a list of catalog definition objects created from the responses JSONArray.
     * @param catalogDefinitionsJObject JSONObject of catalog definitions.
     * @param responseJArray if no response pass an empty json array.
     * @return A List of catalog definitions.
     * @throws JSONException
     */
    public static List<List<CatalogDefinition>> getCatalogDefinitions(JSONObject catalogDefinitionsJObject, JSONArray responseJArray) throws JSONException {
        List<Response> responses = getResponses(responseJArray);
        List<List<CatalogDefinition>> catalogDefinitionsList = new ArrayList<>();
        Iterator<String> cdJArrayIterator = catalogDefinitionsJObject.keys();
        while(cdJArrayIterator.hasNext()) {
            String catalogName = cdJArrayIterator.next();
            JSONArray catalogDefinitionsJArray = catalogDefinitionsJObject.getJSONArray(catalogName);
            for(int i = 0; i < catalogDefinitionsJArray.length(); i++) {
                List<CatalogDefinition> catalogDefinitions = new ArrayList<>();
                JSONArray catalogDefinitionsJArray2 = catalogDefinitionsJArray.getJSONArray(i);
                for(int j = 0; j < catalogDefinitionsJArray2.length(); j++) {
                    CatalogDefinition catalogDefinition = new CatalogDefinition(catalogName, catalogDefinitionsJArray2.getJSONObject(j));
                    catalogDefinition.setResponse(findResponse(responses, catalogDefinition));
                    catalogDefinitions.add(catalogDefinition);
                }
                catalogDefinitionsList.add(catalogDefinitions);
            }
        }
        return catalogDefinitionsList;
    }

    /**
     * Returns a list of catalog definitions from a specific catalog.
     * @param catalog Catalog name to filter by.
     * @param catalogDefinitionLists A list of catalog definitions to filter through.
     * @return The filtered list of catalog definitions.
     */
    public static List<List<CatalogDefinition>> getCatalogDefinitions(String catalog, List<List<CatalogDefinition>> catalogDefinitionLists) {
        List<List<CatalogDefinition>> filteredList = new ArrayList<>();
        for (List<CatalogDefinition> catalogDefinitions : catalogDefinitionLists)
            if(catalogDefinitions.size() > 0 && catalogDefinitions.get(0).getCatalog().equals(catalog))
                filteredList.add(catalogDefinitions);
        return filteredList;
    }

    /**
     * Retruns a JSON Array of the responses.
     * @param catalogDefinitions A list of catalog definitions containing responses.
     * @return JSON Array of responses
     */
    public JSONArray getResponsesJSONCatalogDefinitionList(List<CatalogDefinition> catalogDefinitions) {
        JSONArray outputJArray = new JSONArray();
        for (CatalogDefinition catalogDefinition : catalogDefinitions) {
            outputJArray.put(catalogDefinition.response.getJSONObject());
        }
        return outputJArray;
    }

    /**
     * Returns a JSON Array of the responses.
     * @param responses List of responses.
     * @return JSON Array of the responses.
     */
    public JSONArray getResponsesJSONResponseList(List<Response> responses) {
        JSONArray outputJArray = new JSONArray();
        for (Response response : responses) {
            outputJArray.put(response);
        }
        return outputJArray;
    }

    /**
     * Returns a JSON Object of the catalog definitions.
     * @return
     */
    public static JSONObject getCatalogDefinitionsJSON(List<CatalogDefinition> catalogDefinitions) {
        JSONObject outputJObject = new JSONObject();
        try {
            for (CatalogDefinition catalogDefinition : catalogDefinitions) {
                if (!outputJObject.has(catalogDefinition.getCatalog()))
                    outputJObject.put(catalogDefinition.getCatalog(), new JSONArray());
                JSONArray catalogJArray = outputJObject.getJSONArray(catalogDefinition.getCatalog());
                catalogJArray.put(catalogDefinition.getJSONObject());
            }
        } catch (JSONException e) {

        }
        return outputJObject;
    }

    /**
     * Returns a JSON array of the inputs for a catalog definition.
     * @param inputs A list of input values.
     * @return JSON array of input values.
     */
    public static JSONArray getInputsJSONArray(List<Input> inputs) {
        JSONArray outputJArray = new JSONArray();
        for (Input input : inputs) {
            outputJArray.put(input.getJSONObject());
        }
        return outputJArray;
    }

    /**
     * Finds a response for a catalog defintion.
     * @param responses List of responses to search through.
     * @param catalogDefinition The catalog definition the response must match.
     * @return returns the matching response object (returns null if not found).
     */
    public static Response findResponse(List<Response> responses, CatalogDefinition catalogDefinition) {
        return findResponse(responses, catalogDefinition.catalog, catalogDefinition.name);
    }

    /**
     * Finds a response for a catalog definition.
     * @param responses A list of responses to search through.
     * @param catalogName The catalog name for the search.
     * @param questionName The question name for the search.
     * @return returns the correct response object (returns null if not found).
     */
    public static Response findResponse(List<Response> responses, String catalogName, String questionName) {
        for (Response response : responses) {
            if(response.catalog.equals(catalogName) && response.name.equals(questionName))
                return response;
        }
        return null;
    }

    /**
     * Stores Response values.
     */
    public static class Response {
        private String name;
        private Object value;
        private String catalog;
        Response(JSONObject responseItem) throws JSONException{
            name = responseItem.getString("name");
            value = responseItem.get("value");
            catalog = responseItem.getString("catalog");
        }

        /**
         * Get a JSON Object interpretation of the Response object.
         * @return JSON Object interpretation of the Response object.
         */
        public JSONObject getJSONObject() {
            JSONObject outputJObject = new JSONObject();
            try {
                outputJObject.put("name", name);
                outputJObject.put("value", value);
                outputJObject.put("catalog", catalog);
            } catch (JSONException e) {}
            return outputJObject;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    /**
     * Stores catalog definitions (check in questions).
     */
    public static class CatalogDefinition {
        private String name;
        private String catalog;
        private String kind;
        private Response response = null;
        private List<Input> inputs;
        CatalogDefinition(String catalog, JSONObject catalogDefinitionJson) throws JSONException{
            name = catalogDefinitionJson.getString("name");
            kind = catalogDefinitionJson.getString("kind");
            this.catalog = catalog;
            JSONArray inputsJArray = catalogDefinitionJson.optJSONArray("inputs");
            if(inputsJArray != null) {
                inputs = new ArrayList<>();
                for(int i = 0; i < inputsJArray.length(); i++) {
                    inputs.add(new Input(inputsJArray.getJSONObject(i)));
                }
            }
        }

        /**
         * Get a JSON Object interpretation of the CatalogDefinition object.
         * @return JSON Object interpretation of the Catalog Definition object.
         */
        public JSONObject getJSONObject() {
            JSONObject outputJObject = new JSONObject();
            try {
                outputJObject.put("name", name);
                outputJObject.put("kind", kind);
                if(inputs != null)
                    outputJObject.put("inputs", getInputsJSONArray(inputs));
            } catch(JSONException e) {

            }
            return outputJObject;
        }

        public String getName() {
            return name;
        }

        public List<Input> getInputs() {
            return inputs;
        }

        public String getCatalog() {
            return catalog;
        }

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        public String getKind() {
            return kind;
        }
    }

    /**
     * Stores values for a select input question.
     */
    public static class Input {
        private Object value;
        private String helper;
        private String label;
        private String metaLabel;

        Input(JSONObject inputJObject) throws JSONException{
            value = inputJObject.get("value");
            helper = inputJObject.optString("helper", null);
            metaLabel = inputJObject.optString("meta_label", null);
            label = inputJObject.optString("label", null);
        }

        /**
         * Get a JSON Object interpretation of the Input object.
         * @return JSON Object interpretation of the Input object.
         */
        public JSONObject getJSONObject() {
            JSONObject outputJObject = new JSONObject();
            try {
                outputJObject.put("value", value);
                outputJObject.putOpt("helper", helper);
                outputJObject.putOpt("meta_label", metaLabel);
                outputJObject.putOpt("label", label);
            } catch (JSONException e) {

            }
            return outputJObject;
        }

        public String getMetaLabel() {
            return metaLabel;
        }

        public Object getValue() {
            return value;
        }

        public String getLabel() {
            return label;
        }
    }
}
