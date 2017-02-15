package com.flaredown.flaredownApp.Dagger2;

import android.app.Fragment;

import com.flaredown.flaredownApp.FlaredownApplication;
import com.flaredown.flaredownApp.Helpers.Wrappers.Android.ActivityWrapper;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger2 Application Component.
 */
@Singleton
@Component(modules = { ApplicationModule.class })
public interface ApplicationComponent {
    void injectApplication(FlaredownApplication application);

    void injectActivity(ActivityWrapper activityWrapper);

    // Support injecting into both types of fragments.
    void injectFragment(Fragment fragment);
    void injectFragment(android.support.v4.app.Fragment fragment);
}
