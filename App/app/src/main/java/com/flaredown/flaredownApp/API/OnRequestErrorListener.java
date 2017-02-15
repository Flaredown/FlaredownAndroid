package com.flaredown.flaredownApp.API;

/**
 * Request error listener for the {@link SuperRequest}
 */
public interface OnRequestErrorListener <Throwable> {
    void error(Throwable error);
}
