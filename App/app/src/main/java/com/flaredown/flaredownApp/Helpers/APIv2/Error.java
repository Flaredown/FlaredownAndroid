package com.flaredown.flaredownApp.Helpers.APIv2;

import android.support.annotation.Nullable;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Returned from the Communicate class when an error occurs when fetching data from the api.
 */
public class Error {
    @Nullable
    private VolleyError volleyError;
    private boolean responseGiven = true; // Assuming true.
    private boolean internetConnection = true; // Assuming true.
    private Exception exceptionThrown = null;
    private int statusCode = 500;
    private String debugString = ""; // Extra detail to the error.
    @Nullable
    private Runnable retryRunnable;

    public Error() {
    }

    /**
     * Construct an Error object passing a volley error.
     * @param volleyError
     */
    public Error(VolleyError volleyError) {
        this.setVolleyError(volleyError);
    }

    /**
     * Get the error information (if any) which is returned by volley.
     * @return The volley error object.
     */
    @Nullable
    public VolleyError getVolleyError() {
        return volleyError;
    }

    /**
     * Set the volley error, also updates the status code and internet connection.
     * @param volleyError The error object returned by volley
     */
    public void setVolleyError(VolleyError volleyError) {
        this.volleyError = volleyError;
        try {
            this.statusCode = volleyError.networkResponse.statusCode;
            if(this.statusCode == 503)
                setIsResponseGiven(false);
        } catch (NullPointerException e) {
            this.statusCode = 503;
            setIsResponseGiven(false);
        }
    }

    public Exception getExceptionThrown() {
        return exceptionThrown;
    }

    public Error setExceptionThrown(Exception e) {
        this.exceptionThrown = e;
        this.statusCode = 500;

        return this;
    }

    /**
     * Get a list of error messages which could (not a guarantee) be returned by the API.
     * @return A list of error messages.
     */
    public ArrayList<String> getErrorList() {
        ArrayList<String> output = new ArrayList<>();

        try {
            String responseString = new String(volleyError.networkResponse.data);
            JSONObject responseObject = new JSONObject(responseString);
            JSONArray errorJArray = responseObject.getJSONArray("errors");
            for (int i = 0; i < errorJArray.length(); i++) {
                output.add(errorJArray.getString(i));
            }
        } catch (Exception e) {e.printStackTrace();}

        return output;
    }

    /**
     * Was the device connected to the internet at api call.
     * @return true if the device had an internet connection at api call.
     */
    public boolean isResponseGiven() {
        return responseGiven;
    }

    /**
     * Set if there is an internet connection.
     * @param responseGiven True if there is an internet connection.
     */
    public void setIsResponseGiven(boolean responseGiven) {
        this.responseGiven = responseGiven;
    }

    /**
     * Get the HTTP Error status code.
     * @return The HTTP Error status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Set the HTTP Error status code.
     * @param statusCode The HTTP Error status code.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Get the debug string, which is an extra snippet of information passed to help debug an error.
     * @return Extra snippet of information to help debug en error.
     */
    public String getDebugString() {
        return debugString;
    }

    /**
     * SEt the debug string, which is an extra snippet of information passed to help debug an error.
     * @param debugString Extra snippet of information to help debug an error.
     */
    public Error setDebugString(String debugString) {
        this.debugString = debugString;
        return this;
    }

    /**
     * If available the runnable which can be run to retry the api request.
     * @return If available the runnable which can be run to retry the api request.
     */
    @Nullable
    public Runnable getRetryRunnable() {
        return retryRunnable;
    }

    /**
     * Set the retry runnable, the runnable which can be run to retry the api request.
     * @param retryRunnable A runnable which can be run to retry the api request.
     */
    public Error setRetryRunnable(Runnable retryRunnable) {
        this.retryRunnable = retryRunnable;
        return this;
    }
}
