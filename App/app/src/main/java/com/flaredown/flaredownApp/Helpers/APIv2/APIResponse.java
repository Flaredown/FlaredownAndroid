package com.flaredown.flaredownApp.Helpers.APIv2;

/**
 * Used to get the response from the Communicate class.
 * @param <SuccessType> On Success return object type.
 * @param <FailureType> On Failure return object type.
 */
public interface APIResponse<SuccessType, FailureType> {
    public void onSuccess(SuccessType result);
    public void onFailure(FailureType result);
}
