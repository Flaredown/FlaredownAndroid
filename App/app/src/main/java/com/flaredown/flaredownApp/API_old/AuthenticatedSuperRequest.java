package com.flaredown.flaredownApp.API_old;

import android.os.Parcelable;

/**
 * Abstract class for API objects which inject the authentication headers.
 */

public abstract class AuthenticatedSuperRequest<D extends Parcelable> extends SuperRequest<D> {
    public AuthenticatedSuperRequest(RequestMethod requestMethod, String url) {
        super(requestMethod, url);
    }
    // TODO override the getHeaders method and add authentication headers.
}
