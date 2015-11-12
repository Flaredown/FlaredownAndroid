package com.flaredown.flaredownApp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.flaredown.com.flaredown.R;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class FragmentTreatmentReminder extends DialogFragment implements View.OnClickListener,TimePickerDialog.OnTimeSetListener,AdapterView.OnItemClickListener{
    private View mView;
    private ListView mlvTreatmentReminders;
    private ReminderListAdapter<String> mAdapter;
    private List<String> mTimes = new ArrayList<String>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mView = getActivity().getLayoutInflater().inflate(R.layout.fragment_treatment_reminder, null);
        builder.setView(mView);

        Switch swMonday = (Switch) mView.findViewById(R.id.swTreatmentReminderMonday);
        Switch swTuesday = (Switch) mView.findViewById(R.id.swTreatmentReminderTuesday);
        Switch swWednesday = (Switch) mView.findViewById(R.id.swTreatmentReminderWednesday);
        Switch swThursday = (Switch) mView.findViewById(R.id.swTreatmentReminderThursday);
        Switch swFriday = (Switch) mView.findViewById(R.id.swTreatmentReminderFriday);
        Switch swSaturday = (Switch) mView.findViewById(R.id.swTreatmentReminderSaturday);
        Switch swSunday = (Switch) mView.findViewById(R.id.swTreatmentReminderSunday);
        TextView tvTreatmentAddReminderTime = (TextView) mView.findViewById(R.id.tvTreatmentAddReminderTime);
        mlvTreatmentReminders = (ListView) mView.findViewById(R.id.lvTreatmentReminderTimes);
        mAdapter = new ReminderListAdapter<>(mTimes);
        mlvTreatmentReminders.setAdapter(mAdapter);
        mlvTreatmentReminders.setOnItemClickListener(this);

        //Listeners
        swMonday.setOnClickListener(this);
        swTuesday.setOnClickListener(this);
        swWednesday.setOnClickListener(this);
        swThursday.setOnClickListener(this);
        swFriday.setOnClickListener(this);
        swSaturday.setOnClickListener(this);
        swSunday.setOnClickListener(this);
        tvTreatmentAddReminderTime.setOnClickListener(this);

        return builder.create();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        HashMap<String, String> listItem = new HashMap<String, String>();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
        time.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        mTimes.add(sdf.format(time.getTimeInMillis()));
        mAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnItems(mlvTreatmentReminders);
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
        switch(view.getId()){
            case R.id.tvTreatmentAddReminderTime:
                //Popup time picker
                TimePickerDialog time = new TimePickerDialog(getActivity(),this,Calendar.HOUR_OF_DAY,Calendar.MINUTE,false);
                time.show();
                break;
        }
    }

    class ReminderListAdapter<String> extends ArrayAdapter<String> {
        List<String> mTimes;

        @Override
        public int getCount() {
            return mTimes.size();
        }

        ReminderListAdapter(List<String> times) {
            super(getActivity(),R.layout.treatment_reminder_times,times);
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
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.treatment_reminder_times, parent, false);

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