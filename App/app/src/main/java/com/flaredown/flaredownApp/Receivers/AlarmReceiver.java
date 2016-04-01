package com.flaredown.flaredownApp.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.flaredown.flaredownApp.Helpers.FlaredownConstants;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null){
            //Reset alarm if time zone changed
            if ("android.intent.action.TIME_SET".equals(intent.getAction()) || "android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction()) || "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                Intent serviceIntent = new Intent(context,AlarmIntentService.class);
                serviceIntent.putExtra(FlaredownConstants.KEY_ALARM_RECEIVER_RESET,FlaredownConstants.VALUE_ALARM_RECEIVER_RESET_ALL);
                context.startService(serviceIntent);
            } else if (intent.hasExtra(FlaredownConstants.KEY_ALARM_ID)){
                Log.d("AlarmReceiver","Intent Key Found");
                Intent serviceIntent = new Intent(context,AlarmIntentService.class);
                serviceIntent.putExtra(FlaredownConstants.KEY_ALARM_ID,intent.getIntExtra(FlaredownConstants.KEY_ALARM_ID,0));
                context.startService(serviceIntent);
            }
        }
    }
}
