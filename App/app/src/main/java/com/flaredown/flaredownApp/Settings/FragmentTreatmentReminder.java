package com.flaredown.flaredownApp.Settings;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flaredown.flaredownApp.Helpers.TimeHelper;
import com.flaredown.flaredownApp.Models.Alarm;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Receivers.AlarmReceiver;
import com.flaredown.flaredownApp.FlareDown.Locales;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class FragmentTreatmentReminder extends DialogFragment implements View.OnClickListener,TimePickerDialog.OnTimeSetListener,AdapterView.OnItemClickListener{
    private View mView;
    private ListView mlvTreatmentReminders;
    private ReminderListAdapter<String> mAdapter;
    private List<String> mTimes = new ArrayList<>();
    private Realm mRealm;
    private String mTreatmentTitle;
    private RealmResults<Alarm> mAlarms;
    private Switch swSunday;
    private Switch swMonday;
    private Switch swTuesday;
    private Switch swWednesday;
    private Switch swThursday;
    private Switch swFriday;
    private Switch swSaturday;
    private Context mContext;



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mView = getActivity().getLayoutInflater().inflate(R.layout.fragment_treatment_reminder, null);
        builder.setView(mView);

        mContext = mView.getContext();

        mRealm = Realm.getInstance(getActivity());

        swMonday = (Switch) mView.findViewById(R.id.swTreatmentReminderMonday);
        swTuesday = (Switch) mView.findViewById(R.id.swTreatmentReminderTuesday);
        swWednesday = (Switch) mView.findViewById(R.id.swTreatmentReminderWednesday);
        swThursday = (Switch) mView.findViewById(R.id.swTreatmentReminderThursday);
        swFriday = (Switch) mView.findViewById(R.id.swTreatmentReminderFriday);
        swSaturday = (Switch) mView.findViewById(R.id.swTreatmentReminderSaturday);
        swSunday = (Switch) mView.findViewById(R.id.swTreatmentReminderSunday);
        TextView tvTreatmentAddReminderTime = (TextView) mView.findViewById(R.id.tvTreatmentAddReminderTime);
        mlvTreatmentReminders = (ListView) mView.findViewById(R.id.lvTreatmentReminderTimes);
        mAdapter = new ReminderListAdapter<>(mTimes);
        mlvTreatmentReminders.setAdapter(mAdapter);
        mlvTreatmentReminders.setOnItemClickListener(this);
        TextView tvTreatmentReminderTitle = (TextView) mView.findViewById(R.id.tvTreatmentReminderTitle);

        //Listeners
        swMonday.setOnClickListener(this);
        swTuesday.setOnClickListener(this);
        swWednesday.setOnClickListener(this);
        swThursday.setOnClickListener(this);
        swFriday.setOnClickListener(this);
        swSaturday.setOnClickListener(this);
        swSunday.setOnClickListener(this);
        tvTreatmentAddReminderTime.setOnClickListener(this);

        //Get all known treatment alarms
        mTreatmentTitle = getArguments().getString("treatment_title");
        if (!mTreatmentTitle.isEmpty() || null != mTreatmentTitle){
            //Set Title
            tvTreatmentReminderTitle.setText(mTreatmentTitle + " Reminder");
            RealmQuery<Alarm> query = mRealm.where(Alarm.class);
            query.equalTo("title", "treatment_reminder_" + mTreatmentTitle);
            mAlarms = query.findAll();
            //Populate times and checks
            if (null != mAlarms){
                if (mAlarms.size() > 0){
                    for (Alarm x : mAlarms){
                        //Activate switches for each day
                        switch (x.getDayOfWeek()){
                            case "sunday":
                                swSunday.setChecked(true);
                                break;
                            case "monday":
                                swMonday.setChecked(true);
                                break;
                            case "tuesday":
                                swTuesday.setChecked(true);
                                break;
                            case "wednesday":
                                swWednesday.setChecked(true);
                                break;
                            case "thursday":
                                swThursday.setChecked(true);
                                break;
                            case "friday":
                                swFriday.setChecked(true);
                                break;
                            case "saturday":
                                swSaturday.setChecked(true);
                                break;
                        }
                        //Add times to listview
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                        Calendar alarmTime = Calendar.getInstance();
                        alarmTime.setTimeInMillis(x.getTime() - TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
                        String timeFormatted = sdf.format(alarmTime.getTimeInMillis());
                        if (!mTimes.contains(timeFormatted)){
                            mTimes.add(timeFormatted);
                            mAdapter.notifyDataSetChanged();
                            setListViewHeightBasedOnItems(mlvTreatmentReminders);
                        }
                    }
                }
            }
        }

        //Remove any previous alarms
        unscheduleAllAlarms();

        return builder.create();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
        time.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        mTimes.add(sdf.format(time.getTimeInMillis()));
        mAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnItems(mlvTreatmentReminders);

        //If there are existing alarms, add new alarm(s) with new times
        if (null != mAlarms && mAlarms.size() > 0){
            for (Alarm x : mAlarms) {
                Alarm newAlarm = new Alarm();
                newAlarm.setTitle(mTreatmentTitle);
                newAlarm.setTime(time.getTimeInMillis());
                newAlarm.setId(new Random(Calendar.getInstance().getTimeInMillis()).nextInt());
                newAlarm.setDayOfWeek(x.getDayOfWeek());
                addUpdateOneAlarm(x);
            }
        }
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvTreatmentAddReminderTime:
                //Popup time picker
                TimePickerDialog time = new TimePickerDialog(mContext, this,Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE), false);
                time.show();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Context context = mView.getContext();
        // Save/Update all alarms
        if (mTimes.size() > 0){
            if (updateAllAlarms() && scheduleAllAlarms()){
                Toast.makeText(context,Locales.read(context,"confirmation_message.alarms_saved").create(), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context,Locales.read(context,"nice_errors.general_error_description").create(), Toast.LENGTH_SHORT).show();
            }
        }
        else { //they removed all times or alarms
            if (removeAllAlarms()&& unscheduleAllAlarms()){
                Toast.makeText(context, Locales.read(context, "confirmation_message.alarms_saved").create(), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context,Locales.read(context,"nice_errors.general_error_description").create(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean removeAllAlarms(){
        try{
            mRealm.beginTransaction();
            mRealm.where(Alarm.class).equalTo("title","treatment_reminder_" + mTreatmentTitle).findAll().clear();
            mRealm.commitTransaction();
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    private boolean addUpdateOneAlarm(Alarm alarm){
        try{
            mRealm.beginTransaction();
            Alarm realmAlarm = mRealm.copyToRealmOrUpdate(alarm);
            mRealm.commitTransaction();
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    private boolean updateAllAlarms(){
        try{
            removeAllAlarms();
             if (mTimes.size() > 0) { //We have times scheduled
                for (String time : mTimes) { //Loop through each time
                    //Check each day and schedule
                    if (swSunday.isChecked()){
                        Alarm newAlarm = new Alarm();
                        newAlarm.setTitle("treatment_reminder_" + mTreatmentTitle);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                        Calendar cal = Calendar.getInstance();
                        Calendar curCal = Calendar.getInstance();
                        cal.setTime(sdf.parse(time));
                        cal.set(Calendar.YEAR,curCal.get(Calendar.YEAR));
                        cal.set(Calendar.MONTH,curCal.get(Calendar.MONTH));
                        cal.set(Calendar.WEEK_OF_MONTH,curCal.get(Calendar.WEEK_OF_MONTH));
                        cal.clear(Calendar.SECOND);
                        cal.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
                        newAlarm.setTime(cal.getTimeInMillis()+ TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
                        newAlarm.setId(new Random().nextInt());
                        newAlarm.setDayOfWeek("sunday");
                        addUpdateOneAlarm(newAlarm);
                    }
                    if (swMonday.isChecked()){
                        Alarm newAlarm = new Alarm();
                        newAlarm.setTitle("treatment_reminder_" + mTreatmentTitle);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                        Calendar cal = Calendar.getInstance();
                        Calendar curCal = Calendar.getInstance();
                        cal.setTime(sdf.parse(time));
                        cal.set(Calendar.YEAR,curCal.get(Calendar.YEAR));
                        cal.set(Calendar.MONTH,curCal.get(Calendar.MONTH));
                        cal.set(Calendar.WEEK_OF_MONTH,curCal.get(Calendar.WEEK_OF_MONTH));
                        cal.clear(Calendar.SECOND);
                        cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                        newAlarm.setTime(cal.getTimeInMillis()+ TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
                        newAlarm.setId(new Random().nextInt());
                        newAlarm.setDayOfWeek("monday");
                        addUpdateOneAlarm(newAlarm);
                    }
                    if (swTuesday.isChecked()){
                        Alarm newAlarm = new Alarm();
                        newAlarm.setTitle("treatment_reminder_" + mTreatmentTitle);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                        Calendar cal = Calendar.getInstance();
                        Calendar curCal = Calendar.getInstance();
                        cal.setTime(sdf.parse(time));
                        cal.set(Calendar.YEAR,curCal.get(Calendar.YEAR));
                        cal.set(Calendar.MONTH,curCal.get(Calendar.MONTH));
                        cal.set(Calendar.WEEK_OF_MONTH,curCal.get(Calendar.WEEK_OF_MONTH));
                        cal.clear(Calendar.SECOND);
                        cal.set(Calendar.DAY_OF_WEEK,Calendar.TUESDAY);
                        newAlarm.setTime(cal.getTimeInMillis() + TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
                        newAlarm.setId(new Random().nextInt());
                        newAlarm.setDayOfWeek("tuesday");
                        addUpdateOneAlarm(newAlarm);
                    }
                    if (swWednesday.isChecked()){
                        Alarm newAlarm = new Alarm();
                        newAlarm.setTitle("treatment_reminder_" + mTreatmentTitle);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                        Calendar cal = Calendar.getInstance();
                        Calendar curCal = Calendar.getInstance();
                        cal.setTime(sdf.parse(time));
                        cal.set(Calendar.YEAR,curCal.get(Calendar.YEAR));
                        cal.set(Calendar.MONTH,curCal.get(Calendar.MONTH));
                        cal.set(Calendar.WEEK_OF_MONTH,curCal.get(Calendar.WEEK_OF_MONTH));
                        cal.clear(Calendar.SECOND);
                        cal.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);
                        newAlarm.setTime(cal.getTimeInMillis() + TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
                        newAlarm.setId(new Random().nextInt());
                        newAlarm.setDayOfWeek("wednesday");
                        addUpdateOneAlarm(newAlarm);
                    }
                    if (swThursday.isChecked()){
                        Alarm newAlarm = new Alarm();
                        newAlarm.setTitle("treatment_reminder_" + mTreatmentTitle);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                        Calendar cal = Calendar.getInstance();
                        Calendar curCal = Calendar.getInstance();
                        cal.setTime(sdf.parse(time));
                        cal.set(Calendar.YEAR,curCal.get(Calendar.YEAR));
                        cal.set(Calendar.MONTH,curCal.get(Calendar.MONTH));
                        cal.set(Calendar.WEEK_OF_MONTH,curCal.get(Calendar.WEEK_OF_MONTH));
                        cal.clear(Calendar.SECOND);
                        cal.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);
                        newAlarm.setTime(cal.getTimeInMillis()+ TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
                        newAlarm.setId(new Random().nextInt());
                        newAlarm.setDayOfWeek("thursday");
                        addUpdateOneAlarm(newAlarm);
                    }
                    if (swFriday.isChecked()){
                        Alarm newAlarm = new Alarm();
                        newAlarm.setTitle("treatment_reminder_" + mTreatmentTitle);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                        Calendar cal = Calendar.getInstance();
                        Calendar curCal = Calendar.getInstance();
                        cal.setTime(sdf.parse(time));
                        cal.set(Calendar.YEAR,curCal.get(Calendar.YEAR));
                        cal.set(Calendar.MONTH,curCal.get(Calendar.MONTH));
                        cal.set(Calendar.WEEK_OF_MONTH,curCal.get(Calendar.WEEK_OF_MONTH));
                        cal.clear(Calendar.SECOND);
                        cal.set(Calendar.DAY_OF_WEEK,Calendar.FRIDAY);
                        newAlarm.setTime(cal.getTimeInMillis()+ TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
                        newAlarm.setId(new Random().nextInt());
                        newAlarm.setDayOfWeek("friday");
                        addUpdateOneAlarm(newAlarm);
                    }
                    if (swSaturday.isChecked()){
                        Alarm newAlarm = new Alarm();
                        newAlarm.setTitle("treatment_reminder_" + mTreatmentTitle);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                        Calendar cal = Calendar.getInstance();
                        Calendar curCal = Calendar.getInstance();
                        cal.setTime(sdf.parse(time));
                        cal.set(Calendar.YEAR,curCal.get(Calendar.YEAR));
                        cal.set(Calendar.MONTH,curCal.get(Calendar.MONTH));
                        cal.set(Calendar.WEEK_OF_MONTH,curCal.get(Calendar.WEEK_OF_MONTH));
                        cal.clear(Calendar.SECOND);
                        cal.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
                        newAlarm.setTime(cal.getTimeInMillis() + TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
                        newAlarm.setId(new Random().nextInt());
                        newAlarm.setDayOfWeek("saturday");
                        addUpdateOneAlarm(newAlarm);
                    }
                }
            }
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    private boolean scheduleAllAlarms(){
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        try {
            for (Alarm x : mAlarms) {
                Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
                alarmIntent.putExtra("id",x.getId());
                alarmIntent.putExtra("title",x.getTitle());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, x.getId(), alarmIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    manager.setExact(AlarmManager.RTC_WAKEUP, x.getTime() - TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()), pendingIntent);
                } else {
                    manager.set(AlarmManager.RTC_WAKEUP, x.getTime() - TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()), pendingIntent);
                }
            }
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    private boolean unscheduleAllAlarms(){
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        try {
            for (Alarm x : mAlarms) {
                Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);
                alarmIntent.putExtra("id", x.getId());
                alarmIntent.putExtra("title", x.getTitle());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, x.getId(), alarmIntent, 0);
                manager.cancel(pendingIntent);
            }
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    class ReminderListAdapter<String> extends ArrayAdapter<String> {
        List<String> mTimes;

        @Override
        public int getCount() {
            return mTimes.size();
        }

        ReminderListAdapter(List<String> times) {
            super(mContext,R.layout.treatment_reminder_times, (List<String>) times);
            this.mTimes = times;
        }

        public class ViewHolder{
            public TextView tv1;
            public TextView tv2;

        }

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;

            if (convertView == null) {
                row = LayoutInflater.from(mContext).inflate(R.layout.treatment_reminder_times, parent, false);

                holder = new ViewHolder();
                holder.tv1 = (TextView) row.findViewById(R.id.tvTreatmentReminderTime);
                holder.tv2 = (TextView) row.findViewById(R.id.tvTreatmentReminderDelete);

                row.setTag(holder);
            }
            else{
                holder = (ViewHolder)row.getTag();
            }


            if(mTimes.size()>0)
            {
                //SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
                holder.tv1.setText(mTimes.get(position).toString());
                holder.tv2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mTimes.remove(position);
                        notifyDataSetChanged();
                    }
                });
            }
            return row;
        }
    }
}