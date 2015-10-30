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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = PreferenceKeys.getSharedPreferences(context);
        Long alarmTime = sp.getLong("reminder_time", 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,  0);
        Calendar firingCal= Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();
        firingCal.setTimeInMillis(alarmTime);

        if("android.intent.action.TIME_SET".equals(intent.getAction()) || "android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction())){
            //reset alarms since time changes
            cancelAlarm(manager, pendingIntent);
            setAlarm(context,firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance()));
        }
        else{
            //see if alarm needs to fire
            if (alarmTime > 0){
                //Get diff between current time and alarm time
                //negative value is alarm in past, postive value is alarm in future
                Long diff = (firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance())) - currentCal.getTimeInMillis();
                //Figure out if alarm has already been triggered within 60 seconds
                if (diff > 60000){//yet to be triggered, do nothing
                }
                else if (diff < -60000){ //alarm already fired, reschedule
                    //Reset alarm +1 day
                    firingCal.add(Calendar.DATE, 1);
                    cancelAlarm(manager, pendingIntent);
                    setAlarm(context,firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance()));
                }
                else{ //fire now
                    PendingIntent pi = PendingIntent.getActivity(context, 1, new Intent(context, CheckinActivity.class),0);
                    NotificationManager notificationManager;
                    Notification myNotification;
                    int MY_NOTIFICATION_ID = 1456465879;
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
                    //Reset alarm +1 day
                    firingCal.add(Calendar.DATE, 1);
                    cancelAlarm(manager, pendingIntent);
                    setAlarm(context,firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance()));
                }
            }
        }
    }
    private int getCurrentTimezoneOffset(Calendar c) {
        return c.getTimeZone().getOffset(c.getTimeInMillis());
    }

    private void setAlarm(Context c, Long alarmTime){
        AlarmManager manager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(c, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, 0, alarmIntent, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
        else{
            manager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
        //Update Shared Prefs
        SharedPreferences sp = PreferenceKeys.getSharedPreferences(c);
        SharedPreferences.Editor editor;
        editor = sp.edit();
        editor.putLong("reminder_time", alarmTime + getCurrentTimezoneOffset(Calendar.getInstance()));
        editor.apply();
    }

    private void cancelAlarm(AlarmManager am, PendingIntent p){
        am.cancel(p);
    }
}