package com.flaredown.flaredownApp.Login;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flaredown.flaredownApp.Checkin.CheckinActivity;
import com.flaredown.flaredownApp.Helpers.API.API;
import com.flaredown.flaredownApp.Helpers.API.API_Error;
import com.flaredown.flaredownApp.Helpers.APIv2.*;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Session.Session;
import com.flaredown.flaredownApp.Helpers.APIv2.Error;
import com.flaredown.flaredownApp.Helpers.DefaultErrors;
import com.flaredown.flaredownApp.Helpers.Locales;
import com.flaredown.flaredownApp.Helpers.PreferenceKeys;
import com.flaredown.flaredownApp.Helpers.Styling;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Receivers.InternetStatusBroadcastReceiver;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

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
        populateAutoComplete();

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
                if (id == R.id.et_login || id == EditorInfo.IME_NULL) {
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
        InternetStatusBroadcastReceiver.setUp(mContext, new Runnable() {
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

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
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
            //mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.setError(Locales.read(mContext, "nice_errors.field_invalid").replace("field", "Password").createAT());
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            //mEmailView.setError(getString(R.string.error_field_required));
            mEmailView.setError(Locales.read(mContext, "nice_errors.field_required").replace("field", "Email").createAT());
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            //mEmailView.setError(getString(R.string.error_invalid_email));
            mEmailView.setError(Locales.read(mContext, "nice_errors.field_invalid").replace("field", "Email").createAT());
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
                    Intent intent = new Intent(mContext, CheckinActivity.class);
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
                    new ErrorDialog(mContext, result);
                }
            });


//            flareDownAPI.users_sign_in(email, password, new API.OnApiResponse<JSONObject>() {
//                @Override
//                public void onSuccess(JSONObject jsonObject) {
//                    PreferenceKeys.log(PreferenceKeys.LOG_I, DEBUG_TAG, "Successful login");
//                    Intent intent = new Intent(mContext, CheckinActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    startActivity(intent);
//                    overridePendingTransition(0,0);
//                    finish();
//                }
//
//                @Override
//                public void onFailure(API_Error error) {
//                    setView(VIEW_LOGIN);
//                    //TODO differentiate between no internet connection and incorrect user details.
//                    //PreferenceKeys.log(PreferenceKeys.LOG_E, DEBUG_TAG, "An error has occured");
//                    // Check for incorrect credentials
//                    if(error.statusCode == 422) {
//                        String errorMessage = Locales.read(mContext, "nice_errors.bad_credentials").create();
//                        mEmailView.setError(errorMessage);
//                        mPasswordView.setError(errorMessage);
//                    } else {
//                        new DefaultErrors(mContext, error);
//                    }
//                }
//            });

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

        addEmailsToAutoComplete(emails);
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


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }
}