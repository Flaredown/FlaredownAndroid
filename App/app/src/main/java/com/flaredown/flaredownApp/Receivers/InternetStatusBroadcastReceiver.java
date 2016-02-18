package com.flaredown.flaredownApp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.flaredown.flaredownApp.Helpers.API.API;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thunter on 03/10/2015.
 */
public class InternetStatusBroadcastReceiver extends BroadcastReceiver {
    private Handler handler; // Handler used to execute code on the UI thread;
    private List<Runnable> onConnect = new ArrayList<>();
    private List<Runnable> onDisconnect = new ArrayList<>();
    private boolean isConnected = true;

    public InternetStatusBroadcastReceiver(Context context, Handler handler) {
        this.handler = handler;
        context.registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public InternetStatusBroadcastReceiver(Context context, Handler handler, Runnable doOnConnect, Runnable doOnDisconnect) {
        this(context, handler);
        addOnConnect(doOnConnect);
        addOnDisconnect(doOnDisconnect);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        API flareDownAPI = new API(context);
        if(flareDownAPI.checkInternet() && !isConnected)
            for (Runnable runnable : onConnect) {
                handler.post(runnable);
            }
        else if(!flareDownAPI.checkInternet() && isConnected)
            for (Runnable runnable : onDisconnect) {
                handler.post(runnable);
            }
        isConnected = flareDownAPI.checkInternet();
    }

    public static InternetStatusBroadcastReceiver setUp(Context mContext, Runnable doOnConnect, Runnable doOnDisconnect) {
        InternetStatusBroadcastReceiver internetStatusBroadcastReceiver = new InternetStatusBroadcastReceiver(mContext, new Handler(), doOnConnect, doOnDisconnect);
        return internetStatusBroadcastReceiver;
    }

    public InternetStatusBroadcastReceiver addOnConnect(Runnable onConnect) {
        this.onConnect.add(onConnect);
        return this;
    }
    public InternetStatusBroadcastReceiver addOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect.add(onDisconnect);
        return this;
    }
    public void removeOnConnect(Runnable onConnect) {
        this.onConnect.remove(onConnect);
    }
    public void removeOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect.remove(onDisconnect);
    }
}