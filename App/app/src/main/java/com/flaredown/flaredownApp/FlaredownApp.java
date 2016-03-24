package com.flaredown.flaredownApp;

import android.app.Application;

import io.intercom.android.sdk.Intercom;

/**
 * Created by squiggie on 3/8/16.
 */
public class FlaredownApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Intercom.initialize(this, "android_sdk-08fe7642afc492924de63ea52b997775feeac2b3", "zi05kys7");
    }
}
