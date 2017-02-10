package com.flaredown.flaredownApp.API;

import android.os.Parcelable;

import com.flaredown.flaredownApp.Helpers.Volley.WebAttributes;

/**
 * Abstract class for API request objects.
 */
public abstract class SuperRequest<D extends Parcelable, E extends RequestErrorException> {

    private RequestMethod requestMethod;
    private String url;

    private OnRequestSuccessListener<D> onRequestSuccessListener;
    private OnRequestErrorListener<E> onRequestErrorListener;

    public SuperRequest(RequestMethod requestMethod, String url) {
        this.requestMethod = requestMethod;
    }

    /**
     * Method called on successful request.
     * @param data The data received.
     */
    protected abstract void onRequestSuccess(String data);

    /**
     * Method called on request error.
     * @param object
     */
    protected abstract void onRequestError(String object); // TODO pass correct object.

    /**
     * Called to retrieve the headers for a request.
     * @return The web attributes for the header params.
     */
    protected WebAttributes getHeaders() {
        return new WebAttributes();
    }

    /**
     * Called to retrieve the post parameters for a request.
     * @return The web attributes for the post params.
     */
    protected WebAttributes getPostParams() {
        return new WebAttributes();
    }

    /**
     * Called to retrieve the get parameters for a request.
     * @return The web attributes for the get params.
     */
    protected WebAttributes getGetParams() {
        return new WebAttributes();
    }

    /**
     * Call to retrieve the request body for a request.
     * @return The request body, default: Null.
     */
    protected String getRequestBody() {
        return null;
    }

    /**
     * If a {@link OnRequestSuccessListener} has been set call it passing the {@param object}
     */
    protected void triggerOnRequestSuccessListener(D object) {
        if(onRequestSuccessListener != null)
            onRequestSuccessListener.onSucdess(object);
    }

    /**
     * If a {@link OnRequestErrorListener} has been set call it passing the {@param errorException}
     */
    protected void triggerOnRequestErrorListenr(E errorException) {
        if(onRequestErrorListener != null)
            onRequestErrorListener.onError(errorException);
    }

    // *** Setters and getters ***

    public void setOnRequestSuccessListener(OnRequestSuccessListener<D> onRequestSuccessListener) {
        this.onRequestSuccessListener = onRequestSuccessListener;
    }

    public void setOnRequestErrorListener(OnRequestErrorListener<E> onRequestErrorListener) {
        this.onRequestErrorListener = onRequestErrorListener;
    }
}
