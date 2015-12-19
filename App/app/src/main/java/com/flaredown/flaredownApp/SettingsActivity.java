package com.flaredown.flaredownApp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.flaredown.com.flaredown.R;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
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
import com.flaredown.flaredownApp.FlareDown.API_Error;
import com.flaredown.flaredownApp.FlareDown.Alarm;
import com.flaredown.flaredownApp.FlareDown.AlarmReceiver;
import com.flaredown.flaredownApp.FlareDown.DefaultErrors;
import com.flaredown.flaredownApp.FlareDown.ForceLogin;
import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;

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
    Switch sw_checkinReminder;
    LinearLayout ll_treatmentReminder;
    TextView tv_terms;
    TextView tv_policy;
    Intent alarmIntent;
    AlarmManager manager;
    API flareDownAPI;
    Realm mRealm;
    Alarm mAlarm;
    Alarm mProxyAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = this;
        Styling.setFont();
        mRealm = Realm.getInstance(mContext);

        flareDownAPI = new API(mContext);
        if(!flareDownAPI.isLoggedIn()) {  // Prevent other code running if not logged in.
            new ForceLogin(this, flareDownAPI);
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

        //Set Toolbar
        Toolbar mainToolbarView = (Toolbar) findViewById(R.id.toolbar_top);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(R.string.title_activity_settings);
        setSupportActionBar(mainToolbarView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Get checkin reminder from realm if one exists
        RealmQuery<Alarm> query = mRealm.where(Alarm.class);
        query.contains("title", "checkin_reminder");
        mProxyAlarm = query.findFirst();

        if (mProxyAlarm != null){
            mAlarm = new Alarm();
            mAlarm.setTime(mProxyAlarm.getTime());
            mAlarm.setId(mProxyAlarm.getId());
            mAlarm.setTitle(mProxyAlarm.getTitle());
            sw_checkinReminder.setChecked(true);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            String time = sdf.format(mAlarm.getTime());
            tv_checkinRemindTime.setText(time);
        }

        //Get API information for treatments and display
        flareDownAPI.entries(Calendar.getInstance().getTime(), new API.OnApiResponse<JSONObject>() {

            @Override
            public void onFailure(API_Error error) {
                new DefaultErrors(mContext, error);
            }

            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject entry = result.getJSONObject("entry");
                    JSONArray treatments = entry.getJSONArray("treatments");
                    for (int i = 0; i < treatments.length(); i++) {
                        JSONObject treatment = treatments.getJSONObject(i);
                        Iterator<String> iter = treatment.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            if (key.equals("name")) {
                                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lparams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.sep_margin_small));
                                TextView tv = new TextView(mContext);
                                tv.setTextAppearance(mContext, R.style.AppTheme_TextView_Link);
                                tv.setLayoutParams(lparams);
                                tv.setText(treatment.get(key).toString());
                                Bundle bundle = new Bundle();
                                bundle.putString("treatment_title", treatment.get(key).toString());
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
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(mContext, Locales.read(mContext, "nice_errors.api_error_description").create(), Toast.LENGTH_LONG).show();
                }
            }
        });

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
                flareDownAPI.users_sign_out(new API.OnApiResponse<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(API_Error error) {
                        Toast.makeText(mContext, "Failed to logout", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        sw_checkinReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mAlarm = new Alarm();
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    Calendar cal = Calendar.getInstance();
                    String currentTime = sdf.format(cal.getTimeInMillis());
                    tv_checkinRemindTime.setText(currentTime);
                    tv_checkinRemindTime.setAlpha((float)1);
                    mAlarm.setId(new Random(Calendar.getInstance().getTimeInMillis()).nextInt());
                    mAlarm.setTime(cal.getTimeInMillis() + getCurrentTimezoneOffset(Calendar.getInstance()));
                    mAlarm.setTitle("checkin_reminder");
                } else {
                    tv_checkinRemindTime.setAlpha((float).20);
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

                alertDialog.setPositiveButton(Locales.read(mContext, "nav.done").create(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, picker.getCurrentHour());
                        cal.set(Calendar.MINUTE, picker.getCurrentMinute());
                        cal.clear(Calendar.SECOND);
                        mAlarm.setTime(cal.getTimeInMillis() + +getCurrentTimezoneOffset(Calendar.getInstance()));
                        tv_checkinRemindTime.setText(sdf.format(cal.getTimeInMillis()));
                        dialog.dismiss();
                    }
                });

                alertDialog.setNegativeButton(Locales.read(mContext, "nav.cancel").create(), new DialogInterface.OnClickListener() {
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
                intent.putExtra("Identifier","policy");
                startActivity(intent);
            }
        });

        tv_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PolicyWebView.class);
                intent.putExtra("Identifier","terms");
                startActivity(intent);
            }
        });

        updateLocales();

    }

    private void addUpdateAlarm(){
        mRealm.beginTransaction();
        Alarm realmAlarm = mRealm.copyToRealmOrUpdate(mAlarm);
        mRealm.commitTransaction();
    }

    private void removeAlarm(){
        mRealm.beginTransaction();
        mRealm.where(Alarm.class).equalTo("title","checkin_reminder").findAll().clear();
        mRealm.commitTransaction();
    }


    public void updateLocales() {
        tv_AccountTitle.setText(Locales.read(mContext, "menu_item_account").createAT());
        tv_EditAccount.setText(Locales.read(mContext, "account.edit").createAT());
        tv_SettingsLogout.setText(Locales.read(mContext, "menu_item_logout").createAT());
        tv_checkinRemindTitle.setText(Locales.read(mContext,"forms.checkin_remind_title").create());
        tv_treatmentRemindTitle.setText(Locales.read(mContext,"forms.treatment_remind_title").create());
        //tv_terms.setText(Locales.read(mContext,"terms_of_service").create());
        //tv_policy.setText(Locales.read(mContext,"privacy_policy").create());

        //If reminder is already set, get it from realm and populate
        if (sw_checkinReminder.isChecked()) { //reminder set
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(mAlarm.getTime() - getCurrentTimezoneOffset(Calendar.getInstance()));
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
            Toast.makeText(this, Locales.read(mContext,"confirmation_messages.settings_saved").create(), Toast.LENGTH_LONG).show();
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void save(){
        manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent;
        if (mAlarm != null) {
            alarmIntent.putExtra("id", mAlarm.getId());
            alarmIntent.putExtra("title", mAlarm.getTitle());
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
                manager.setExact(AlarmManager.RTC_WAKEUP, mAlarm.getTime() - getCurrentTimezoneOffset(Calendar.getInstance()), pendingIntent);
            } else {
                manager.set(AlarmManager.RTC_WAKEUP, mAlarm.getTime() - getCurrentTimezoneOffset(Calendar.getInstance()), pendingIntent);
            }
        }
        else { //delete alarm in realm and remove pending intent
            removeAlarm();
            manager.cancel(pendingIntent);
        }
    }
    public int getCurrentTimezoneOffset(Calendar c) {
        return c.getTimeZone().getOffset(c.getTimeInMillis());
    }
}
