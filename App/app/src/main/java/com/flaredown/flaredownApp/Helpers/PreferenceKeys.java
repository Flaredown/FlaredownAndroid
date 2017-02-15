package com.flaredown.flaredownApp.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.flaredown.flaredownApp.BuildConfig;

/**
 * Created by thunter on 01/09/15.
 */
public class PreferenceKeys {
    public static final boolean DEBUGGING = !BuildConfig.APPLICATION_ID.equals("com.flaredown.flaredownApp");
    //public static final String API_DOMAIN = "https://app.flaredown.com/v1";

    //Key values stored in sharedpreferences
    public static final String P_FIRST_RUN = "first_run";
    //public static final String P_LOGGED_IN = "logged_in";
    //public static final String P_USER_EMAIL = "user_email";
    //public static final String P_AUTH_TOKEN = "auth_token";

    public static final String SP_Av2_USER_TOKEN = "APIv2User[token]";
    public static final String SP_Av2_USER_EMAIL = "APIv2User[email]";
    public static final String SP_Av2_USER_ID = "APIv2User[id]";
    public static final String SP_Av2_SESSION_ID = "APIv2User[session_id]";
    public static final String SP_Av2_CREATED_AT = "APIv2User[created_at]";
    public static final String SP_Av2_UPDATED_AT = "APIv2User[updated_at]";

    public static final String USER_SESSION = "USER_SESSION";

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
