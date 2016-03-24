package com.flaredown.flaredownApp.Receivers;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.flaredown.flaredownApp.Checkin.CheckinActivity;
import com.flaredown.flaredownApp.Helpers.API.API;
import com.flaredown.flaredownApp.Helpers.API.API_Error;
import com.flaredown.flaredownApp.Helpers.API.EntryParser.Entry;
import com.flaredown.flaredownApp.Helpers.FlaredownConstants;
import com.flaredown.flaredownApp.Helpers.Locales;
import com.flaredown.flaredownApp.Helpers.TimeHelper;
import com.flaredown.flaredownApp.Models.Alarm;
import com.flaredown.flaredownApp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class AlarmIntentService extends IntentService implements API.OnApiResponse{
    public AlarmIntentService() {
        super("AlarmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Realm realm = Realm.getInstance(getApplicationContext());
        RealmQuery<Alarm> query = realm.where(Alarm.class);
        int id = 0;
        String key = "";

        if (intent.hasExtra(FlaredownConstants.KEY_ALARM_RECEIVER_RESET)){
            key = intent.getStringExtra(FlaredownConstants.KEY_ALARM_RECEIVER_RESET);
        } else if (intent.hasExtra(FlaredownConstants.KEY_ALARM_ID)){
            id = intent.getIntExtra(FlaredownConstants.KEY_ALARM_ID,0);
        }
        if (key.contains(FlaredownConstants.VALUE_ALARM_RECEIVER_RESET_ALL)){
            RealmResults<Alarm> alarms = query.findAll();
            //Reset all alarms because of timezone change or phone reboot
            if (alarms.size() > 0) {
                for (Alarm alarm : alarms) {
                    cancelAlarm(recreatePendingIntent(alarm));
                    setNewAlarm(alarm.getId(), alarm.getTime() - TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
                }
            }
        } else if (id != 0) {
            try {
                query.equalTo("id", id);
                Alarm alarm = query.findFirst();
                if (alarm.getTitle().contains("treatment_reminder")) { //Treatment Reminder
                    if (checkTreatmentExists(alarm)) {
                        createTreatmentAlarm(alarm);
                    } else {
                        //treatment not found anymore in API or cache
                        //remove from realm and delete any pending alarms
                        PendingIntent pendingIntent = recreatePendingIntent(alarm);
                        cancelAlarm(pendingIntent);
                        removeAlarmFromRealm(alarm);
                    }
                } else { //Checkin Reminder
                    if (!userAlreadyCheckedIn()) {
                        createCheckinAlarm(alarm);
                    } else {
                        PendingIntent pendingIntent = recreatePendingIntent(alarm);
                        cancelAlarm(pendingIntent);
                        rescheduleCheckinAlarm(alarm);
                    }
                }

            } catch (Exception e){
            }
        }
    }

    private PendingIntent recreatePendingIntent(Alarm alarm) {
        Intent recreatedIntent = new Intent(getApplicationContext(),AlarmReceiver.class);
        return PendingIntent.getBroadcast(getApplicationContext(), alarm.getId(), recreatedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void cancelAlarm(PendingIntent pendingIntent) {
        AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }

    private void setNewAlarm(int id, Long alarmTime) {
        AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(getApplicationContext().ALARM_SERVICE);
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        alarmIntent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }

    private void rescheduleCheckinAlarm(Alarm alarm) {
        Calendar firingCal = Calendar.getInstance();
        firingCal.setTimeInMillis(alarm.getTime());
        //Reset alarm +1 day
        firingCal.add(Calendar.DATE, 1);
        cancelAlarm(recreatePendingIntent(alarm));
        setNewAlarm(alarm.getId(), firingCal.getTimeInMillis() - TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
    }

    private void rescheduleTreatmentAlarm(Alarm alarm) {
        Calendar firingCal = Calendar.getInstance();
        firingCal.setTimeInMillis(alarm.getTime());
        //Reset alarm +7 day
        firingCal.add(Calendar.DATE, 7);
        cancelAlarm(recreatePendingIntent(alarm));
        setNewAlarm(alarm.getId(), firingCal.getTimeInMillis() - TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
    }

    private boolean checkTreatmentExists(Alarm alarm){
        API api = new API(getApplicationContext());
        JSONArray treatments;
        boolean treatmentExists = false;

        try {
            if (api.apiFromCacheIsDirty("entries")) {
                JSONObject json = api.entryBlocking(Calendar.getInstance().getTime());
                JSONObject entry = json.getJSONObject("entry");
                treatments = entry.getJSONArray("treatments");
            } else {
                String json = api.getAPIFromCache("entries");
                JSONObject entries = new JSONObject(json);
                JSONObject entry = entries.getJSONObject("entry");
                treatments = entry.getJSONArray("treatments");
            }
            if (treatments != null && treatments.length() > 0) {
                for (int i = 0; i < treatments.length(); i++) {
                    JSONObject treatment = treatments.getJSONObject(i);
                    if (alarm.getTitle().equals("treatment_reminder_" + treatment.get("name").toString().toLowerCase())){
                        //treatment still exists
                        treatmentExists = true;
                    }
                }
            } else {
                treatmentExists = true;
            }
        } catch (Exception e){
            //If exception occurs, fall back to just firing the alarm so we act like the treatment still exists
            treatmentExists = true;
        }
        return treatmentExists;
    }

    private void createTreatmentAlarm(Alarm alarm){
        Calendar firingCal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        firingCal.setTimeInMillis(alarm.getTime());

        //see if alarm needs to fire
        //Get diff between current time and alarm time
        //negative value is alarm in past, postive value is alarm in future
        Long diff = (firingCal.getTimeInMillis() - TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance())) - currentCal.getTimeInMillis();
        //Figure out if alarm has already been triggered within 60 seconds
        if (diff >= -60000 && diff <= 60000) {//fire now
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 1, new Intent(getApplicationContext(), CheckinActivity.class), 0);
            NotificationManager notificationManager;
            Notification myNotification;
            int MY_NOTIFICATION_ID = new Random().nextInt();
            myNotification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(Locales.read(getApplicationContext(), "treatment_reminder_title").create())
                    .setContentText(Locales.read(getApplicationContext(), "treatment_reminder_text").create() + alarm.getTitle().substring(alarm.getTitle().lastIndexOf("_") + 1))
                    .setContentIntent(pi)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo)
                    .build();
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MY_NOTIFICATION_ID, myNotification);

            //Reset alarm
            rescheduleTreatmentAlarm(alarm);

            //save alarm in realm
            updateAlarmInRealm(alarm, firingCal.getTimeInMillis());
        } else if (diff < -60000) { //alarm already fired, reschedule
            //Reset alarm
            rescheduleTreatmentAlarm(alarm);
            //update alarm in realm
            updateAlarmInRealm(alarm, firingCal.getTimeInMillis());
        }
    }

    private void createCheckinAlarm(Alarm alarm){
        Calendar firingCal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();

        firingCal.setTimeInMillis(alarm.getTime());

        //see if alarm needs to fire
        //Get diff between current time and alarm time
        //negative value is alarm in past, postive value is alarm in future
        Long diff = (firingCal.getTimeInMillis() - TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance())) - currentCal.getTimeInMillis();
        //Figure out if alarm has already been triggered within 60 seconds
        if (diff >= -60000 && diff <= 60000) { ///fire now
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 1, new Intent(getApplicationContext(), CheckinActivity.class), 0);
            NotificationManager notificationManager;
            Notification myNotification;
            int MY_NOTIFICATION_ID = new Random().nextInt();
            myNotification = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(Locales.read(getApplicationContext(), "alarm_reminder_title").create())
                    .setContentText(Locales.read(getApplicationContext(), "alarm_reminder_text").create())
                    .setContentIntent(pi)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo)
                    .build();
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MY_NOTIFICATION_ID, myNotification);

            //Reset alarm
            rescheduleCheckinAlarm(alarm);

            //update alarm in realm
            updateAlarmInRealm(alarm, firingCal.getTimeInMillis());
        } else if (diff < -60000) { //alarm in past, reschedule
            rescheduleCheckinAlarm(alarm);
        }
    }

    private boolean userAlreadyCheckedIn(){
        API api = new API(getApplicationContext());
        boolean checkedIn;
        JSONObject entryJson;

        try{
            if (api.apiFromCacheIsDirty("entries")){
                entryJson = api.entryBlocking(Calendar.getInstance().getTime());
            } else {
                entryJson = new JSONObject(api.getAPIFromCache("entries"));
            }

            Entry entry = new Entry(entryJson);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND,0);
            today.set(Calendar.MILLISECOND, 0);
            if (entry.getEntryDate().before(today)){
                checkedIn = false;
            } else {
                if (entry.isComplete()){
                    checkedIn = true;
                } else {
                    checkedIn = false;
                }
            }

        } catch (Exception e){
            checkedIn = false;
        }
        return checkedIn;
    }

    private void updateAlarmInRealm(Alarm alarm, Long newTime){
        Realm realm = Realm.getInstance(getApplicationContext());
        realm.beginTransaction();
        alarm.setTime(newTime);
        realm.copyToRealmOrUpdate(alarm);
        realm.commitTransaction();
    }

    private void removeAlarmFromRealm(Alarm alarm){
        Realm realm = Realm.getInstance(getApplicationContext());
        realm.beginTransaction();
        realm.where(Alarm.class).equalTo("id",alarm.getId()).findAll().clear();
        realm.commitTransaction();
    }

    @Override
    public void onFailure(API_Error error) {

    }

    @Override
    public void onSuccess(Object result) {

    }
}
