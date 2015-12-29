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
import android.flaredown.com.flaredown.R;
import android.support.v7.app.NotificationCompat;

import com.flaredown.flaredownApp.Checkin.CheckinActivity;
import com.flaredown.flaredownApp.PreferenceKeys;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class AlarmReceiver extends BroadcastReceiver {
    AlarmManager mManager;
    PendingIntent mPendingIntent;
    Realm mRealm;
    Context mContext;
    Intent mIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        mManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        mRealm = Realm.getInstance(context);
        mContext = context;
        mIntent = intent;

        if (intent != null) {
            //Reset alarm if time zone changed
            if ("android.intent.action.TIME_SET".equals(intent.getAction()) || "android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction()) || "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                RealmQuery<Alarm> query = mRealm.where(Alarm.class);
                query.contains("title", "reminder");
                RealmResults<Alarm> alarms = query.findAll();
                if (alarms.size() > 0) {
                    for (Alarm x : alarms) {
                        mPendingIntent = PendingIntent.getBroadcast(context, x.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        cancelAlarm();
                        setNewAlarm(x.getId(), x.getTitle(), x.getTime() - getCurrentTimezoneOffset(Calendar.getInstance()));
                    }
                }
            }

            if (intent.hasExtra("title")) {
                if (intent.getStringExtra("title").contains("treatment_reminder")) { //treatment reminder
                    Alarm alarm;
                    RealmQuery<Alarm> query = mRealm.where(Alarm.class);
                    query.equalTo("id", intent.getIntExtra("id", 0));
                    alarm = query.findFirst();
                    Boolean fireTreatmentAlarm = false;

                    //Check to see if that treatment still exists by getting the api cache from SP
                    //if we have entries in the SP cache
                    try {
                        String json = PreferenceKeys.getSharedPreferences(context).getString("FlareDownAPI_entries_cache", "");
                        JSONObject entries = new JSONObject(json);
                        JSONObject entry = entries.getJSONObject("entry");
                        JSONArray treatments = entry.getJSONArray("treatments");
                        if (treatments.length() > 0) {
                            for (int i = 0; i < treatments.length(); i++) {
                                JSONObject treatment = treatments.getJSONObject(i);
                                if (alarm.getTitle().equals("treatment_reminder_" + treatment.get("name"))){
                                    //treatment still exists fire alarm
                                    fireTreatmentAlarm = true;
                                }
                            }
                        }
                        else{ //no SP cache default to firing alarm anyway
                            fireTreatmentAlarm = true;
                        }
                        if (fireTreatmentAlarm){
                            createTreatmentAlarm(alarm);
                        }
                        else{
                            //treatment not found anymore in API
                            //remove from realm and delete and pending alarms
                            cancelAlarm();
                            mRealm.beginTransaction();
                            mRealm.where(Alarm.class).equalTo("id",alarm.getId()).findAll().clear();
                            mRealm.commitTransaction();
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                else if (intent.getStringExtra("title").contains("checkin_reminder")) { //Checkin Reminder
                    createCheckinAlarm(intent);
                }
            }
        }
    }

    private int getCurrentTimezoneOffset(Calendar c) {
        return c.getTimeZone().getOffset(c.getTimeInMillis());
    }


    private void setNewAlarm(int id, String title, Long alarmTime) {
        Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
        alarmIntent.putExtra("id", id);
        alarmIntent.putExtra("title", title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        } else {
            mManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }

    private void cancelAlarm() {
        mManager.cancel(mPendingIntent);
    }

    private void createCheckinAlarm(Intent intent){
        Calendar firingCal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();
        Alarm alarm;
        RealmQuery<Alarm> query = mRealm.where(Alarm.class);
        query.equalTo("id", intent.getIntExtra("id", 0));
        alarm = query.findFirst();
        firingCal.setTimeInMillis(alarm.getTime());

        //see if alarm needs to fire
        //Get diff between current time and alarm time
        //negative value is alarm in past, postive value is alarm in future
        Long diff = (firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance())) - currentCal.getTimeInMillis();
        //Figure out if alarm has already been triggered within 60 seconds
        if (diff > 60000) {//yet to be triggered, do nothing
        } else if (diff < -60000) { //alarm already fired, reschedule
            //Reset alarm +1 day
            firingCal.add(Calendar.DATE, 1);
            cancelAlarm();
            setNewAlarm(alarm.getId(), alarm.getTitle(), firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance()));
        } else { //fire now
            PendingIntent pi = PendingIntent.getActivity(mContext, 1, new Intent(mContext, CheckinActivity.class), 0);
            NotificationManager notificationManager;
            Notification myNotification;
            int MY_NOTIFICATION_ID = new Random().nextInt();
            myNotification = new NotificationCompat.Builder(mContext)
                    .setContentTitle(Locales.read(mContext, "alarm_reminder_title").create())
                    .setContentText(Locales.read(mContext, "alarm_reminder_text").create())
                    .setContentIntent(pi)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo)
                    .build();
            notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
            //Reset alarm + day
            firingCal.add(Calendar.DATE, 1);
            cancelAlarm();
            setNewAlarm(alarm.getId(), alarm.getTitle(), firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance()));
            //save alarm in realm
            mRealm.beginTransaction();
            alarm.setTime(firingCal.getTimeInMillis());
            Alarm newAlarm = mRealm.copyToRealmOrUpdate(alarm);
            mRealm.commitTransaction();
        }
    }

    private void createTreatmentAlarm(Alarm alarm){
        Calendar firingCal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        firingCal.setTimeInMillis(alarm.getTime());

        //see if alarm needs to fire
        //Get diff between current time and alarm time
        //negative value is alarm in past, postive value is alarm in future
        Long diff = (firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance())) - currentCal.getTimeInMillis();
        //Figure out if alarm has already been triggered within 60 seconds
        if (diff > 60000) {//yet to be triggered, do nothing
        } else if (diff < -60000) { //alarm already fired, reschedule
            //Reset alarm +7 day
            firingCal.add(Calendar.DATE, 7);
            cancelAlarm();
            setNewAlarm(alarm.getId(), alarm.getTitle(), firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance()));
            //save alarm in realm
            mRealm.beginTransaction();
            alarm.setTime(firingCal.getTimeInMillis());
            Alarm newAlarm = mRealm.copyToRealmOrUpdate(alarm);
            mRealm.commitTransaction();
        } else { //fire now
            PendingIntent pi = PendingIntent.getActivity(mContext, 1, new Intent(mContext, CheckinActivity.class), 0);
            NotificationManager notificationManager;
            Notification myNotification;
            int MY_NOTIFICATION_ID = new Random().nextInt();
            myNotification = new NotificationCompat.Builder(mContext)
                    .setContentTitle(Locales.read(mContext, "treatment_reminder_title").create())
                    .setContentText(Locales.read(mContext, "treatment_reminder_text").create() + alarm.getTitle().substring(alarm.getTitle().lastIndexOf("_") + 1))
                    .setContentIntent(pi)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo)
                    .build();
            notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
            //Reset alarm +7 day
            firingCal.add(Calendar.DATE, 7);
            cancelAlarm();
            setNewAlarm(alarm.getId(), alarm.getTitle(), firingCal.getTimeInMillis() - getCurrentTimezoneOffset(Calendar.getInstance()));
            //save alarm in realm
            mRealm.beginTransaction();
            alarm.setTime(firingCal.getTimeInMillis());
            Alarm newAlarm = mRealm.copyToRealmOrUpdate(alarm);
            mRealm.commitTransaction();
        }
    }
}
