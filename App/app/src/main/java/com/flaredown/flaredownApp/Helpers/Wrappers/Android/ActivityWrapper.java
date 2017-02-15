package com.flaredown.flaredownApp.Helpers.Wrappers.Android;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.flaredown.flaredownApp.Dagger2.ApplicationComponent;
import com.flaredown.flaredownApp.Dagger2.HasComponent;
import com.flaredown.flaredownApp.FlaredownApplication;

import javax.inject.Inject;

/**
 * Activity wrapper, provide extra functionality to the android application and should be used in
 * place of the Activity class.
 */
public abstract class ActivityWrapper extends AppCompatActivity implements HasComponent<ActivityComponent> {
    ActivityComponent activityComponent;
    
    @Inject
    FlaredownApplication application;

    /**
     * Called when the activity is starting.  This is where most initialization
     * should go: calling {@link #setContentView(int)} to inflate the
     * activity's UI, using {@link #findViewById} to programmatically interact
     * with widgets in the UI, calling
     * {@link #managedQuery(Uri, String[], String, String[], String)} to retrieve
     * cursors for data being displayed, etc.
     * <p>
     * <p>You can call {@link #finish} from within this function, in
     * which case onDestroy() will be immediately called without any of the rest
     * of the activity lifecycle ({@link #onStart}, {@link #onResume},
     * {@link #onPause}, etc) executing.
     * <p>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     * @see #onStart
     * @see #onSaveInstanceState
     * @see #onRestoreInstanceState
     * @see #onPostCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HasComponent<ApplicationComponent>) getApplication()).getComponent().injectActivity(this);

        activityComponent = DaggerActivityComponent
                .builder()
                .applicationComponent(((HasComponent<ApplicationComponent>) getApplication()).getComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    @Override
    public ActivityComponent getComponent() {
        return activityComponent;
    }
}
