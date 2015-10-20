package com.flaredown.flaredownApp.FlareDown;

/**
 * Created by squigge on 10/15/2015.
 */
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.flaredown.com.flaredown.R;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.flaredown.flaredownApp.Checkin.CheckinActivity;
import com.flaredown.flaredownApp.PreferenceKeys;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    Notification myNotification;
    private static final int MY_NOTIFICATION_ID=1456465879;
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        //get alarm time
        SharedPreferences sp = PreferenceKeys.getSharedPreferences(context);
        String alarmTime = sp.getString("reminder_time","");

        if (!alarmTime.isEmpty() && alarmTime != null){
            Calendar firingCal= Calendar.getInstance();
            Calendar currentCal = Calendar.getInstance();
            firingCal.setTimeInMillis(Long.valueOf(alarmTime));
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            //Get diff between current time and alarm time
            //- value is alarm in past, + value is alarm in future
            Long diff = firingCal.getTimeInMillis() - currentCal.getTimeInMillis();
            //Figure out if alarm has already been triggered within 60 seconds
            if (diff > 60000){//yet to be triggered, do nothing
                Log.d("Alarm:","> Not Triggered");
            }
            else if (diff < -60000){ //alarm already fired, reschedule
                firingCal.add(Calendar.DATE, 1);
                Log.d("Alarm:", "< Rescheduled for " + firingCal.getTime().toString());
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firingCal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
            else{ //fire now
                Log.d("Alarm:","= Triggered");
                PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, CheckinActivity.class), 0);
                myNotification = new NotificationCompat.Builder(context)
                        .setContentTitle("FlareDown Alarm")
                        .setContentText("It's time to Check In!")
                        .setContentIntent(pi)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.logo)
                        .build();
                notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
            }
        }
    }
}