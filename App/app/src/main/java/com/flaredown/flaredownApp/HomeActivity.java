package com.flaredown.flaredownApp;

import android.content.Context;
import android.content.Intent;
import android.flaredown.com.flaredown.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class HomeActivity extends AppCompatActivity {
    Context mContext;
    FlareDownAPI flareDownAPI;

    FrameLayout contentFrame;
    FrameLayout drawFrame;


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

        contentFrame = (FrameLayout) findViewById(R.id.content_frame);
        drawFrame = (FrameLayout) findViewById(R.id.draw_frame);


    }
}
