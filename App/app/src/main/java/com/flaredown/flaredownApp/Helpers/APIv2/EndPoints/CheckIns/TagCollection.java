package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;

/**
 * Used for storing a collection of unique tags.
 */
public class TagCollection <T extends Tag> extends ObservableHashSet<T> {
    @Override
    public boolean addAll(Collection<? extends T> collection) {
        for (T object : collection) {
            if(this.contains(object) || collection.contains(object)) throw new IllegalStateException("Tag ID already exists in collection (" + object.getId() + ")");
            if(object.getId() == null) throw new IllegalStateException("Tag ID cannot equal null");
        }
        return super.addAll(collection);
    }

    @Override
    public boolean add(T object) {
        if(this.contains(object)) throw new IllegalStateException("Tag ID already exists in collection (" + object.getId() + ")");
        if(object.getId() == null) throw new IllegalStateException("Tag ID cannot equal null");
        return super.add(object);
    }
}
