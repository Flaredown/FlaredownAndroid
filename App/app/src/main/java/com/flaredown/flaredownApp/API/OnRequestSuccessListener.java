package com.flaredown.flaredownApp.API;

import android.os.Parcelable;

/**
 * Request Success interface for {@link SuperRequest}.
 * @param <D> The object type the request returns.
 */
public interface OnRequestSuccessListener<D extends Parcelable> {
    void onSucdess(D object);
}
