package com.flaredown.flaredownApp.FlareDown;

import com.android.volley.VolleyError;

/**
 * Object is returned if the API request failed. Use the default error class to handle the error.
 */
public class API_Error {
    public VolleyError volleyError;
    public Boolean internetConnection;
    public int statusCode = 500;
    private Runnable retryRunnable = null;

    /**
     * Automatically sets status code, internet connection fields from a VolleyError object.
     * @param volleyError Passed from an OnApiResponse<>.onFailure();
     * @return returns itself for easy concatenation.
     */
    public API_Error setVolleyError(VolleyError volleyError) {
        this.volleyError = volleyError;
        //if(volleyError.networkResponse.statusCode != null)
        try {
            this.statusCode = volleyError.networkResponse.statusCode;
            if(this.statusCode == 503) {
                this.internetConnection = false;
            }
        } catch (NullPointerException e) {
            this.statusCode = 503;
            this.internetConnection = false;
        }
        return this;
    }

    /**
     * Change the ok button to retry, when clicked runnable will run.
     * @param runnable retry action.
     * @return Itself.
     */
    public API_Error setRetry(Runnable runnable) {
        retryRunnable = runnable;
        return this;
    }

    /**
     * Returns the retry runnable.
     * @return Retry runnable.
     */
    public Runnable getRetry() {
        return retryRunnable;
    }

    /**
     * Set the status code of the error.
     * @param statusCode the HTTP status code.
     * @return returns itself for easy concatenation.
     */
    public API_Error setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * Set if the error was caused by no internet connection.
     * @param internetConnection is the error caused by no internet connection.
     * @return returns itself for easy concatentation.
     */
    public API_Error setInternetConnection(boolean internetConnection) {
        if(!internetConnection)
            statusCode = 503;
        if(statusCode == 503)
            this.internetConnection = false;
        else
            this.internetConnection = internetConnection;
        return this;
    }

    @Override
    public String toString() {
        return "Internet Connection: " + (internetConnection ? "true" : "false") + " Status Code: " + String.valueOf(statusCode);
    }
}
