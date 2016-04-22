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
import com.flaredown.flaredownApp.Helpers.APIv2.Communicate;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings.Tracking;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings.Trackings;
import com.flaredown.flaredownApp.Helpers.FlaredownConstants;
import com.flaredown.flaredownApp.Helpers.TimeHelper;
import com.flaredown.flaredownApp.Models.Alarm;
import com.flaredown.flaredownApp.Models.Treatment;
import com.flaredown.flaredownApp.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class AlarmIntentService extends IntentService{
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
        alarmIntent.putExtra(FlaredownConstants.KEY_ALARM_ID, id);
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
        //update alarm in realm
        updateAlarmInRealm(alarm, firingCal.getTimeInMillis());
    }

    private void rescheduleTreatmentAlarm(Alarm alarm) {
        Calendar firingCal = Calendar.getInstance();
        firingCal.setTimeInMillis(alarm.getTime());
        //Reset alarm +7 day
        firingCal.add(Calendar.DATE, 7);
        cancelAlarm(recreatePendingIntent(alarm));
        setNewAlarm(alarm.getId(), firingCal.getTimeInMillis() - TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
        //update alarm in realm
        updateAlarmInRealm(alarm, firingCal.getTimeInMillis());
    }

    private boolean checkTreatmentExists(Alarm alarm){
        Communicate flaredownApi = new Communicate(getApplicationContext());
        boolean treatmentExists = false;

        try {
            Trackings trackings = flaredownApi.getTrackingsBlocking(TrackableType.TREATMENT,Calendar.getInstance());
            List<Integer> ids = new ArrayList<>();
            for (Tracking tracking : trackings){
                ids.add(tracking.getTrackable_id());
            }
            List<Treatment> treatments = flaredownApi.getTreatmentsBlocking(ids);
            if (treatments != null && treatments.size() > 0) {
                for (int i = 0; i < treatments.size(); i++) {
                    Treatment treatment = treatments.get(i);
                    if (alarm.getTitle().equals("treatment_reminder_" + treatment.getName().toLowerCase())){
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
                    .setContentTitle(getResources().getString(R.string.locales_treatment_reminder_title))
                    .setContentText(getResources().getString(R.string.locales_treatment_reminder_text) + " " + alarm.getTitle().substring(alarm.getTitle().lastIndexOf("_") + 1))
                    .setContentIntent(pi)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo)
                    .build();
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MY_NOTIFICATION_ID, myNotification);

            //Reset alarm
            rescheduleTreatmentAlarm(alarm);
        } else if (diff < -60000) { //alarm already fired, reschedule
            //Reset alarm
            rescheduleTreatmentAlarm(alarm);
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
                    .setContentTitle(getResources().getString(R.string.locales_alarm_reminder_title))
                    .setContentText(getResources().getString(R.string.locales_alarm_reminder_text))
                    .setContentIntent(pi)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo)
                    .build();
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(MY_NOTIFICATION_ID, myNotification);

            //Reset alarm
            rescheduleCheckinAlarm(alarm);
        } else if (diff < -60000) { //alarm in past, reschedule
            rescheduleCheckinAlarm(alarm);
        }
    }

    private boolean userAlreadyCheckedIn(){
        // TODO Upgrade to the new api
//        API api = new API(getApplicationContext());
//        boolean checkedIn;
//        JSONObject entryJson;
//
//        try{
//            if (api.apiFromCacheIsDirty("entries")){
//                entryJson = api.entryBlocking(Calendar.getInstance().getTime());
//            } else {
//                entryJson = new JSONObject(api.getAPIFromCache("entries"));
//            }
//
//            Entry entry = new Entry(entryJson);
//            Calendar today = Calendar.getInstance();
//            today.set(Calendar.HOUR_OF_DAY, 0);
//            today.set(Calendar.MINUTE, 0);
//            today.set(Calendar.SECOND,0);
//            today.set(Calendar.MILLISECOND, 0);
//            if (entry.getEntryDate().before(today)){
//                checkedIn = false;
//            } else {
//                checkedIn = entry.isComplete();
//            }
//
//        } catch (Exception e){
//            checkedIn = false;
//        }
//        return checkedIn;
        return false; // Just put in to prevent exception TODO remove
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
}
