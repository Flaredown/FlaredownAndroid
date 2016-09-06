package com.flaredown.flaredownApp.Main;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.flaredown.flaredownApp.Checkin.CheckinFragment;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Settings.SettingsFragment;
import com.flaredown.flaredownApp.WebView.WebViewFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class MainActivity extends AppCompatActivity {
    private BottomBar mBottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bottom Menu Bar
        mBottomBar = BottomBar.attach(this, savedInstanceState, ContextCompat.getColor(this, R.color.accent_hover), ContextCompat.getColor(this, R.color.white), .25f);
        mBottomBar.setItems(R.menu.menu_bottom);
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                Fragment frag = null;
                switch (menuItemId) {
                    case R.id.bottomBarItemOne:
                        frag = new CheckinFragment();
                        break;
                    case R.id.bottomBarItemTwo:
                        frag = new WebViewFragment();
                        break;
                    case R.id.bottomBarItemThree:
                        frag = new SettingsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                //do nothing
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mBottomBar.onSaveInstanceState(outState);
    }
}
