package com.flaredown.flaredownApp.Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Helper class used to help diagnose the internet connectivity.
 */
public class InternetConnectivity {

    /**
     * Use the connectivity manager to test if the device is connected to the internet.
     * @param context
     * @return True if the device is connected to the internet.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected())
            return true;
        return false;
    }
}
