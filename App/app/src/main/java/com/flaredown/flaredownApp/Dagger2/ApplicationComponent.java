package com.flaredown.flaredownApp.Dagger2;

import com.flaredown.flaredownApp.Helpers.Wrappers.Android.ActivityWrapper;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger2 Application Component.
 */
@Singleton
@Component(modules = { ApplicationModule.class })
public interface ApplicationComponent {
    /**
     * For injecting an ActivityWrapper object.
     * @param activity The activity for injecting.
     */
    void inject(ActivityWrapper activity);
}
