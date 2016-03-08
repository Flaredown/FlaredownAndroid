package com.flaredown.flaredownApp.Login;

import android.app.Activity;
import android.content.Intent;

import com.flaredown.flaredownApp.Helpers.API.API;
import com.flaredown.flaredownApp.Helpers.PreferenceKeys;

/**
 * Created by thunter on 14/09/15.
 */
public class ForceLogin {
    public ForceLogin(Activity activity, API flareDownAPI) {
        if(!flareDownAPI.isLoggedIn()) {
            PreferenceKeys.log(PreferenceKeys.LOG_I, "HomeActivity", "User not logged in, redirecting to login activity");
            Intent intent = new Intent(activity, LoginActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
            activity.finish();
        }
    }
}
