package com.flaredown.flaredownApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.flaredown.flaredownApp.FlareDown.API;

/**
 * Created by thunter on 03/10/2015.
 */
public class InternetStatusBroadcastReceiver extends BroadcastReceiver {
    private final Handler handler; // Handler used to execute code on the UI thread;
    private Runnable doOnConnect;
    private Runnable doOnDisconnect;
    private boolean isConnected = true;

    public InternetStatusBroadcastReceiver(Context context, Handler handler, Runnable doOnConnect, Runnable doOnDisconnect) {
        this.handler = handler;
        this.doOnConnect = doOnConnect;
        this.doOnDisconnect = doOnDisconnect;
        context.registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        API flareDownAPI = new API(context);
        if(flareDownAPI.checkInternet() && !isConnected)
            handler.post(doOnConnect);
        else if(!flareDownAPI.checkInternet() && isConnected)
            handler.post(doOnDisconnect);
        isConnected = flareDownAPI.checkInternet();
    }

    public static InternetStatusBroadcastReceiver setUp(Context mContext, Runnable doOnConnect, Runnable doOnDisconnect) {
        InternetStatusBroadcastReceiver internetStatusBroadcastReceiver = new InternetStatusBroadcastReceiver(mContext, new Handler(), doOnConnect, doOnDisconnect);
        return internetStatusBroadcastReceiver;
    }
}