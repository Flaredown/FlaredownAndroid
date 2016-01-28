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

    public static boolean catalogDefinitionHasOneElement(List<CollectionCatalogDefinition> collectionCatalogDefinitions) {
        return collectionCatalogDefinitions != null && collectionCatalogDefinitions.size() > 0 && collectionCatalogDefinitions.size() > 0;
    }
    public static CatalogDefinition getFirstCatalogDefinition(List<CollectionCatalogDefinition> collectionCatalogDefinitions) {
        if(catalogDefinitionHasOneElement(collectionCatalogDefinitions))
            return collectionCatalogDefinitions.get(0).get(0);
        else
            return null;
    }


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
     *///TODO TEST OBJ
    public static List<CollectionCatalogDefinition> getCatalogDefinitions(JSONObject catalogDefinitionsJObject, JSONArray responseJArray) throws JSONException {
        List<Response> responses = getResponses(responseJArray);
        List<CollectionCatalogDefinition> collectionDefinitions = new ArrayList<>();

        Iterator<String> cdJArrayIterator = catalogDefinitionsJObject.keys();
        while(cdJArrayIterator.hasNext()) {
            String catalogName = cdJArrayIterator.next();
            JSONArray catalogDefinitionsJArray = catalogDefinitionsJObject.getJSONArray(catalogName);
            for(int i = 0; i < catalogDefinitionsJArray.length(); i++) {
                CollectionCatalogDefinition collectionCatalogDefinition = new CollectionCatalogDefinition();
                JSONArray catalogDefinitionsJArray2 = catalogDefinitionsJArray.getJSONArray(i);
                for(int j = 0; j < catalogDefinitionsJArray2.length(); j++) {
                    CatalogDefinition catalogDefinition = new CatalogDefinition(catalogName, catalogDefinitionsJArray2.getJSONObject(j));
                    catalogDefinition.setResponse(findResponse(responses, catalogDefinition));
                    collectionCatalogDefinition.add(catalogDefinition);
                }
                collectionDefinitions.add(collectionCatalogDefinition);
            }
        }

        return collectionDefinitions;
    }

    /**
     * Returns a list of catalog definitions from a specific catalog.
     * @param catalog Catalog name to filter by.
     * @param catalogDefinitionLists A list of catalog definitions to filter through.
     * @return The filtered list of catalog definitions.
     *///TODO TEST OBJ
    public static List<CollectionCatalogDefinition> getCatalogDefinitions(String catalog, List<CollectionCatalogDefinition> catalogDefinitionLists) {
        List<CollectionCatalogDefinition> filteredList = new ArrayList<>();
        for (CollectionCatalogDefinition collectionCatalogDefinition : catalogDefinitionLists)
            if(collectionCatalogDefinition.getCatalog().equals(catalog))
                filteredList.add(collectionCatalogDefinition);
        return filteredList;
    }

    /**
     * Retruns a JSON Array of the responses.
     * @param collectionCatalogDefinitions A list of catalog definitions containing responses.
     * @return JSON Array of responses
     *///TODO TEST OBJ
    public JSONArray getResponsesJSONCatalogDefinitionList(List<CollectionCatalogDefinition> collectionCatalogDefinitions) {
        JSONArray outputJArray = new JSONArray();
        for (CollectionCatalogDefinition collectionCatalogDefinition : collectionCatalogDefinitions) {
            for (CatalogDefinition catalogDefinition : collectionCatalogDefinition) {
                outputJArray.put(catalogDefinition.response.getJSONObject());
            }
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
     *///TODO TEST OBJ
    public static JSONObject getCatalogDefinitionsJSON(List<CollectionCatalogDefinition> collectionCatalogDefinitions) {
        JSONObject outputJObject = new JSONObject();
        try {
            for (CollectionCatalogDefinition collectionCatalogDefinition : collectionCatalogDefinitions) {
                for (CatalogDefinition catalogDefinition : collectionCatalogDefinition) {
                    JSONArray catalogJArray = outputJObject.getJSONArray(collectionCatalogDefinition.getCatalog());
                    catalogJArray.put(catalogDefinition.getJSONObject());
                    if (!outputJObject.has(collectionCatalogDefinition.getCatalog()))
                        outputJObject.put(collectionCatalogDefinition.getCatalog(), catalogJArray);
                }
            }
        } catch (JSONException e) {

        }
        return outputJObject;
    }

    public static CatalogDefinition createCatalogDefinition(String catalog, String name, CatalogInputType catalogInputType, List<Input> inputs ) {
        return new CatalogDefinition(catalog, name, catalogInputType, inputs);
    }

    public static List<Input> getDefaultInputSmilies() {
        List<Input> inputs = new ArrayList<>();
        for(int i = 0; i < 5; i++)
            inputs.add(new Input(i).setHelper("basic_" + i).setMetaLabel((i == 0) ? "smiley" : null));
        return inputs;
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


    public enum CatalogInputType {
        SELECT, CHECKBOX, NUMBER
    }

    public static String getCatalogInputTypeString(CatalogInputType cit) {
        switch (cit) {
            case SELECT:
                return "select";
            case CHECKBOX:
                return "checkbox";
            case NUMBER:
                return "number";
        }
        return "";
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

        CatalogDefinition(String catalog, String name, CatalogInputType catalogInputType, List<Input> inputs) {
            this.catalog = catalog;
            this.name = name;
            this.kind = getCatalogInputTypeString(catalogInputType);
            this.inputs = inputs;
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

    public static class CollectionCatalogDefinition extends ArrayList<CatalogDefinition> {

        String catalog = null;

        @Override
        public boolean add(CatalogDefinition object) {
            catalog = object.getCatalog();
            return super.add(object);
        }

        public String getCatalog() {
            return catalog;
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
        private Double step;

        Input(JSONObject inputJObject) throws JSONException{
            value = inputJObject.get("value");
            helper = inputJObject.optString("helper", null);
            metaLabel = inputJObject.optString("meta_label", null);
            label = inputJObject.optString("label", null);
            step = inputJObject.optDouble("step", 0);
        }

        Input(Object value) {
            this.value = value;
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
                outputJObject.putOpt("step", step);
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

        public Double getStep() {
            return step;
        }

        public Input setHelper(String helper) {
            this.helper = helper;
            return this;
        }

        public Input setMetaLabel(String metaLabel) {
            this.metaLabel = metaLabel;
            return this;
        }
    }
}
