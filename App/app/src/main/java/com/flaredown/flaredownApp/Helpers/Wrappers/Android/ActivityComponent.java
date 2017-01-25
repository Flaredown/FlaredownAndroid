package com.flaredown.flaredownApp.Helpers.Wrappers.Android;

import android.app.Fragment;

import com.flaredown.flaredownApp.Activities.Login.LoginFragment;
import com.flaredown.flaredownApp.Dagger2.ApplicationComponent;
import com.flaredown.flaredownApp.Dagger2.ActivityScope;

import dagger.Component;

/**
 * Login Component for Dagger2.
 */
@ActivityScope
@Component (
        dependencies = {
                ApplicationComponent.class
        },
        modules = {
                ActivityModule.class
        }
)
public interface ActivityComponent {
    public void injectFragment(Fragment fragment);
    public void injectFragment(android.support.v4.app.Fragment fragment);
}
