package com.flaredown.flaredownApp.API;

/**
 * Request error listener for the {@link SuperRequest}
 */
public interface OnRequestErrorListener <E extends RequestErrorException> {
    void onError(E error);
}
