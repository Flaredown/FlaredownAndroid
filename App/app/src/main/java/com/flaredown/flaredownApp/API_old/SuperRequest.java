package com.flaredown.flaredownApp.API_old;

import android.content.Context;
import android.os.Parcelable;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.flaredown.flaredownApp.Helpers.Volley.QueueProvider;
import com.flaredown.flaredownApp.Helpers.Volley.VolleyRequestWrapper;
import com.flaredown.flaredownApp.Helpers.Volley.WebAttributes;

/**
 * Abstract class for API request objects.
 */
public abstract class SuperRequest<D extends Parcelable> {

    private RequestMethod requestMethod;
    private boolean hasRequestRun = false;
    private String endpoint;

    private OnRequestSuccessListener<D> onRequestSuccessListener;
    private OnRequestErrorListener<Throwable> onRequestErrorListener;

    public SuperRequest(RequestMethod requestMethod, String endpoint) {
        this.requestMethod = requestMethod;
        this.endpoint = endpoint;
    }

    public final void start(Context context) {
        if(hasRequestRun) {
            fail(new IllegalStateException("Request has already been started, a request object " +
                    "should only be started once."));
            return;
        }

        VolleyRequestWrapper volleyRequestWrapper = new VolleyRequestWrapper(this.requestMethod.getVolleyMethod(), EndPointUrl.getAPIUrl(this.endpoint, getGetParams()), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                success(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fail(error);
            }
        }) {{
            setHeaders(SuperRequest.this.getHeaders());
            setPostParams(SuperRequest.this.getPostParams());
        }};

        QueueProvider.getQueue(context).add(volleyRequestWrapper);

        hasRequestRun = true;
    }

    /**
     * Called on successful request, calls {@link #triggerOnRequestSuccessListener(Parcelable)}
     * converting the received data from a String to {@link D}.
     * @param data
     */
    private void success(String data) {
        try {
            triggerOnRequestSuccessListener(onRequestSuccess(data));
        } catch (Throwable throwable) {
            fail(throwable);
        }
    }

    /**
     * Called on error, calls {@link #triggerOnRequestErrorListener(Throwable)}
     * @param object
     */
    private void fail(Throwable object){
        triggerOnRequestErrorListener(onRequestError(object));
    }

    /**
     * Method called on successful request, used to convert the returned string into a object before event trigger.
     * @param data The data received.
     */
    protected abstract D onRequestSuccess(String data) throws Throwable;

    /**
     * Method called on request error, used for processing errors before event trigger.
     * @param object
     */
    protected Throwable onRequestError(Throwable object) {
        return object;
    };

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
    private void triggerOnRequestSuccessListener(D object) {
        if(onRequestSuccessListener != null)
            onRequestSuccessListener.success(object);
    }

    /**
     * If a {@link OnRequestErrorListener} has been set call it passing the {@param errorException}
     */
    private void triggerOnRequestErrorListener(Throwable errorException) {
        if(onRequestErrorListener != null)
            onRequestErrorListener.error(errorException);
    }

    // *** Setters and getters ***

    public final void setOnRequestSuccessListener(OnRequestSuccessListener<D> onRequestSuccessListener) {
        this.onRequestSuccessListener = onRequestSuccessListener;
    }

    public final void setOnRequestErrorListener(OnRequestErrorListener<Throwable> onRequestErrorListener) {
        this.onRequestErrorListener = onRequestErrorListener;
    }
}
