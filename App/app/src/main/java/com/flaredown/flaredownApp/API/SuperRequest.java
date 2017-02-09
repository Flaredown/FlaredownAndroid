package com.flaredown.flaredownApp.API;

import android.os.Parcelable;

/**
 * Abstract class for API request objects.
 */
public abstract class SuperRequest<D extends Parcelable, E extends RequestErrorException> {

    private RequestMethod requestMethod;



    public SuperRequest(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }






    private OnRequestSuccessListener<D> onRequestSuccessListener;

    private OnRequestErrorListener<E> onRequestErrorListener;












    // *** Setters and getters ***

    public OnRequestSuccessListener<D> getOnRequestSuccessListener() {
        return onRequestSuccessListener;
    }

    public void setOnRequestSuccessListener(OnRequestSuccessListener<D> onRequestSuccessListener) {
        this.onRequestSuccessListener = onRequestSuccessListener;
    }

    public OnRequestErrorListener<E> getOnRequestErrorListener() {
        return onRequestErrorListener;
    }

    public void setOnRequestErrorListener(OnRequestErrorListener<E> onRequestErrorListener) {
        this.onRequestErrorListener = onRequestErrorListener;
    }
}
