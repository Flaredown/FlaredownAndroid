package com.flaredown.flaredownApp.Helpers.API.EntryParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
     * Create Entry object with a list of CollectionCatalogDefinitions.
     * @param list List of CollectionCatalogDefinitions.
     */
    public Entry(List<CollectionCatalogDefinition> list) {
        super(list);
    }

    /**
     * Default constructor for the Entry class.
     */
    public Entry() {

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

        entryJObject.put("responses", this.getResponses().toJSONArray());


        return returnValue;
    }

    /**
     * Get responses for the entry.
     * @return Get the responses for the entry.
     */
    public Responses getResponses() {
        Responses responses = new Responses();
        for (CollectionCatalogDefinition collectionCatalogDefinition : this) {
            for (CatalogDefinition catalogDefinition : collectionCatalogDefinition) {
                if(catalogDefinition.getResponse() != null) {
                    responses.add(catalogDefinition.getResponse());
                }
            }
        }
        return responses;
    }

    public boolean hasResponse() {
        for (CollectionCatalogDefinition catalogDefinitions : this) {
            for (CatalogDefinition catalogDefinition : catalogDefinitions) {
                if(catalogDefinition.getResponse() != null)
                    return true;
            }
        }
        return false;
    }

    /**
     * Has entry have at least one catalog definition.
     * @return True if entry has one catalog definition.
     */
    public boolean hasCatalogDefintion() {
        for (CollectionCatalogDefinition collectionCatalogDefinition : this) {
            if(collectionCatalogDefinition.size() > 0)
                return true;
        }
        return false;
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
    @Deprecated // TODO MAKE THIS WORK
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


    /**
     * Get an entry with a specific catalog.
     * @param catalogName The specific catalog.
     * @return Entry with a specific catalog.
     */
    public Entry getCatalog(String catalogName) {
        Entry entry = new Entry();
        for (CollectionCatalogDefinition collectionCatalogDefinition : this) {
            if(collectionCatalogDefinition.getCatalogName().equals(catalogName))
                entry.add(collectionCatalogDefinition);
        }
        return entry;
    }

    /**
     * Remove a catalog definition, safely from the entry.
     * @param catalogName The catalog name for the definition.
     * @param definitionName The definition name for the definition.
     * @return True if item was successfully removed.
     */
    public boolean removeDefinition(String catalogName, String definitionName) {
        boolean successful = false;
        for (int i = 0; i < this.size(); i++) {

        }
        for (int i = 0; i < this.size(); i++) {
            CollectionCatalogDefinition collectionCatalogDefinition = this.get(i);
            if(collectionCatalogDefinition.getCatalogName().equals(catalogName)) {
                for (int j = 0; j < collectionCatalogDefinition.size(); j++) {
                    CatalogDefinition catalogDefinition = collectionCatalogDefinition.get(j);
                    if(catalogDefinition.getCatalogName().equals(catalogName) && catalogDefinition.getDefinitionName().equals(definitionName)) {
                        collectionCatalogDefinition.remove(catalogDefinition);
                        successful = true;
                    }
                }
                if(collectionCatalogDefinition.size() == 0)
                    this.remove(collectionCatalogDefinition);
            }
        }
        return successful;
    }
}
