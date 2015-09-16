package com.flaredown.flaredownApp.FlareDown;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.flaredown.flaredownApp.LoginActivity;
import com.flaredown.flaredownApp.PreferenceKeys;

/**
 * Created by thunter on 14/09/15.
 */
public class ForceLogin {
    public ForceLogin(Context context, API flareDownAPI) {
        if(!flareDownAPI.isLoggedIn(false)) {
            PreferenceKeys.log(PreferenceKeys.LOG_I, "HomeActivity", "User not logged in, redirecting to login activity");
            Intent intent = new Intent(context, LoginActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            ((Activity)context).finish();
        }
    }
}
