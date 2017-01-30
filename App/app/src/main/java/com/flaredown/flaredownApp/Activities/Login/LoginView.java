package com.flaredown.flaredownApp.Activities.Login;

import com.flaredown.flaredownApp.Helpers.Wrappers.Mosby.ViewWrapper;

/**
 * The view interface for the {@link LoginFragment}
 */

public interface LoginView extends ViewWrapper<LoginModel> {

    public void showSplashScreen();

    void hideLoading();

    void setFieldErrorMessages(String email, String password);

    /**
     * Clear the login field error messages and password fields. (Email field is left alone).
     */
    void clearFields();

    void clearError();
}
