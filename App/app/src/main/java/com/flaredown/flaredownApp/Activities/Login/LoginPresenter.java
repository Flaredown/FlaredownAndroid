package com.flaredown.flaredownApp.Activities.Login;

import com.flaredown.flaredownApp.FlaredownApplication;
import com.flaredown.flaredownApp.Helpers.UserFriendlyThrowable;
import com.flaredown.flaredownApp.Helpers.Wrappers.Mosby.PresenterWrapper;
import com.flaredown.flaredownApp.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Login Presenter for the {@link LoginFragment}
 */

public class LoginPresenter extends PresenterWrapper<LoginView, LoginModel> {

    public void doSplashScreen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isViewAttached())
                            getView().showContent();
                    }
                });
            }
        }).start();
    }

    public void doLogin(String username, String password) {
        if(isViewAttached()) {
            getView().clearFields();
            getView().showLoading(true);

            List<String> emailErrors = new LinkedList<>();
            List<String> passwordErrors = new LinkedList<>();
            List<String> errors = new LinkedList<>();

            // Warn against empty email field.
            if(username == null || "".equals(username)) {
                emailErrors.add(String.format(FlaredownApplication.getStringResource(R.string.warning_field_empty), FlaredownApplication.getStringResource(R.string.field_email)));
                errors.add(FlaredownApplication.getStringResource(R.string.warning_generic_empty_field));
            }

            // Warn against empty password field.
            if(password == null || "".equals(password)) {
                passwordErrors.add(String.format(FlaredownApplication.getStringResource(R.string.warning_field_empty), FlaredownApplication.getStringResource(R.string.field_password)));
                // Check that not already warned.
                if(errors.size() <= 0)
                    errors.add(FlaredownApplication.getStringResource(R.string.warning_generic_empty_field));
            }

            // Basic validation failed... show errors.
            if(errors.size() > 0) {
                String sEmailErrors  = "";
                for (String emailError : emailErrors) {
                    sEmailErrors += ("".equals(sEmailErrors)? "" : "\n") + emailError;
                }

                String sPasswordErrors = "";
                for (String passwordError : passwordErrors) {
                    sPasswordErrors += ("".equals(sPasswordErrors) ? "" : "\n" ) + passwordError;
                }

                String sErrors = "";
                for (String error : errors) {
                    sErrors += ("".equals(sErrors) ? "" : "\n") + error;
                }

                // Wait a bit to allow the animation to complete.
                final String finalSEmailErrors = sEmailErrors;
                final String finalSPasswordErrors = sPasswordErrors;
                final String finalSErrors = sErrors;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getView().hideLoading();
                                onError(new UserFriendlyThrowable(finalSErrors));
                                getView().setFieldErrorMessages(finalSEmailErrors, finalSPasswordErrors);
                            }
                        });
                    }
                }).start();
                return; // No longer needs to continue.
            }




            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isViewAttached()) {
                                getView().hideLoading();
                            }
                        }
                    });
                }
            }).start();
        }
    }


    public void onError(Throwable throwable) {
        if(isViewAttached()) {
            getView().showError(throwable, true);
        }
    }
}
