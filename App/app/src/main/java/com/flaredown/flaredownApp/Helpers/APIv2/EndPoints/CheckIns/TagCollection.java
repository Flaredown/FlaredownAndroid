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
public class TagCollection <T extends Tag> extends HashSet<T> {

    private transient List<OnCollectionChangeListener> collectionChangeHandler;
    private transient Observable<CollectionChange>  collectionObservable;

    public TagCollection() {
        readResolver();
    }

    public TagCollection(int capacity) {
        super(capacity);
        readResolver();
    }

    public TagCollection(int capacity, float loadFactor) {
        super(capacity, loadFactor);
        readResolver();
    }

    public TagCollection(Collection<? extends T> collection) {
        super(collection);
        readResolver();
    }

    /**
     * Assigns all transient fields after deserialization (and also method call, which is done in the constructors).
     * @return Itself.
     */
    public Object readResolver() {
        this.collectionChangeHandler = new LinkedList<>();
        this.collectionObservable = Observable.create(new Observable.OnSubscribe<CollectionChange>() {
            @Override
            public void call(final Subscriber<? super CollectionChange> subscriber) {
                collectionChangeHandler.add(new OnCollectionChangeListener() {
                    @Override
                    public void CollectionChange(CollectionChange collectionChange) {
                        if(subscriber.isUnsubscribed())
                            return;
                        subscriber.onNext(collectionChange);
                    }
                });
            }
        });
        return this;
    }

    public Observable<CollectionChange> getCollectionObservable() {
        return collectionObservable;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        for (T t : collection) {
            if(this.contains(t)) throw new IllegalStateException("Tad ID already exists in collection, addAll canceled.");
            if(t == null) throw new IllegalStateException("Tad ID cannot be null");
        }


        boolean result = super.addAll(collection);
        for (T t : collection) {
            triggerCollectionChange(new CollectionChange(t, ChangeType.ADD));
        }
        return result;
    }

    @Override
    public boolean add(T object) {
        if(this.contains(object)) new IllegalStateException("Tag ID already exists in collection (" + object.getId() + ")"); // Force unique tags only.
        if(object == null) throw new IllegalStateException("Tag ID cannot be null."); // Tags cannot have a null id.
        boolean result = super.add(object);
        triggerCollectionChange(new CollectionChange(object, ChangeType.ADD));
        return result;
    }

    @Override
    public boolean remove(Object object) {
        boolean result = super.remove(object);
        if(object instanceof Tag)
            triggerCollectionChange(new CollectionChange((Tag) object, ChangeType.REMOVE));
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean result = super.removeAll(collection);
        for (Object o : collection) {
            if(o instanceof Tag)
                triggerCollectionChange(new CollectionChange((Tag) o, ChangeType.REMOVE));
        }
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        for (T t : this) {
            triggerCollectionChange(new CollectionChange(t, ChangeType.REMOVE));
        }
    }

    private void triggerCollectionChange(CollectionChange collectionChange) {
        for (OnCollectionChangeListener onCollectionChangeListener : collectionChangeHandler) {
            onCollectionChangeListener.CollectionChange(collectionChange);
        }
    }

    public static class CollectionChange <T extends Tag> {
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

    public enum ChangeType {
        REMOVE, ADD
    }

    private interface OnCollectionChangeListener {
        void CollectionChange(CollectionChange collectionChange);
    }
}
