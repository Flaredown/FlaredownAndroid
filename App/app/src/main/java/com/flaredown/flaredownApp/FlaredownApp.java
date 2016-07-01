package com.flaredown.flaredownApp;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.flaredown.flaredownApp.Helpers.Migration;

import io.fabric.sdk.android.Fabric;
import io.intercom.android.sdk.Intercom;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class FlaredownApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Intercom.initialize(this, BuildConfig.INTERCOM_API, BuildConfig.APPLICATION_ID);

        //Default Realm Configuration
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .schemaVersion(1)
                .migration(new Migration())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
