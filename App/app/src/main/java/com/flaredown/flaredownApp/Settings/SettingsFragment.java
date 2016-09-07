package com.flaredown.flaredownApp.Settings;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.flaredown.flaredownApp.Helpers.APIv2.ErrorDialog;
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
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class SettingsFragment extends Fragment {
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
    Observable<List<Treatment>> mObservable;
    Subscriber<List<Treatment>> mSubscriber;

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p/>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment,container,false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Styling.setFont();
        mRealm = Realm.getDefaultInstance();
        mContext = getContext();

        flareDownAPI = new Communicate(mContext);
        if(!flareDownAPI.isCredentialsSaved()) {  // Prevent other code running if not logged in.
            new ForceLogin(getActivity());
        }

        tv_AccountTitle = (TextView) view.findViewById(R.id.tv_accountTitle);
        tv_EditAccount = (TextView) view.findViewById(R.id.tv_editAccount);
        tv_SettingsLogout = (TextView) view.findViewById(R.id.tv_settingsLogout);
        tv_checkinRemindTitle = (TextView) view.findViewById(R.id.tv_checkinRemindTitle);
        tv_checkinRemindTime = (TextView) view.findViewById(R.id.tv_checkinRemindTime);
        tv_treatmentRemindTitle = (TextView) view.findViewById(R.id.tv_treatmentRemindTitle);
        sw_checkinReminder = (Switch) view.findViewById(R.id.sw_checkinReminder);
        ll_treatmentReminder = (LinearLayout) view.findViewById(R.id.llTreatmentReminder);
        tv_terms = (TextView) view.findViewById(R.id.terms);
        tv_policy = (TextView) view.findViewById(R.id.privacy_policy);
        llSettingsProgress = (LinearLayout) view.findViewById(R.id.llSettingsProgress);
        rlSettings = (RelativeLayout) view.findViewById(R.id.rlSettings);
        tv_help = (TextView) view.findViewById(R.id.tv_help);

        llSettingsProgress.setVisibility(View.VISIBLE);
        rlSettings.setVisibility(View.GONE);

        //Set Toolbar
        Toolbar mainToolbarView = (Toolbar) view.findViewById(R.id.toolbar_top);
        TextView title = (TextView) view.findViewById(R.id.toolbar_title);
        title.setText(R.string.title_activity_settings);
        //setSupportActionBar(mainToolbarView);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        mObservable = Observable.defer(new Func0<Observable<List<Treatment>>>() {
            @Override
            public Observable<List<Treatment>> call() {
                return Observable.just(mTreatments);
            }
        });

        mSubscriber = new Subscriber<List<Treatment>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                llSettingsProgress.setVisibility(View.GONE);
                rlSettings.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNext(List<Treatment> treatments) {
                showTreatments();
                llSettingsProgress.setVisibility(View.GONE);
                rlSettings.setVisibility(View.VISIBLE);
            }
        };

        getAllTreatments();

        //Listeners
        tv_EditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                FragmentEditAccount frag = new FragmentEditAccount();
                ft.attach(frag);
                frag.show(ft, FlaredownConstants.EDIT_ACCOUNT_FRAGMENT_TITLE);

            }
        });

        tv_SettingsLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAndUnscheduleAllAlarms();
                flareDownAPI.userSignOut();
                new ForceLogin(getActivity());
                //finish();
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

    private void getAllTreatments() {
        flareDownAPI.getTrackings(TrackableType.TREATMENT, Calendar.getInstance(), new APIResponse<Trackings, Error>() {
            @Override
            public void onSuccess(Trackings trackings) {
                List<Integer> ids = new ArrayList<>();
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
                        mObservable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(mSubscriber);
                    }

                    @Override
                    public void onFailure(Error result) {
                        new ErrorDialog(mContext, result).setCancelable(false).show();
                    }
                });
            }

            @Override
            public void onFailure(Error result) {
                new ErrorDialog(mContext, result).setCancelable(false).show();
            }
        });
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
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
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

    private void removeCheckInAlarm(){
        mRealm.beginTransaction();
        mRealm.where(Alarm.class).equalTo(FlaredownConstants.ALARM_TITLE_NAME, FlaredownConstants.ALARM_TITLE_VALUE_CHECKIN_REMINDER).findAll().deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    private void removeAndUnscheduleAllAlarms(){
        RealmQuery query = mRealm.where(Alarm.class);
        RealmResults<Alarm> alarms = query.findAll();
        for (Alarm alarm : alarms){
            //Recreate pending intent and delete
            Intent recreatedIntent = new Intent(getActivity(),AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(getActivity(), alarm.getId(), recreatedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager manager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
            manager.cancel(pi);
        }
        //Remove from Realm
        mRealm.beginTransaction();
        mRealm.delete(Alarm.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id ==android.R.id.home){
            NavUtils.navigateUpFromSameTask(getActivity());
        }

        if (id == R.id.action_save){
            save();
            Toast.makeText(getActivity(), R.string.locales_settings_saved, Toast.LENGTH_LONG).show();
            NavUtils.navigateUpFromSameTask(getActivity());
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
            removeCheckInAlarm();
            manager.cancel(pendingIntent);
        }
    }

}
