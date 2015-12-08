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
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar firingCal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();
        Realm realm = Realm.getInstance(context);

        if (intent != null) {
            //Reset alarm if time zone changed
            if ("android.intent.action.TIME_SET".equals(intent.getAction()) || "android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction()) || "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                RealmQuery<Alarm> query = realm.where(Alarm.class);
                query.contains("title", "_reminder_");
                RealmResults<Alarm> alarms = query.findAll();
                if (alarms.size() > 0){
                    for (Alarm x: alarms){
                        pendingIntent = PendingIntent.getBroadcast(context,x.getId(),intent,PendingIntent.FLAG_UPDATE_CURRENT);
                        cancelAlarm(manager, pendingIntent);
                        setNewAlarm(context, x.getId(), x.getTitle(), x.getTime() - getCurrentTimezoneOffset(Calendar.getInstance()));
                    }
                }
            }

            if (intent.hasExtra("title")) {
                if (intent.getStringExtra("title").contains("treatment_reminder")){ //treatment reminder
                    Alarm alarm;
                    RealmQuery<Alarm> query = realm.where(Alarm.class);
                    query.equalTo("id", intent.getIntExtra("id", 0));
                    alarm = query.findFirst();
                    firingCal.setTimeInMillis(alarm.getTime());

                    //see if alarm needs to fire
                    //Get diff between current time and alarm time
                    //negative value is alarm in past, postive value is alarm in future
                    Long diff = (firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance())) - currentCal.getTimeInMillis();
                    //Figure out if alarm has already been triggered within 60 seconds
                    if (diff > 60000) {//yet to be triggered, do nothing
                    }
                    else if (diff < -60000) { //alarm already fired, reschedule
                        //Reset alarm +7 day
                        firingCal.add(Calendar.DATE, 7);
                        cancelAlarm(manager, pendingIntent);
                        setNewAlarm(context, alarm.getId(), alarm.getTitle(), firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance()));
                        //save alarm in realm
                        realm.beginTransaction();
                        alarm.setTime(firingCal.getTimeInMillis());
                        Alarm newAlarm = realm.copyToRealmOrUpdate(alarm);
                        realm.commitTransaction();
                    }
                    else { //fire now
                        PendingIntent pi = PendingIntent.getActivity(context, 1, new Intent(context, CheckinActivity.class), 0);
                        NotificationManager notificationManager;
                        Notification myNotification;
                        int MY_NOTIFICATION_ID = new Random().nextInt();
                        myNotification = new NotificationCompat.Builder(context)
                                .setContentTitle(Locales.read(context,"treatment_reminder_title").create())
                                .setContentText(Locales.read(context,"treatment_reminder_text").create() + alarm.getTitle().substring(alarm.getTitle().lastIndexOf("_") + 1))
                                .setContentIntent(pi)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.logo)
                                .build();
                        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
                        //Reset alarm +7 day
                        firingCal.add(Calendar.DATE, 7);
                        cancelAlarm(manager, pendingIntent);
                        setNewAlarm(context, alarm.getId(), alarm.getTitle(), firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance()));
                        //save alarm in realm
                        realm.beginTransaction();
                        alarm.setTime(firingCal.getTimeInMillis());
                        Alarm newAlarm = realm.copyToRealmOrUpdate(alarm);
                        realm.commitTransaction();
                    }
                }
                else if (intent.getStringExtra("title").contains("checkin_reminder")){ //Checkin Reminder
                    Alarm alarm;
                    RealmQuery<Alarm> query = realm.where(Alarm.class);
                    query.equalTo("id", intent.getIntExtra("id", 0));
                    alarm = query.findFirst();
                    firingCal.setTimeInMillis(alarm.getTime());

                    //see if alarm needs to fire
                    //Get diff between current time and alarm time
                    //negative value is alarm in past, postive value is alarm in future
                    Long diff = (firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance())) - currentCal.getTimeInMillis();
                    //Figure out if alarm has already been triggered within 60 seconds
                    if (diff > 60000) {//yet to be triggered, do nothing
                    }
                    else if (diff < -60000) { //alarm already fired, reschedule
                        //Reset alarm +1 day
                        firingCal.add(Calendar.DATE, 1);
                        cancelAlarm(manager, pendingIntent);
                        setNewAlarm(context, alarm.getId(), alarm.getTitle(), firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance()));
                    }
                    else { //fire now
                        PendingIntent pi = PendingIntent.getActivity(context, 1, new Intent(context, CheckinActivity.class), 0);
                        NotificationManager notificationManager;
                        Notification myNotification;
                        int MY_NOTIFICATION_ID = new Random().nextInt();
                        myNotification = new NotificationCompat.Builder(context)
                                .setContentTitle(Locales.read(context,"alarm_reminder_title").create())
                                .setContentText(Locales.read(context,"alarm_reminder_text").create())
                                .setContentIntent(pi)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.logo)
                                .build();
                        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
                        //Reset alarm + day
                        firingCal.add(Calendar.DATE, 1);
                        cancelAlarm(manager, pendingIntent);
                        setNewAlarm(context, alarm.getId(), alarm.getTitle(), firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance()));
                        //save alarm in realm
                        realm.beginTransaction();
                        alarm.setTime(firingCal.getTimeInMillis());
                        Alarm newAlarm = realm.copyToRealmOrUpdate(alarm);
                        realm.commitTransaction();
                    }
                }
            }
        }
    }

    private int getCurrentTimezoneOffset(Calendar c) {
        return c.getTimeZone().getOffset(c.getTimeInMillis());
    }


    private void setNewAlarm(Context c, int id, String title, Long alarmTime){
        AlarmManager manager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(c, AlarmReceiver.class);
        alarmIntent.putExtra("id",id);
        alarmIntent.putExtra("title", title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, id, alarmIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
        else{
            manager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }

    private void cancelAlarm(AlarmManager am, PendingIntent p){
        am.cancel(p);
    }

}