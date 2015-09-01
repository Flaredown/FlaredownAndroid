package com.flaredown.flaredownApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by thunter on 01/09/15.
 */
public class PreferenceKeys {
    public static final boolean DEBUGGING = true;
    public static final String API_DOMAIN = "https://api-staging.flaredown.com/v1";

    //Key values stored in sharedpreferences
    public static final String P_FIRST_RUN = "first_run";
    public static final String P_LOGGED_IN = "logged_in";
    public static final String P_USER_EMAIL = "user_email";
    public static final String P_AUTH_TOKEN = "auth_token";

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    // Default logging allows the ability to disable logs.
    public static final int LOG_D = 0;
    public static final int LOG_E = 1;
    public static final int LOG_V = 2;
    public static final int LOG_W = 3;
    public static final int LOG_I = 4;

    public static void log(int type, String tag, String message) {
        if(!DEBUGGING)
            return;
        switch(type) {
            case LOG_E:
                Log.e(tag, message);
                break;
            case LOG_V:
                Log.v(tag, message);
                break;
            case LOG_W:
                Log.w(tag, message);
                break;
            case LOG_I:
                Log.i(tag, message);
                break;
            case LOG_D:
            default:
                Log.d(tag, message);
                break;
        }
    }
}
