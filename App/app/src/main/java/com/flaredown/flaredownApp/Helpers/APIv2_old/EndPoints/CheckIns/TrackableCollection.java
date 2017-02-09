package com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.CheckIns;

import com.flaredown.flaredownApp.Helpers.Observers.ObservableHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;

/**
 * Used for storing a collection of unique Trackables.
 */
public class TrackableCollection <T extends Trackable> extends ObservableHashSet<T> implements Serializable {
    // Allows the check in object to be notified if the collection changes (items added/removed or value has changed).
    private transient ObservableHelper<Void> dataChangeObservable = new ObservableHelper<>();

    // Stores a list of subscribers used for monitoring if the trackable value has changed.
    private transient Map<Trackable, Subscriber<String>> valueChangeSubscribers = new HashMap<>();

    /**
     * Run during construction of the class, no matter the constructor (also run when being dematerialized)
     * @return Itself after all the changes have been made.
     */
    @Override
    public Object readResolver() {
        super.readResolver();
        subscribeCollectionObservable(new Subscriber<CollectionChange<T>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(CollectionChange<T> tCollectionChange) {
                dataChangeObservable.notifySubscribers(null);
            }
        });
        return this;
    }

    /**
     * Add a trackable to the collection.
     * @param trackable The trackable to add to the collection.
     * @return True if successful.
     */
    @Override
    public boolean add(T trackable) {
        if(this.contains(trackable)) return false; // Fail safely, but collection cannot have duplicate objects.
        if(trackable.getTrackableId() == null)
            throw new IllegalStateException("Trackable ID cannot be null"); // Trackables cannot have a null id.

        // Track changes
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                dataChangeObservable.notifySubscribers(null);
            }
        };

        trackable.subscribeValueObservable(subscriber);
        valueChangeSubscribers.put(trackable, subscriber);

        return super.add(trackable);
    }

    /**
     * Add multiple trackables to the collection.
     * @param collection The collection of trackables to add to the collection.
     * @return true if all items added correctly.
     */
    @Override
    public boolean addAll(Collection<? extends T> collection) {
        List<T> collect = new ArrayList<>(collection);
        for (T trackable : collect) {
            if(this.contains(trackable) || collect.contains(trackable)) collect.remove(trackable);
            if(trackable.getTrackableId() == null) throw new IllegalStateException("Trackable ID cannot be null");

            // Track changes
            Subscriber<String> subscriber = new Subscriber<String>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(String s) {
                    dataChangeObservable.notifySubscribers(null);
                }
            };

            trackable.subscribeValueObservable(subscriber);
            valueChangeSubscribers.put(trackable, subscriber);
        }
        return super.addAll(collect);
    }

    /**
     * Remove a trackable from the collection.
     * @param object The trackable to remove from the collection.
     * @return True if successful.
     */
    @Override
    public boolean remove(Object object) {
        Subscriber<String> valueChangeSubscriber = valueChangeSubscribers.get(object);
        if(valueChangeSubscriber != null) {
            valueChangeSubscriber.unsubscribe();
            valueChangeSubscribers.remove(object);
        }
        return super.remove(object);
    }

    /**
     * Remove several trackables from the collection.
     * @param collection the trackables to remove from the collection.
     * @return True if successful.
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        for (Object object : collection) {
            Subscriber<String> valueChangeSubscriber = valueChangeSubscribers.get(object);
            if(valueChangeSubscriber != null) {
                valueChangeSubscriber.unsubscribe();
                valueChangeSubscribers.remove(object);
            }
        }
        return super.removeAll(collection);
    }

    /**
     * Returns the observable helper for when the data changes inside an observable. Emits when...
     *      - Trackable value changes.
     *      - Trackable is added/removed.
     * @return Observable helper for monitoring when the data inside the collection is changed.
     */
    public ObservableHelper<Void> getDataChangeObservable() {
        return dataChangeObservable;
    }
}