package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import com.flaredown.flaredownApp.Helpers.Observers.ObservableHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Hash set which is observable, the observable emits when an item is added/removed from the collection.
 */
public class ObservableHashSet <T> extends HashSet<T> {
    private transient ObservableHelper<CollectionChange<T>> collectionObservable = new ObservableHelper<>();

    public ObservableHashSet() {
        readResolver();
    }

    public ObservableHashSet(int capacity) {
        super(capacity);
        readResolver();
    }

    public ObservableHashSet(int capacity, float loadFactor) {
        super(capacity, loadFactor);
        readResolver();
    }

    public ObservableHashSet(Collection<? extends T> collection) {
        super(collection);
        readResolver();
    }

    /**
     * Assigns all transient fields after deserialization (and also method call, which is done in the constructors).
     * @return Itself.
     */
    public Object readResolver() {
        return this;
    }

    /**
     * Subscribes to the collection observable, which emits on addition or removing of items.
     * @param subscriber The subscriber in which to subscribe to the collection.
     */
    public void subscribeCollectionObservable(Subscriber<CollectionChange<T>> subscriber) {
        collectionObservable.subscribe(subscriber);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        for (T t : collection) {
            if(!this.contains(t)) {
                triggerCollectionChange(new CollectionChange(t, ChangeType.ADD));
            }
        }
        boolean result = super.addAll(collection);
        return result;
    }

    @Override
    public boolean add(T object) {
        boolean result = super.add(object);
        if(result)
            triggerCollectionChange(new CollectionChange(object, ChangeType.ADD));
        return result;
    }

    @Override
    public boolean remove(Object object) {
        boolean result = super.remove(object);
        try {
            if(result)
                triggerCollectionChange(new CollectionChange((T) object, ChangeType.REMOVE));
        } catch (ClassCastException e) {}
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean result = super.removeAll(collection);
        for (Object o : collection) {
            try {
                triggerCollectionChange(new CollectionChange((T) o, ChangeType.REMOVE)); // Notify observer of changes.
            } catch (ClassCastException e) {}
        }
        return result;
    }

    @Override
    public void clear() {
        for (T t : this) {
            triggerCollectionChange(new CollectionChange(t, ChangeType.REMOVE));
        }
        super.clear();
    }

    private void triggerCollectionChange(CollectionChange<T> collectionChange) {
        collectionObservable.notifySubscribers(collectionChange);
    }

    /**
     * Used to emit the changes made to the collection (add/remove) and the item that has been changed.
     * @param <T> The object type the change is about.
     */
    public static class CollectionChange<T> {
        private T object;
        private ChangeType changeType;

        public CollectionChange(T object, ChangeType changeType) {
            this.object = object;
            this.changeType = changeType;
        }

        public T getObject() {
            return object;
        }

        public ChangeType getChangeType() {
            return changeType;
        }
    }

    /**
     * The type of change.
     */
    public enum ChangeType {
        REMOVE, ADD
    }

    private interface OnCollectionChangeListener {
        void CollectionChange(CollectionChange collectionChange);
    }
}
