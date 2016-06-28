package com.flaredown.flaredownApp;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.*;
import io.intercom.android.sdk.Intercom;

public class FlaredownApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Intercom.initialize(this, BuildConfig.INTERCOM_API, BuildConfig.INTERCOM_SECRET);
    }
}
