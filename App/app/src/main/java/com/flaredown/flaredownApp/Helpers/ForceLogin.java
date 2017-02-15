package com.flaredown.flaredownApp.Helpers;

import android.app.Activity;
import android.content.Intent;

import com.flaredown.flaredownApp.API.ResponseModel.Sessions;
import com.flaredown.flaredownApp.API.ServerParsingException;
import com.flaredown.flaredownApp.Activities.Login.LoginActivity;
import com.flaredown.flaredownApp.Helpers.APIv2_old.Communicate;

import static com.flaredown.flaredownApp.API.ResponseModel.Sessions.getFromSharedPreferences;

/**
 * Created by thunter on 14/09/15.
 */
public class ForceLogin {
    public ForceLogin(Activity activity) {

        try {
            Sessions session = Sessions.getFromSharedPreferences(activity);
            if(session != null)
                return; // All is okay
        } catch (ServerParsingException e) {

        }

        PreferenceKeys.log(PreferenceKeys.LOG_I, "HomeActivity", "User not logged in, redirecting to login activity");
        Intent intent = new Intent(activity, LoginActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
        activity.finish();
    }
}
