package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import java.util.HashSet;

/**
 * Used for storing a collection of unique tags.
 */
public class TagCollection <T extends Tag> extends HashSet<T> {
    @Override
    public boolean add(T object) {
        if(this.contains(object)) new IllegalStateException("Tag ID already exists in collection (" + object.getId() + ")"); // Force unique tags only.
        if(object == null) throw new IllegalStateException("Tag ID cannot be null."); // Tags cannot have a null id.
        return super.add(object);
    }
}
