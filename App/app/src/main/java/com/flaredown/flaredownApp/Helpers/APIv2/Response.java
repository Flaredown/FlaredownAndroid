package com.flaredown.flaredownApp.Helpers.APIv2;

/**
 * Used to get the response from the Communicate class.
 * @param <T> On Success return object type.
 * @param <V> On Failure return object type.
 */
public interface Response<T,V> {
    public void onSuccess(T result);
    public void onFailure(V result);
}
