package com.flaredown.flaredownApp;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.flaredown.com.flaredown.R;
import android.net.Uri;

import android.os.Bundle;
import android.os.Handler;
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

import com.flaredown.flaredownApp.Checkin.CheckinActivity;
import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.DefaultErrors;
import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    private Context mContext;
    private static final String IS_RESTORING = "restoring";
    private final String DEBUG_TAG = "LoginActivity";
    private API flareDownAPI;
    private boolean localesLoaded = false;

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

        setContentView(R.layout.activity_login);

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

        // Populate form if debug... makes signing in a little easier :D.
        if(PreferenceKeys.DEBUGGING) {
            mEmailView.setText("test@flaredown.com");
            mPasswordView.setText("testing123");
        }

        Button mEmailSignInButton = (Button) findViewById(R.id.bt_sign_in);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.ll_email_login_form);

        flareDownAPI = new API(mContext);
        PreferenceKeys.log(PreferenceKeys.LOG_W, DEBUG_TAG, "Locales not loaded, trying to load");
        loadLocales(savedInstanceState == null);
        // Listen out for internet connectivity
        InternetStatusBroadcastReceiver.setUp(mContext, new Runnable() {
            @Override
            public void run() {
                internetConnectivity = true;
                setViewInternetConnectivity(true);
                if (!localesLoaded)
                    loadLocales(false);
            }
        }, new Runnable() {
            @Override
            public void run() {
                internetConnectivity = false;
                setViewInternetConnectivity(false);
            }
        });
    }
    private void loadLocales(final boolean animate) {
        flareDownAPI.getLocales(new API.OnApiResponseObject() {
            @Override
            public void onSuccess(JSONObject locales) {
                localesLoaded = true;
                populateLocales(animate);
            }

            @Override
            public void onFailure(API.API_Error error) {
                new DefaultErrors(mContext, error);
            }
        });
    }
    private void populateLocales(final Boolean animate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(animate)
                    Thread.sleep(2000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setView(VIEW_LOGIN);
                        }
                    });
                } catch (Exception e) {e.printStackTrace();}
            }
        }).start();
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


            flareDownAPI.users_sign_in(email, password, new API.OnApiResponseObject() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    PreferenceKeys.log(PreferenceKeys.LOG_I, DEBUG_TAG, "Successful login");
                    Intent intent = new Intent(mContext, CheckinActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(API.API_Error error) {
                    setView(VIEW_LOGIN);
                    //TODO differentiate between no internet connection and incorrect user details.
                    //PreferenceKeys.log(PreferenceKeys.LOG_E, DEBUG_TAG, "An error has occured");
                    // Check for incorrect credentials
                    if(error.statusCode == 422) {
                        String errorMessage = Locales.read(mContext, "nice_errors.bad_credentials").create();
                        mEmailView.setError(errorMessage);
                        mPasswordView.setError(errorMessage);
                    } else {
                        new DefaultErrors(mContext, error);
                    }
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

    public static class InternetReceiver extends BroadcastReceiver {
        private final Handler handler; // Handler used to execute code on the UI thread;
        private Runnable doOnConnect;
        private Runnable doOnDisconnect;
        private boolean isConnected = true;

        public InternetReceiver(Context context, Handler handler, Runnable doOnConnect, Runnable doOnDisconnect) {
            this.handler = handler;
            this.doOnConnect = doOnConnect;
            this.doOnDisconnect = doOnDisconnect;
            context.registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }

        @Override
        public void onReceive(final Context context, Intent intent) {
            API flareDownAPI = new API(context);
            if(flareDownAPI.checkInternet() && !isConnected)
                handler.post(doOnConnect);
            else if(!flareDownAPI.checkInternet() && isConnected)
                handler.post(doOnDisconnect);
            isConnected = flareDownAPI.checkInternet();
        }
    }
}