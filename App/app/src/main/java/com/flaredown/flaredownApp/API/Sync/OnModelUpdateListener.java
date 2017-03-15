package com.flaredown.flaredownApp.API.Sync;

/**
 * Interface for listening when the model is updated. An update can occur multiple for a single
 * request call. Once from the cache, and another from the web.
 *
 * If there is no internet connectivity the {@link #onUpdate(DataSource, ServerModel)} may be called 0-1 times. If there
 * is cached data the method will be called other wise no method call.
 */

public interface OnModelUpdateListener<M extends ServerModel> {
    void onUpdate(DataSource dataSource, M model);
}
