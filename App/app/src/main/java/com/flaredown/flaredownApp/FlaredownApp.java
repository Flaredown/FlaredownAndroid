package com.flaredown.flaredownApp;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.StringRes;

import com.crashlytics.android.Crashlytics;
import com.flaredown.flaredownApp.Dagger2.AppComponent;
import com.flaredown.flaredownApp.Dagger2.AppModule;
import com.flaredown.flaredownApp.Dagger2.DaggerAppComponent;
import com.flaredown.flaredownApp.Helpers.Migration;

import io.fabric.sdk.android.Fabric;
import io.intercom.android.sdk.Intercom;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FlaredownApp extends Application {

    /**
     * The current active (not paused) activity.
     */
    private Activity activeActivity = null;


    /**
     * Dagger2 App Component.
     */
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // Dagger 2
        this.appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();



        Fabric.with(this, new Crashlytics());
        Intercom.initialize(this, BuildConfig.INTERCOM_API, BuildConfig.APPLICATION_ID);

        //Default Realm Configuration
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .schemaVersion(1)
                .migration(new Migration())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    /*********************************
     *** Current Activity Tracking ***
     *********************************/

    /*
    Keeps track of the current active activity for the android application. Allowing presenters etc.
    to run on the UI thread.
     */

    /**
     * Check to see if there is an active activity.
     * @return true if there is an active activity.
     */
    public boolean isActivityActive() {
        return this.activeActivity != null;
    }

    /**
     * Get the current active activity, null if no current activity.
     * @return The current active activity.
     */
    public Activity getActiveActivity() {
        return activeActivity;
    }

    /**
     * Set the current active activity, do not use to set activity as 'deactive' use {@link #}.
     * @param activeActivity The current active activity.
     */
    public void setActiveActivity(Activity activeActivity) {
        this.activeActivity = activeActivity;
    }

    /**
     * Deactive the activity (only deactivates an activity providing the activity given is equal to
     * the current activity).
     * @param activity The activity to deactivate.
     */
    public void deactiveActiveActivity(Activity activity) {
        if(this.activeActivity != null && this.activeActivity.equals(activity)) {
            this.activeActivity = null;
        }
    }
    /*************************************
     *** END Current Activity Tracking ***
     *************************************/


    /*******************************
     *** Useful helper functions ***
     *******************************/

    /**
     * Execute a Runnable on android's UI thread.
     * @param runnable The runnable to run.
     */
    public void runOnUIThread(Runnable runnable) {
        if(isActivityActive()) {
            getActiveActivity().runOnUiThread(runnable);
        }
    }

    /***********************************
     *** END Useful helper functions ***
     ***********************************/

    /**
     * Get the Dagger2 App component.
     * @return Dagger2 Application Component.
     */
    public AppComponent getAppComponent() {
        return appComponent;
    }
}
