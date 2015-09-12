package com.flaredown.flaredownApp;

import android.content.Context;
import android.content.Intent;
import android.flaredown.com.flaredown.R;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class HomeActivity extends AppCompatActivity {
    Context mContext;
    FlareDownAPI flareDownAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        flareDownAPI = new FlareDownAPI(mContext);
        super.onCreate(savedInstanceState);
        // Checking if user is logged in, otherwise redirect to login screen.

        if(!flareDownAPI.isLoggedIn(false)) {
            PreferenceKeys.log(PreferenceKeys.LOG_I, "HomeActivity", "User not logged in, redirecting to login activity");
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }
        setContentView(R.layout.activity_home);
    }

}
