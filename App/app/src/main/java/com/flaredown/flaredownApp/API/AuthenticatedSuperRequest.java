package com.flaredown.flaredownApp.API;

import android.os.Parcelable;

/**
 * Abstract class for API objects which inject the authentication headers.
 */

public abstract class AuthenticatedSuperRequest<D extends Parcelable, E extends RequestErrorException> extends SuperRequest<D, E> {
    public AuthenticatedSuperRequest(RequestMethod requestMethod, String url) {
        super(requestMethod, url);
    }
    // TODO override the getHeaders method and add authentication headers.
}
