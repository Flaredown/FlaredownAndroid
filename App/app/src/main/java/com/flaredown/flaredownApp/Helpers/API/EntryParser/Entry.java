package com.flaredown.flaredownApp.Helpers.API.EntryParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Used to Parse the entry endpoint.
 */
public class Entry extends ArrayList<CollectionCatalogDefinition>{
    private Date entryDate;
    private String notes = "";

    /**
     * Creates an Entry object from an entry json object returned from the Flaredown API.
     * @param entryJObject Entry json object returned from the Flaredown API.
     * @throws JSONException if json cannot be parsed.
     */
    public Entry(JSONObject entryJObject) throws JSONException {
        JSONObject entry = entryJObject.getJSONObject("entry");
        JSONObject catalogsJObject = entry.getJSONObject("catalog_definitions");

        Iterator<String> catalogsIterator = catalogsJObject.keys();
        while(catalogsIterator.hasNext()) {
            String key = catalogsIterator.next();
            JSONArray collectionCDJArrays= catalogsJObject.getJSONArray(key);
            for(int i = 0; i < collectionCDJArrays.length(); i++) {
                JSONArray collectionCDJArray = collectionCDJArrays.getJSONArray(i);
                this.add(new CollectionCatalogDefinition(collectionCDJArray, key));
            }
        }

        if(entry.has("responses")) {
            this.setResponeses(entry.getJSONArray("responses"));
        }
    }

    /**
     * Set responses with a JSON Array.
     * @param responsesJArray Resposnes represented as a JSON Array.
     */
    public void setResponeses(JSONArray responsesJArray) throws JSONException{
        for(int i = 0; i < responsesJArray.length(); i++) {
            JSONObject responseJObject = responsesJArray.getJSONObject(i);
            Response response = new Response(responseJObject);
            this.addResponse(response);
        }
    }

    /**
     * Add a single response
     * @param response
     * @throws NullPointerException if no definition name and catalog name.
     * @throws IndexOutOfBoundsException if cannot find catalog definition.
     */
    public void addResponse(Response response) throws NullPointerException, IndexOutOfBoundsException{
        try {
            this.findCatalogDefinition(response.getCatalogName(), response.getDefinitionName()).setResponse(response);
        } catch (NullPointerException e) {
            throw new IndexOutOfBoundsException("Could not find catalog definition");
        }
    }

    public CatalogDefinition findCatalogDefinition(String catalogName, String definitionName) {
        for (CollectionCatalogDefinition collectionCatalogDefinition : this) {
            for (CatalogDefinition catalogDefinition : collectionCatalogDefinition) {
                if(catalogName.equals(catalogDefinition.getCatalogName()) && definitionName.equals(catalogDefinition.getDefinitionName()))
                    return catalogDefinition;
            }
        }
        return null;
    }

    /**
     * Creates a JSONObject which can be used to initiate the object.
     * @return JSONObject representing the Entry object.
     */
    public JSONObject toJSONObject() throws JSONException{
        JSONObject returnValue = new JSONObject();
        JSONObject entryJObject = new JSONObject();
        returnValue.put("entry", entryJObject);

        JSONObject catalogsJObject = new JSONObject();
        entryJObject.put("catalog_definitions", catalogsJObject);
        for (CollectionCatalogDefinition catalogDefinitions : this) {
            JSONArray collectionJObject = new JSONArray();
            if(catalogsJObject.has(catalogDefinitions.getCatalogName()))
                collectionJObject = catalogsJObject.getJSONArray(catalogDefinitions.getCatalogName());
            else
                catalogsJObject.put(catalogDefinitions.getCatalogName(), collectionJObject);
            collectionJObject.put(catalogDefinitions.toJSONArray());
        }


        return returnValue;
    }

    /**
     * Adds collection catalog definition.
     * Method deprecated use the .add method instead.
     * @param collectionCatalogDefinition
     */
    @Deprecated
    public void addCollectionCatalogDefinition(CollectionCatalogDefinition collectionCatalogDefinition) {
        this.add(collectionCatalogDefinition);
    }

    /**
     * Returns the date for the entry.
     * @return The date for the entry.
     */
    public Date getDate() {
        return this.entryDate;
    }

    /**
     * Returns the notes for the entry.
     * @return The notes for the entry.
     */
    public String getNotes() {
        return this.notes;
    }
}
