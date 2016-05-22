package com.flaredown.flaredownApp.Settings;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flaredown.flaredownApp.EditAccount.FragmentEditAccount;
import com.flaredown.flaredownApp.Helpers.APIv2.APIResponse;
import com.flaredown.flaredownApp.Helpers.APIv2.Communicate;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings.Trackings;
import com.flaredown.flaredownApp.Helpers.APIv2.Error;
import com.flaredown.flaredownApp.Helpers.FlaredownConstants;
import com.flaredown.flaredownApp.Helpers.Styling.Styling;
import com.flaredown.flaredownApp.Helpers.TimeHelper;
import com.flaredown.flaredownApp.Login.ForceLogin;
import com.flaredown.flaredownApp.Models.Alarm;
import com.flaredown.flaredownApp.Models.Treatment;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Receivers.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import io.intercom.android.sdk.Intercom;
import io.realm.Realm;
import io.realm.RealmQuery;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity {
    Context mContext;
    TextView tv_AccountTitle;
    TextView tv_EditAccount;
    TextView tv_SettingsLogout;
    TextView tv_checkinRemindTitle;
    TextView tv_checkinRemindTime;
    TextView tv_treatmentRemindTitle;
    TextView tv_help;
    Switch sw_checkinReminder;
    LinearLayout ll_treatmentReminder;
    LinearLayout llSettingsProgress;
    RelativeLayout rlSettings;
    TextView tv_terms;
    TextView tv_policy;
    Intent alarmIntent;
    AlarmManager manager;
    Communicate flareDownAPI;
    Realm mRealm;
    Alarm mAlarm;
    Alarm mProxyAlarm;
    List<Treatment> mTreatments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        mContext = this;
        Styling.setFont();
        mRealm = Realm.getInstance(mContext);

        flareDownAPI = new Communicate(mContext);
        if(!flareDownAPI.isCredentialsSaved()) {  // Prevent other code running if not logged in.
            new ForceLogin(this);
            return;
        }

        tv_AccountTitle = (TextView) findViewById(R.id.tv_accountTitle);
        tv_EditAccount = (TextView) findViewById(R.id.tv_editAccount);
        tv_SettingsLogout = (TextView) findViewById(R.id.tv_settingsLogout);
        tv_checkinRemindTitle = (TextView) findViewById(R.id.tv_checkinRemindTitle);
        tv_checkinRemindTime = (TextView) findViewById(R.id.tv_checkinRemindTime);
        tv_treatmentRemindTitle = (TextView) findViewById(R.id.tv_treatmentRemindTitle);
        sw_checkinReminder = (Switch) findViewById(R.id.sw_checkinReminder);
        ll_treatmentReminder = (LinearLayout)findViewById(R.id.llTreatmentReminder);
        tv_terms = (TextView)findViewById(R.id.terms);
        tv_policy = (TextView)findViewById(R.id.privacy_policy);
        llSettingsProgress = (LinearLayout) findViewById(R.id.llSettingsProgress);
        rlSettings = (RelativeLayout) findViewById(R.id.rlSettings);
        tv_help = (TextView) findViewById(R.id.tv_help);

        llSettingsProgress.setVisibility(View.VISIBLE);
        rlSettings.setVisibility(View.GONE);

        //Set Toolbar
        Toolbar mainToolbarView = (Toolbar) findViewById(R.id.toolbar_top);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(R.string.title_activity_settings);
        setSupportActionBar(mainToolbarView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Get checkin reminder from realm if one exists
        RealmQuery<Alarm> query = mRealm.where(Alarm.class);
        query.contains(FlaredownConstants.ALARM_TITLE_NAME, FlaredownConstants.ALARM_TITLE_VALUE_CHECKIN_REMINDER);
        mProxyAlarm = query.findFirst();

        if (mProxyAlarm != null){
            mAlarm = new Alarm();
            mAlarm.setTitle(mProxyAlarm.getTitle());
            mAlarm.setDayOfWeek(mProxyAlarm.getDayOfWeek());
            mAlarm.setId(mProxyAlarm.getId());
            mAlarm.setTime(mProxyAlarm.getTime());
            sw_checkinReminder.setChecked(true);
            SimpleDateFormat sdf = new SimpleDateFormat(FlaredownConstants.SIMPLE_DATE_FORMAT_HOUR_MINUTE);
            String time = sdf.format(mAlarm.getTime());
            tv_checkinRemindTime.setText(time);
        }

        flareDownAPI.getTrackings(TrackableType.TREATMENT, Calendar.getInstance(), new APIResponse<Trackings, com.flaredown.flaredownApp.Helpers.APIv2.Error>() {
            @Override
            public void onSuccess(Trackings trackings) {
                List<Integer> ids = new ArrayList<Integer>();
                for (int i = 0; i < trackings.size(); i++) {
                    ids.add(trackings.get(i).getTrackable_id());
                }
                flareDownAPI.getTreatments(ids, new APIResponse<List<Treatment>, Error>() {
                    @Override
                    public void onSuccess(List<Treatment> treatments) {
                        if (mTreatments != null){
                            mTreatments.clear();
                        }
                        mTreatments = treatments;
                        showTreatments();
                        llSettingsProgress.setVisibility(View.GONE);
                        rlSettings.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Error result) {
                        llSettingsProgress.setVisibility(View.GONE);
                        rlSettings.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(Error result) {
                llSettingsProgress.setVisibility(View.GONE);
                rlSettings.setVisibility(View.VISIBLE);
            }
        });

        //Listeners
        tv_EditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                FragmentEditAccount frag = new FragmentEditAccount();
                ft.attach(frag);
                frag.show(ft, FlaredownConstants.EDIT_ACCOUNT_FRAGMENT_TITLE);

            }
        });

        tv_SettingsLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flareDownAPI.userSignOut();
                new ForceLogin(SettingsActivity.this);
                finish();
            }
        });

        sw_checkinReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mAlarm = new Alarm();
                    SimpleDateFormat sdf = new SimpleDateFormat(FlaredownConstants.SIMPLE_DATE_FORMAT_HOUR_MINUTE);
                    Calendar cal = Calendar.getInstance();
                    String currentTime = sdf.format(cal.getTimeInMillis());
                    tv_checkinRemindTime.setText(currentTime);
                    tv_checkinRemindTime.setAlpha((float) 1);
                    mAlarm.setId(new Random(Calendar.getInstance().getTimeInMillis()).nextInt());
                    mAlarm.setTime(cal.getTimeInMillis() + TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
                    mAlarm.setTitle(FlaredownConstants.ALARM_TITLE_VALUE_CHECKIN_REMINDER);
                } else {
                    tv_checkinRemindTime.setAlpha((float) .20);
                }
            }
        });

        tv_checkinRemindTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Popup time picker
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                alertDialog.setTitle("");

                final TimePicker picker = new TimePicker(mContext);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                picker.setLayoutParams(lp);
                picker.setIs24HourView(DateFormat.is24HourFormat(mContext));
                picker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                picker.setCurrentMinute(Calendar.getInstance().get(Calendar.MINUTE));
                alertDialog.setView(picker);

                alertDialog.setPositiveButton(R.string.locales_nav_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        SimpleDateFormat sdf = new SimpleDateFormat(FlaredownConstants.SIMPLE_DATE_FORMAT_HOUR_MINUTE);
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
                        cal.set(Calendar.MINUTE, picker.getCurrentMinute());
                        cal.clear(Calendar.SECOND);
                        mAlarm.setTime(cal.getTimeInMillis() + TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
                        tv_checkinRemindTime.setText(sdf.format(cal.getTimeInMillis()));
                        dialog.dismiss();
                    }
                });

                alertDialog.setNegativeButton(R.string.locales_nav_back, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }
        });

        tv_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PolicyWebView.class);
                intent.putExtra(FlaredownConstants.BUNDLE_IDENTIFIER_KEY, FlaredownConstants.BUNDLE_IDENTIFIER_VALUE_POLICY);
                startActivity(intent);
            }
        });

        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PolicyWebView.class);
                intent.putExtra(FlaredownConstants.BUNDLE_IDENTIFIER_KEY, FlaredownConstants.BUNDLE_IDENTIFIER_VALUE_TERMS);
                startActivity(intent);
            }
        });

        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intercom.client().displayConversationsList();
            }
        });

        updateLocales();

    }

    private void showTreatments(){

        if (ll_treatmentReminder != null){
            ll_treatmentReminder.removeAllViews();
        }

        ll_treatmentReminder.addView(tv_treatmentRemindTitle);

        for(Treatment treatment : mTreatments){
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.sep_margin_small));
            TextView tv = new TextView(mContext);
            tv.setTextAppearance(mContext, R.style.AppTheme_TextView_Link);
            tv.setLayoutParams(lparams);
            tv.setText(treatment.getName());
            Bundle bundle = new Bundle();
            bundle.putString(FlaredownConstants.BUNDLE_TREATMENT_TITLE_KEY, treatment.getName());
            ll_treatmentReminder.addView(tv);
            tv.setOnClickListener(new View.OnClickListener() {
                private Bundle bundleTitle;

                @Override
                public void onClick(View view) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    FragmentTreatmentReminder frag = new FragmentTreatmentReminder();
                    frag.setArguments(bundleTitle);
                    ft.attach(frag);
                    frag.show(ft, "dialog");
                }

                private View.OnClickListener init(Bundle bundle) {
                    bundleTitle = bundle;
                    return this;
                }
            }.init(bundle));
        }
        llSettingsProgress.setVisibility(View.GONE);
        rlSettings.setVisibility(View.VISIBLE);
    }

    private void addUpdateAlarm(){
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(mAlarm);
        mRealm.commitTransaction();
    }

    private void removeAlarm(){
        mRealm.beginTransaction();
        mRealm.where(Alarm.class).equalTo(FlaredownConstants.ALARM_TITLE_NAME, FlaredownConstants.ALARM_TITLE_VALUE_CHECKIN_REMINDER).findAll().clear();
        mRealm.commitTransaction();
    }

    public void updateLocales() {
        tv_AccountTitle.setText(R.string.locales_menu_item_account);
        tv_EditAccount.setText(R.string.locales_edit_account_info);
        tv_SettingsLogout.setText(R.string.locales_menu_item_logout);
        tv_checkinRemindTitle.setText(R.string.locales_checkin_alarm_title);
        tv_treatmentRemindTitle.setText(R.string.locales_treatment_reminder_title);
        tv_help.setText(R.string.locales_intercom_help_text);
        //If reminder is already set, get it from realm and populate
        if (sw_checkinReminder.isChecked()) { //reminder set
            SimpleDateFormat sdf = new SimpleDateFormat(FlaredownConstants.SIMPLE_DATE_FORMAT_HOUR_MINUTE);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(mAlarm.getTime() - TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()));
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
            Toast.makeText(this, R.string.locales_settings_saved, Toast.LENGTH_LONG).show();
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void save(){
        manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent;
        if (mAlarm != null) {
            alarmIntent.putExtra(FlaredownConstants.KEY_ALARM_ID, mAlarm.getId());
            pendingIntent = PendingIntent.getBroadcast(mContext, mAlarm.getId(), alarmIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else{
            pendingIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        }
        //Save alarms in Realm and create pending intent
        if (sw_checkinReminder.isChecked()) {
            addUpdateAlarm();
            //Set Alarm
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                manager.setExact(AlarmManager.RTC_WAKEUP, mAlarm.getTime() - TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()), pendingIntent);
            } else {
                manager.set(AlarmManager.RTC_WAKEUP, mAlarm.getTime() - TimeHelper.getCurrentTimezoneOffset(Calendar.getInstance()), pendingIntent);
            }
        }
        else { //delete alarm in realm and remove pending intent
            removeAlarm();
            manager.cancel(pendingIntent);
        }
    }
}
