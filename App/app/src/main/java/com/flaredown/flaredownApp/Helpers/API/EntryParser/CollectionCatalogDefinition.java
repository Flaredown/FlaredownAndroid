package com.flaredown.flaredownApp.Helpers.API.EntryParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by thunter on 19/02/16.
 */
public class CollectionCatalogDefinition extends ArrayList<CatalogDefinition> {
    String catalogName = "";

    /**
     * Default constructor for the CollectionCatalogDefinition.
     * @param catalogName The name of the catalog the collection is for.
     */
    public CollectionCatalogDefinition(String catalogName) {
        this.catalogName = catalogName;
    }

    /**
     * Constructs the CollectionCatalogDefinition object from a JSON array of catalog definitions.
     * @param collectionCatalogDefinitionJArray JSON array of catalog definitions.
     * @throws JSONException If cannot be parsed JSONException will be thrown.
     */
    public CollectionCatalogDefinition(JSONArray collectionCatalogDefinitionJArray, String catalogName) throws JSONException{
        this.catalogName = catalogName;
        for(int i = 0; i < collectionCatalogDefinitionJArray.length(); i++) {
            JSONObject catalogDefinitionJObject = collectionCatalogDefinitionJArray.getJSONObject(i);
            this.add(new CatalogDefinition(catalogDefinitionJObject, this.catalogName));
        }
    }

    /**
     * Adds catalog definition to CollectionCatalogDefinition.
     * Depreacted use .add instead.
     * @param catalogDefinition The catalog definition to add to collection.
     * @return always true.
     */
    @Deprecated
    public boolean addCatalogDefinition(CatalogDefinition catalogDefinition) {
        return this.add(catalogDefinition);
    }

    public JSONArray toJSONArray() throws JSONException{
        JSONArray returnJArray = new JSONArray();
        for (CatalogDefinition catalogDefinition : this) {
            returnJArray.put(catalogDefinition.toJSONObject());
        }
        return returnJArray;
    }

    /**
     * Get the catalog name for this collection.
     * @return catalog name string.
     */
    public String getCatalogName() {
        return catalogName;
    }
}
