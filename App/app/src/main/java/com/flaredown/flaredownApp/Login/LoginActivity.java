package com.flaredown.flaredownApp.Login;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.flaredown.flaredownApp.Helpers.APIv2.APIResponse;
import com.flaredown.flaredownApp.Helpers.APIv2.Communicate;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Session.Session;
import com.flaredown.flaredownApp.Helpers.APIv2.Error;
import com.flaredown.flaredownApp.Helpers.APIv2.ErrorDialog;
import com.flaredown.flaredownApp.Helpers.Styling.Styling;
import com.flaredown.flaredownApp.Main.MainActivity;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Receivers.InternetStatusBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private Context mContext;
    private static final String IS_RESTORING = "restoring";
    private final String DEBUG_TAG = "LoginActivity";
    //private API flareDownAPI;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private TextView tv_ForgotPassword;
    private TextView tv_noInternetConnection;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Styling.setFont();

        setContentView(R.layout.login_activity);

        // Set up the login form.
        tv_noInternetConnection = (TextView) findViewById(R.id.tv_noInternetConnection);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.act_email);

        mPasswordView = (EditText) findViewById(R.id.password);
        tv_ForgotPassword = (TextView) findViewById(R.id.tv_ForgotPassword);
        tv_ForgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getResources().getString(R.string.password_reset_website)));
                startActivity(intent);
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_GO) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.bt_sign_in);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.ll_email_login_form);

        // Listen out for internet connectivity
        InternetStatusBroadcastReceiver.initiate(mContext, new Runnable() {
            @Override
            public void run() {
                internetConnectivity = true;
                setView(VIEW_LOGIN);
                setViewInternetConnectivity(true);
            }
        }, new Runnable() {
            @Override
            public void run() {
                internetConnectivity = false;
                setViewInternetConnectivity(false);
            }
        });

        if(savedInstanceState == null) {
            // Display splash screen for 3 seconds, then display the login window.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setView(VIEW_LOGIN);
                        }
                    });
                }
            }).start();
        } else setView(VIEW_LOGIN); //TODO restore correct view on rotate (if there is no internet connectivity).
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_RESTORING, true);
        super.onSaveInstanceState(outState);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getResources().getString(R.string.locales_nice_errors_field_invalid).replace("{{field}}", "Password"));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getResources().getString(R.string.locales_nice_errors_field_required).replace("{{field}}", "Email"));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.locales_nice_errors_field_invalid).replace("{{field}}", "Email"));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            setView(VIEW_LOADING);
            //mAuthTask = new UserLoginTask(email, password);
            //mAuthTask.execute((Void) null);

            new Communicate(this).userSignIn(email, password, new APIResponse<Session, com.flaredown.flaredownApp.Helpers.APIv2.Error>() {
                @Override
                public void onSuccess(Session result) {
                    // Tell Fabric.
                    Answers.getInstance().logLogin(new LoginEvent()
                        .putMethod("default")
                        .putSuccess(true));

                    Intent intent = new Intent(mContext, MainActivity.class);
                    // Stops the transition animation from occurring.
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    // Prevents the user going back.
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    // Stop the transition animation from occurring.
                    overridePendingTransition(0,0);
                    finish();
                }

                @Override
                public void onFailure(Error result) {
                    setView(VIEW_LOGIN);
                    // Check for incorrect credentials
                    if(result.getStatusCode() ==  401 && result.getErrorList().indexOf("invalid email or password") >= 0) {
                        String errorMessage = mContext.getString(R.string.locales_nice_errors_bad_credentials);
                        mEmailView.setError(errorMessage);
                        mPasswordView.setError(errorMessage);
                    } else
                        new ErrorDialog(mContext, result).setCancelable(true).show();
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 1;
    }

    /**
     * List of available views....
     */
    private final int VIEW_LOGIN = 0;
    private final int VIEW_LOADING = 1;
    private final int VIEW_NO_INTERNET = 2;

    private boolean internetConnectivity = true;
    private int previousView = 1;
    // Set views
    public void setView(int viewId){ setView(viewId, false); }
    public void setView(int viewId, boolean override) {
        if(viewId != VIEW_NO_INTERNET)
            previousView = viewId;
        if(internetConnectivity && viewId != VIEW_NO_INTERNET) {
            switch(viewId) {
                case VIEW_LOGIN:
                    mLoginFormView.setVisibility(View.VISIBLE);
                    tv_noInternetConnection.setVisibility(View.GONE);
                    break;
                default:
                    mLoginFormView.setVisibility(View.GONE);
                    tv_noInternetConnection.setVisibility(View.GONE);
            }
        } else {
            mLoginFormView.setVisibility(View.GONE);
            tv_noInternetConnection.setVisibility(View.VISIBLE);
        }
    }
    public void setViewInternetConnectivity(boolean connected) {
        internetConnectivity = connected;
        if(connected)
            setView(previousView);
        else
            setView(VIEW_NO_INTERNET);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}