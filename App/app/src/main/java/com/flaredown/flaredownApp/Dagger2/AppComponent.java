package com.flaredown.flaredownApp.Dagger2;

import com.flaredown.flaredownApp.Helpers.Wrappers.Android.ActivityWrapper;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger2 Application Component.
 */
@Singleton
@Component(modules = { AppModule.class })
public interface AppComponent {
    /**
     * For injecting an ActivityWrapper object.
     * @param activity The activity for injecting.
     */
    void inject(ActivityWrapper activity);
}
