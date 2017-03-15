package com.flaredown.flaredownApp.API.Sync;

/**
 *
 */

public abstract class Endpoint<M extends ServerModel, U extends ServerUpdate> {
    private String endpoint;
    private RequestMethod requestMethod;

    public Endpoint() {
        construct();
    }

    /**
     * Method to be called in all constructor methods.
     */
    private void construct() {
        this.endpoint = getEndpoint();
        this.requestMethod = getRequestMethod();
    }


    /**
     * Get the endpoint for this object.
     * @return The endpoint for this object.
     */
    protected abstract String getEndpoint();

    /**
     * Get the request method for this object.
     * @return The request method for this object.
     */
    protected abstract RequestMethod getRequestMethod();
}
