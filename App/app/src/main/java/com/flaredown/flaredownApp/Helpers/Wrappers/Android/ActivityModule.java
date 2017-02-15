package com.flaredown.flaredownApp.Helpers.Wrappers.Android;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger2 Module for injection.
 */
@Module
public class ActivityModule {
    private ActivityWrapper activity;

    public ActivityModule(ActivityWrapper activity) {
        this.activity = activity;
    }

    @Provides
    public ActivityWrapper provideActivity() {
        return this.activity;
    }
}
