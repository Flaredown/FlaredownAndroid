package com.flaredown.flaredownApp.Helpers.Observers;

import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Tracks the value of a immutable variable using observers. When the value is changed with the
 * setValue method, the observer will notify subscribers.
 *
 * Note:: Mutable types can change without being notified.
 */
public class ImmutableObserver <T> {
    private T object;
    private transient List<OnChangeListener<T>> changeHandlers;
    private transient Observable<T> observable;

    private Object readResolver() {
        this.changeHandlers = new LinkedList<>();
        this.observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                changeHandlers.add(new OnChangeListener<T>() {
                    @Override
                    public void onChange(T object) {
                        if(subscriber.isUnsubscribed())
                            return;
                        subscriber.onNext(object);
                    }
                });
            }
        });

        return this;
    }

    // Constructors.
    public ImmutableObserver() {
        this.readResolver();
    }

    /**
     * Set the initial value of the observer.
     * @param object Object should be immutable, any changes to a mutable variable will not be notified.
     */
    public ImmutableObserver(T object) {
        this.readResolver();
        this.object = object;
    }


    private interface OnChangeListener<T> {
        void onChange(T object);
    }

    /**
     * Get the observable object for this object.
     * @return The observable which notifies on object change.
     */
    public Observable<T> getObservable() {
        return observable;
    }

    /**
     * Get the value of the object which is being observed.
     * @return The value of the object which is being observed.
     */
    public T getValue() {
        return object;
    }

    /**
     * Set the value of the object, which will notify any subscribed observers.
     * @param object The new value.
     */
    public void setValue(T object) {
        this.object = object;
        for (OnChangeListener<T> changeHandler : changeHandlers) {
            changeHandler.onChange(object);
        }
    }
}
