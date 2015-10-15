package com.flaredown.flaredownApp.FlareDown;

/**
 * Created by squigge on 10/15/2015.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.flaredown.com.flaredown.R;
import android.support.v7.app.NotificationCompat;

import com.flaredown.flaredownApp.Checkin.CheckinActivity;

public class AlarmReceiver extends BroadcastReceiver {
    Notification myNotification;
    private static final int MY_NOTIFICATION_ID=1456465879;
    NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
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