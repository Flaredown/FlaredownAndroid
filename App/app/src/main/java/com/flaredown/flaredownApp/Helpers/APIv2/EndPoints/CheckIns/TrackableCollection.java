package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import com.flaredown.flaredownApp.Helpers.Observers.ObservableHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.functions.Action1;

/**
 * Used for storing a collection of unique Trackables.
 */
public class TrackableCollection <T extends Trackable> extends ObservableHashSet<T> {
    // Allows the check in object to be notified if the collection changes (items added/removed or value has changed).
    private ObservableHelper<Void> dataChangeObservable = new ObservableHelper<>();

    // Stores a list of subscribers used for monitoring if the trackable value has changed.
    private Map<Trackable, Subscriber<String>> valueChangeSubscribers = new HashMap<>();

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
        if(this.contains(trackable)) throw new IllegalStateException("Collection already has a Trackable with this id (" + trackable.getTrackableId() + ")."); // Force unique trackables.
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
        for (T trackable : collection) {
            if(this.contains(trackable) || collection.contains(trackable)) throw new IllegalStateException("Collection already has a Trackable with this id(" + trackable.getTrackableId() + ").");
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
        return super.addAll(collection);
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
