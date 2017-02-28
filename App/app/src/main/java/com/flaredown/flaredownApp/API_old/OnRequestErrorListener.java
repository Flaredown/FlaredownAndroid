package com.flaredown.flaredownApp.API_old;

/**
 * Request error listener for the {@link SuperRequest}
 */
public interface OnRequestErrorListener <Throwable> {
    void error(Throwable error);
}
