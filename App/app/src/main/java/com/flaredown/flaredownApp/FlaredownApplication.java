package com.flaredown.flaredownApp;

import android.app.Application;
import android.content.Context;
import android.support.annotation.StringRes;

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
    private static FlaredownApplication instance;

    /**
     * Dagger2 App Component.
     */
    private ApplicationComponent applicationComponent;



    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Dagger 2
        this.applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        this.applicationComponent.injectApplication(this);



        Fabric.with(this, new Crashlytics());
        Intercom.initialize(this, BuildConfig.INTERCOM_API, BuildConfig.APPLICATION_ID);

        Realm.init(this);

        //Default Realm Configuration
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
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


    /**
     * Get string resources without worrying about context.
     * @param resId The resId of the string to {@return}.
     * @return The string related to the {@param resId}
     */
    public static String getStringResource(@StringRes int resId) {
        return instance.getString(resId);
    }

    /**
     * Get the Application Instance.
     * @return The application instance.
     */
    public static FlaredownApplication getInstance() {
        return instance;
    }
}
