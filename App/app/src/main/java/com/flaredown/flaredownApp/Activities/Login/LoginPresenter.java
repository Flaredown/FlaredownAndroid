package com.flaredown.flaredownApp.Activities.Login;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.flaredown.flaredownApp.API.Endpoints.Session.SessionEndpoint;
import com.flaredown.flaredownApp.API.Endpoints.Session.SessionLogin;
import com.flaredown.flaredownApp.API.Endpoints.Session.SessionModel;
import com.flaredown.flaredownApp.API.Sync.DataSource;
import com.flaredown.flaredownApp.API.Sync.OnErrorListener;
import com.flaredown.flaredownApp.API.Sync.OnModelUpdateListener;
import com.flaredown.flaredownApp.API.Sync.ServerModel;
import com.flaredown.flaredownApp.API.Sync.UpdateManager;
import com.flaredown.flaredownApp.Activities.Main.MainActivity;
import com.flaredown.flaredownApp.Activities.Register.RegisterActivity;
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

            new SessionEndpoint().sendRequest(new SessionLogin(email, password), new UpdateManager<SessionModel>() {{
                setErrorListener(new OnErrorListener() {
                    @Override
                    public void onError(Throwable throwable, boolean cachedCalled) {
                        if(throwable instanceof VolleyError) {
                            final VolleyError error = (VolleyError) throwable;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(isViewAttached()) {
                                        getView().showError(error, true);
                                    }
                                }
                            });
                        }
                    }
                });
                setModelUpdateListener(new OnModelUpdateListener<SessionModel>() {
                    @Override
                    public void onUpdate(DataSource dataSource, SessionModel model) {
                        MainActivity.startActivityNoHistoryNoAnimation(getActivity());
                        model.saveToSharedPreferences(getActivity());
                        // Record successful login through fabric.
                        Answers.getInstance().logLogin(new LoginEvent()
                            .putMethod("default")
                            .putSuccess(true));
                    }
                });
            }});
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
