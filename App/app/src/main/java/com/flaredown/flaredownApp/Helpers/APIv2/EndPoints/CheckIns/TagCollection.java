package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import java.io.Serializable;
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
public class TagCollection <T extends Tag> extends ObservableHashSet<T> implements Serializable {
    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return super.addAll(collection);
    }

    @Override
    public boolean add(T object) {
        return super.add(object);
    }
}
