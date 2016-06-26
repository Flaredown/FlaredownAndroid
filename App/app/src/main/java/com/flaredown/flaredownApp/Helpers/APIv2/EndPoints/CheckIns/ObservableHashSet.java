package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by thunter on 25/06/16.
 */
public class ObservableHashSet <T> extends HashSet<T> {
    private transient List<OnCollectionChangeListener> collectionChangeHandler;
    private transient Observable<CollectionChange> collectionObservable;

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
                triggerCollectionChange(new CollectionChange((T) o, ChangeType.REMOVE));
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

    private void triggerCollectionChange(CollectionChange collectionChange) {
        for (OnCollectionChangeListener onCollectionChangeListener : collectionChangeHandler) {
            onCollectionChangeListener.CollectionChange(collectionChange);
        }
    }

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

    public enum ChangeType {
        REMOVE, ADD
    }

    private interface OnCollectionChangeListener {
        void CollectionChange(CollectionChange collectionChange);
    }
}
