package com.flaredown.flaredownApp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.flaredown.com.flaredown.R;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.AlarmReceiver;
import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity {
    Context mContext;
    //MainToolbarView mainToolbarView;
    TextView tv_AccountTitle;
    TextView tv_EditAccount;
    TextView tv_SettingsLogout;
    TextView tv_checkinRemindTitle;
    TextView tv_checkinRemindTime;
    TextView tv_treatmentRemindTitle;
    Switch sw_checkinReminder;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Calendar alarm = Calendar.getInstance();
    Intent alarmIntent;
    PendingIntent pendingIntent;
    AlarmManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = this;
        Styling.setFont();

        tv_AccountTitle = (TextView) findViewById(R.id.tv_accountTitle);
        tv_EditAccount = (TextView) findViewById(R.id.tv_editAccount);
        tv_SettingsLogout = (TextView) findViewById(R.id.tv_settingsLogout);
        tv_checkinRemindTitle = (TextView) findViewById(R.id.tv_checkinRemindTitle);
        tv_checkinRemindTime = (TextView) findViewById(R.id.tv_checkinRemindTime);
        tv_treatmentRemindTitle = (TextView) findViewById(R.id.tv_treatmentRemindTitle);
        sw_checkinReminder = (Switch) findViewById(R.id.sw_checkinReminder);

        //Set Toolbar
        Toolbar mainToolbarView = (Toolbar) findViewById(R.id.toolbar_top);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Settings");
        setSupportActionBar(mainToolbarView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Get shared prefs and see if reminder already set
        sp = PreferenceKeys.getSharedPreferences(mContext);
        sw_checkinReminder.setChecked(sp.getBoolean("reminder", false));

        // Retrieve a PendingIntent that will perform a broadcast
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(mContext, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, 0);

        //Listeners
        tv_EditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.edit_account_website)));
                startActivity(intent);
            }
        });

        tv_SettingsLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                API flareDownAPI = new API(mContext);
                flareDownAPI.users_sign_out(new API.OnApiResponseObject() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(API.API_Error error) {
                        Toast.makeText(mContext, "Failed to logout", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        sw_checkinReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    String currentTime = sdf.format(new Date());
                    tv_checkinRemindTime.setText(currentTime.toString());
                } else {
                    tv_checkinRemindTime.setText("");
                    manager.cancel(pendingIntent);
                }
            }
        });

        tv_checkinRemindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Popup time picker
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setTitle("");
                alertDialog.setMessage("Pick a reminder time");

                final TimePicker picker = new TimePicker(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                picker.setLayoutParams(lp);
                picker.setIs24HourView(DateFormat.is24HourFormat(mContext));
                picker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                picker.setCurrentMinute(Calendar.getInstance().get(Calendar.MINUTE));
                alertDialog.setView(picker);

                alertDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                        alarm.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
                        alarm.set(Calendar.MINUTE, picker.getCurrentMinute());
                        alarm.clear(Calendar.SECOND);
                        tv_checkinRemindTime.setText(sdf.format(alarm.getTime()));
                        dialog.dismiss();
                    }
                });

                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });

        updateLocales();
    }
    public void updateLocales() {
        tv_AccountTitle.setText(Locales.read(mContext, "menu_item_account").createAT());
        tv_EditAccount.setText(Locales.read(mContext, "account.edit").createAT());
        tv_SettingsLogout.setText(Locales.read(mContext, "menu_item_logout").createAT());
        tv_checkinRemindTitle.setText(R.string.checkin_remind_title);
        tv_treatmentRemindTitle.setText(R.string.treatment_remind_title);

        //If reminder is already set, get it from saved prefs and populate
        if (sw_checkinReminder.isChecked()) { //reminder set
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(Long.valueOf(sp.getString("reminder_time","12:00 PM")));
            tv_checkinRemindTime.setText(sdf.format(c.getTime()));
        }
        else { //reminder not set, set blank
            tv_checkinRemindTime.setText("");
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id ==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        if (id == R.id.action_save){
            save();
            Toast.makeText(this, "Settings Saved", Toast.LENGTH_LONG).show();
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void save(){
        //Save shared prefs and alarms
        editor = sp.edit();
        if (sw_checkinReminder.isChecked()) {
            editor.putBoolean("reminder", true);
            editor.putString("reminder_time", String.valueOf(alarm.getTimeInMillis()));
            //Set Alarm
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        else {
            editor.putBoolean("reminder",false);
            editor.putString("reminder_time", "");
        }
        editor.apply();
    }
}
