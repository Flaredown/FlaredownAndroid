package com.flaredown.flaredownApp.API.Sync;

/**
 * Used for handling errors when fetching an endpoint.
 */

public interface OnErrorListener {
    /**
     * Used to handle an error when fetching an endpoint.
     * @param throwable The throwable error what occurred.
     * @param cachedCalled If true {@link OnModelUpdateListener#onUpdate(DataSource, ServerModel)}
     *                     has been called with a cached version.
     */
    void onError(Throwable throwable, boolean cachedCalled);
}
