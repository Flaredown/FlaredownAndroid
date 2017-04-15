package com.flaredown.flaredownApp.Activities.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.flaredown.flaredownApp.Activities.Checkin.CheckinFragment;
import com.flaredown.flaredownApp.Activities.Settings.SettingsFragment;
import com.flaredown.flaredownApp.Activities.WebView.WebViewFragment;
import com.flaredown.flaredownApp.R;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class MainActivity extends AppCompatActivity {

    /**
     * Start the activity.
     * @param context
     */
    public static void startActivity(Context context) {
        startActivity(context, new Intent(context, MainActivity.class));
    }

    /**
     * Start the activity with the intent flags, {@link Intent#FLAG_ACTIVITY_NO_ANIMATION} and
     * {@link Intent#FLAG_ACTIVITY_NO_HISTORY}. Mainly used for the login in view to start this view.
     * @param activity The activity to start from.
     */
    public static void startActivityNoHistoryNoAnimation(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);

        // Disable activity transitions.
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        // Disable history, back button doesn't return to activity.
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        activity.startActivity(intent);
        activity.overridePendingTransition(0,0);
        activity.finish();
    }

    /**
     * Start activity, from pre-existing intent.
     * @param context The context to start the activity from.
     * @param intent The pre-existing intent.
     */
    public static void startActivity(Context context, Intent intent) {
        context.startActivity(intent);
    }


    public static final String I_VIEW = "View Type";
    private BottomBar mBottomBar;
    private CheckinFragment checkinFragment;
    private SettingsFragment settingsFragment;
    private WebViewFragment webViewFragment;


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
                switch (menuItemId) {
                    case R.id.bottomBarItemOne:
                        switchViews(Views.CHECK_IN);
                        break;
                    case R.id.bottomBarItemTwo:
                        switchViews(Views.GRAPH);
                        break;
                    case R.id.bottomBarItemThree:
                        switchViews(Views.ACCOUNT);
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                //do nothing
            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra(I_VIEW)) {
            switchViews((Views) intent.getSerializableExtra(I_VIEW));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mBottomBar.onSaveInstanceState(outState);
    }


    public CheckinFragment getCheckinFragment() {
        return checkinFragment;
    }

    public SettingsFragment getSettingsFragment() {
        return settingsFragment;
    }

    public WebViewFragment getWebViewFragment() {
        return webViewFragment;
    }

    /**
     * Changes the main fragment view to the corresponding view.
     * @param view The view the fragment should be changed to.
     * @return The new fragment.
     */
    public Fragment switchViews(Views view) {
        Fragment frag = null;
        settingsFragment = null;
        webViewFragment = null;
        checkinFragment = null;
        switch (view) {
            case CHECK_IN:
                frag = checkinFragment = new CheckinFragment();
                break;
            case GRAPH:
                frag = webViewFragment = new WebViewFragment();
                break;
            case ACCOUNT:
                frag = settingsFragment = new SettingsFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
        return frag;
    }

    public enum Views {
        CHECK_IN,
        ACCOUNT,
        GRAPH
    }
}
