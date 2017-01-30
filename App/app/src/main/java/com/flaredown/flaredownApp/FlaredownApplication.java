package com.flaredown.flaredownApp;

import android.app.Activity;
import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.flaredown.flaredownApp.Dagger2.ApplicationComponent;
import com.flaredown.flaredownApp.Dagger2.ApplicationModule;
import com.flaredown.flaredownApp.Dagger2.DaggerApplicationComponent;
import com.flaredown.flaredownApp.Dagger2.HasComponent;
import com.flaredown.flaredownApp.Helpers.Migration;

import io.fabric.sdk.android.Fabric;
import io.intercom.android.sdk.Intercom;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FlaredownApplication extends Application implements HasComponent<ApplicationComponent> {
    /**
     * Dagger2 App Component.
     */
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // Dagger 2
        this.applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        this.applicationComponent.injectApplication(this);



        Fabric.with(this, new Crashlytics());
        Intercom.initialize(this, BuildConfig.INTERCOM_API, BuildConfig.APPLICATION_ID);

        //Default Realm Configuration
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .schemaVersion(1)
                .migration(new Migration())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    /**
     * Get the Dagger2 App component.
     * @return Dagger2 Application Component.
     */
    @Override
    public ApplicationComponent getComponent() {
        return applicationComponent;
    }
}
