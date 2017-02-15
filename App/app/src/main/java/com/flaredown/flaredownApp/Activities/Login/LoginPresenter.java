package com.flaredown.flaredownApp.Activities.Login;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.flaredown.flaredownApp.API.OnRequestErrorListener;
import com.flaredown.flaredownApp.API.OnRequestSuccessListener;
import com.flaredown.flaredownApp.API.Requests.UserSignIn;
import com.flaredown.flaredownApp.API.ResponseModel.Sessions;
import com.flaredown.flaredownApp.Activities.Main.MainActivity;
import com.flaredown.flaredownApp.Activities.Register.RegisterActivity;
import com.flaredown.flaredownApp.FlaredownApplication;
import com.flaredown.flaredownApp.Helpers.APIv2_old.APIResponse;
import com.flaredown.flaredownApp.Helpers.APIv2_old.Communicate;
import com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.Session.Session;
import com.flaredown.flaredownApp.Helpers.APIv2_old.Error;
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
        doSplashScreen(null);
    }

    public void doSplashScreen(Runnable doAfter) {
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

    public void doLogin(String email, String password) {
        if(isViewAttached()) {
            getView().clearFields();
            getView().showLoading(true);

            List<String> emailErrors = new LinkedList<>();
            List<String> passwordErrors = new LinkedList<>();
            List<String> errors = new LinkedList<>();

            // Warn against empty email field.
            if(email == null || "".equals(email)) {
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

            // Submit login request.
            new UserSignIn(email, password) {{
                setOnRequestSuccessListener(new OnRequestSuccessListener<Sessions>() {
                    @Override
                    public void success(Sessions object) {
                        object.storeSession(getActivity(), true);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                getActivity().startActivity(intent);
                                // Ensure no animation occurs.
                                getActivity().overridePendingTransition(0, 0);
                                getActivity().finish();
                            }
                        });
                    }
                });

                setOnRequestErrorListener(new OnRequestErrorListener<Throwable>() {
                    @Override
                    public void error(final Throwable error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(error instanceof UserSignIn.InvalidCredentialsException)
                                    LoginPresenter.this.onError(new UserFriendlyThrowable(getActivity().getString(R.string.locales_nice_errors_bad_credentials)));
                                else
                                    LoginPresenter.this.onError(error);
                                getView().hideLoading();
                            }
                        });
                    }
                });

                start(getActivity());
            }};
        }
    }

    public void doOpenRegisterForm() {
        if(isViewAttached()) {
            getView().showSplashScreen();

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
                            getView().showContent();
                            RegisterActivity.startActivity(getActivity());
                        }
                    });
                }
            }).start();
        }
    }

    public void doOpenForgotPassword() {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(FlaredownApplication.getStringResource(R.string.password_reset_website)));
        getActivity().startActivity(intent); // TODO, Implement native or make local web wrapper.
    }


    public void onError(Throwable throwable) {
        if(isViewAttached()) {
            getView().showError(throwable, true);
        }
    }
}
