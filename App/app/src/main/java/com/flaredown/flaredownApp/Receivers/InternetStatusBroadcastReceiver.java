package com.flaredown.flaredownApp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.flaredown.flaredownApp.Helpers.InternetConnectivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thunter on 03/10/2015.
 */
public class InternetStatusBroadcastReceiver extends BroadcastReceiver {
    private Context context;
    private Handler handler; // Handler used to execute code on the UI thread;
    private List<Runnable> onConnect = new ArrayList<>();
    private List<Runnable> onDisconnect = new ArrayList<>();
    private boolean isConnected = true;

    public InternetStatusBroadcastReceiver(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        context.registerReceiver(this, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    /**
     * Unregisters the receiver.
     */
    public void unRegisterReceiver() {
        context.unregisterReceiver(this);
    }

    public InternetStatusBroadcastReceiver(Context context, Handler handler, Runnable doOnConnect, Runnable doOnDisconnect) {
        this(context, handler);
        addOnConnect(doOnConnect);
        addOnDisconnect(doOnDisconnect);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        boolean tmpConnected = InternetConnectivity.isConnected(context);

        if(tmpConnected && !isConnected) {
            for (Runnable runnable : onConnect) {
                handler.post(runnable);
            }
        }
        else if(!tmpConnected && isConnected) {
            for (Runnable runnable : onDisconnect) {
                handler.post(runnable);
            }
        }
        isConnected = tmpConnected;
    }

    /**
     * Set up the broadcast receiver and return an instance of the class.
     * @param mContext
     * @param doOnConnect Runnable to do on connection.
     * @param doOnDisconnect Runnable to do on disconnect.
     * @return Instance of this class.
     */
    public static InternetStatusBroadcastReceiver initiate(Context mContext, Runnable doOnConnect, Runnable doOnDisconnect) {
        InternetStatusBroadcastReceiver internetStatusBroadcastReceiver = new InternetStatusBroadcastReceiver(mContext, new Handler(), doOnConnect, doOnDisconnect);
        return internetStatusBroadcastReceiver;
    }

    /**
     * Add on connect runnable.
     * @param onConnect Runnable to be executed on connect.
     * @return itself.
     */
    public InternetStatusBroadcastReceiver addOnConnect(Runnable onConnect) {
        this.onConnect.add(onConnect);
        return this;
    }

    /**
     * Add on disconnect runnable.
     * @param onDisconnect Runnable to be executed on disconnect.
     * @return itself.
     */
    public InternetStatusBroadcastReceiver addOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect.add(onDisconnect);
        return this;
    }

    /**
     * Remove a connection runnable.
     * @param onConnect The connect runnable to remove.
     */
    public void removeOnConnect(Runnable onConnect) {
        this.onConnect.remove(onConnect);
    }

    /**
     * Remove a disconnect runnable.
     * @param onDisconnect The disconnect runnable to remove.
     */
    public void removeOnDisconnect(Runnable onDisconnect) {
        this.onDisconnect.remove(onDisconnect);
    }
}