package com.flaredown.flaredownApp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.API_Error;
import com.flaredown.flaredownApp.FlareDown.DefaultErrors;
import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Iterator;

public class FragmentEditAccount extends DialogFragment implements View.OnClickListener,Spinner.OnItemSelectedListener,DatePicker.OnDateChangedListener{
    private TextView mtvCountry;
    private TextView mtvBirthdate;
    private TextView mtvSex;
    private CheckBox mchkMale;
    private CheckBox mchkFemale;
    private CheckBox mchkOther;
    private CheckBox mchkNotSay;
    private Spinner mCountriesSpinner;
    private Button mbtnSave;
    private API mFlaredownAPI;
    private Context mContext;
    private String mGender;
    private String mDobDay;
    private String mDobMonth;
    private String mDobYear;
    private String mLocation;
    private DatePicker mBirthDatePicker;
    private JSONObject mCurrentUser = new JSONObject();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_account, null);
        builder.setView(view);
        mContext = getActivity();

        mchkMale = (CheckBox) view.findViewById(R.id.chkMale);
        mchkFemale = (CheckBox) view.findViewById(R.id.chkFemale);
        mchkOther = (CheckBox) view.findViewById(R.id.chkOther);
        mchkNotSay = (CheckBox) view.findViewById(R.id.chkPreferNotToStay);
        mCountriesSpinner = (Spinner) view.findViewById(R.id.country);
        mBirthDatePicker = (DatePicker) view.findViewById(R.id.birthday);
        mBirthDatePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
        mbtnSave = (Button)view.findViewById(R.id.editAccountSave);
        final LinearLayout llProgress = (LinearLayout) view.findViewById(R.id.llProgress);
        final LinearLayout llEditAccount = (LinearLayout) view.findViewById(R.id.llEditAccount);
        mtvCountry = (TextView) view.findViewById(R.id.tvCountry);
        mtvBirthdate = (TextView) view.findViewById(R.id.tvBirthdate);
        mtvSex = (TextView) view.findViewById(R.id.tvSex);

        llEditAccount.setVisibility(View.GONE);
        llProgress.setVisibility(View.VISIBLE);

        //Click Handlers
        mchkMale.setOnClickListener(this);
        mchkFemale.setOnClickListener(this);
        mchkOther.setOnClickListener(this);
        mchkNotSay.setOnClickListener(this);
        mbtnSave.setOnClickListener(this);
        mBirthDatePicker.init(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), this);
        mCountriesSpinner.setOnItemSelectedListener(this);
        mbtnSave.setOnClickListener(this);


        mFlaredownAPI = new API(mContext);
        //Get current_user information
        if (mFlaredownAPI.apiFromCacheIsDirty("me")){
            //Get new api info
            mFlaredownAPI.current_user(new API.OnApiResponse<JSONObject>() {
                @Override
                public void onFailure(API_Error error) {
                    DefaultErrors newError = new DefaultErrors(mContext, error);
                    llProgress.setVisibility(View.GONE);
                    llEditAccount.setVisibility(View.VISIBLE);
                }

                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        mCurrentUser = result.getJSONObject("current_user");
                        JSONObject settings = mCurrentUser.getJSONObject("settings");
                        mGender = settings.getString("sex").toLowerCase();
                        mDobYear = settings.getString("dobYear").toLowerCase();
                        mDobMonth = settings.getString("dobMonth").toLowerCase();
                        mDobDay = settings.getString("dobDay").toLowerCase();
                        mLocation = settings.getString("location").toLowerCase();
                        updateViews();
                        llProgress.setVisibility(View.GONE);
                        llEditAccount.setVisibility(View.VISIBLE);
                        mFlaredownAPI.cacheAPI("me",result.toString());
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            //Retrieve from cache
            try {
                JSONObject me = new JSONObject(mFlaredownAPI.getAPIFromCache("me"));
                mCurrentUser = me.getJSONObject("current_user");
                JSONObject settings = mCurrentUser.getJSONObject("settings");
                mGender = settings.getString("sex").toLowerCase();
                mDobYear = settings.getString("dobYear").toLowerCase();
                mDobMonth = settings.getString("dobMonth").toLowerCase();
                mDobDay = settings.getString("dobDay").toLowerCase();
                mLocation = settings.getString("location").toLowerCase();
                updateViews();
                llProgress.setVisibility(View.GONE);
                llEditAccount.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        applyLocales();

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.chkMale:
                uncheckBoxes();
                mchkMale.setChecked(true);
                mGender = "Male";
                break;
            case R.id.chkFemale:
                uncheckBoxes();
                mchkFemale.setChecked(true);
                mGender = "Female";
                break;
            case R.id.chkOther:
                uncheckBoxes();
                mchkOther.setChecked(true);
                mGender = "Other";
                break;
            case R.id.chkPreferNotToStay:
                uncheckBoxes();
                mchkNotSay.setChecked(true);
                mGender = "Prefer not to say";
                break;
            case R.id.editAccountSave:
                //show saving spinner
                final ProgressDialog progress = new ProgressDialog(mContext);
                progress.setMessage(Locales.read(mContext,"forms.saving").createAT());
                        progress.setCancelable(false);
                progress.show();
                //add values to json
                updateJSON();
                //update via api
                mFlaredownAPI.updateUser(mCurrentUser, new API.OnApiResponse<JSONObject>() {
                    @Override
                    public void onFailure(API_Error error) {
                        Toast.makeText(mContext, Locales.read(mContext,"nice_errors.general_error_description").create(), Toast.LENGTH_LONG).show();
                        API_Error api_error = new API_Error();
                        progress.dismiss();
                    }

                    @Override
                    public void onSuccess(JSONObject result) {
                        //Clear API cache so changes will be retrieved fresh
                        mFlaredownAPI.cacheAPI("me","");
                        progress.dismiss();
                        dismiss();
                        Toast.makeText(mContext, Locales.read(mContext,"confirmation_messages.settings_saved").create(), Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }
    }

    private void applyLocales(){
        mtvCountry.setText(Locales.read(mContext,"onboarding.location").createAT());
        mtvBirthdate.setText(Locales.read(mContext,"onboarding.dob").createAT());
        mtvSex.setText(Locales.read(mContext,"onboarding.sex").createAT());
        mchkMale.setText(Locales.read(mContext,"onboarding.sex_options.male").createAT());
        mchkFemale.setText(Locales.read(mContext,"onboarding.sex_options.female").createAT());
        mchkOther.setText(Locales.read(mContext,"onboarding.sex_options.other").createAT());
        mchkNotSay.setText(Locales.read(mContext,"onboarding.sex_options.unspecified").createAT());
        mbtnSave.setText(Locales.read(mContext,"forms.save").createAT());

    }
    private void updateJSON(){
        try {
            JSONObject settings = mCurrentUser.getJSONObject("settings");
            settings.put("sex",mGender);
            settings.put("dobDay",mDobDay);
            settings.put("dobMonth",mDobMonth);
            settings.put("dobYear",mDobYear);
            settings.put("location",mLocation);
            mCurrentUser.put("settings",settings);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void uncheckBoxes(){
        mchkMale.setChecked(false);
        mchkFemale.setChecked(false);
        mchkOther.setChecked(false);
        mchkNotSay.setChecked(false);
    }

    private void updateViews(){
        //Set Gender Checks
        switch(mGender){
            case "male":
                mchkMale.setChecked(true);
                break;
            case "female":
                mchkFemale.setChecked(true);
                break;
            case "other":
                mchkOther.setChecked(true);
                break;
            case "prefer not to say":
                mchkNotSay.setChecked(true);
                break;
        }

        //set birth date
        int year = Integer.parseInt(mDobYear);
        int month = Integer.parseInt(mDobMonth);
        int day = Integer.parseInt(mDobDay);
        mBirthDatePicker.updateDate(year, month - 1, day);

        //set Country
        try {
            ArrayAdapter<String> countries = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item);
            String json = Locales.read(mContext,"location_options").create();
            JSONObject locations = new JSONObject(json);
            Iterator<String> iter = locations.keys();
            int x = 0;
            int y = 0;
            while (iter.hasNext()){
                String key = iter.next();
                countries.add(locations.getString(key));
                if (locations.getString(key).toLowerCase().equals(mLocation.toLowerCase())){
                     y = x;
                }
                x++;
            }
            mCountriesSpinner.setAdapter(countries);
            mCountriesSpinner.setSelection(y);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
        mDobYear = String.valueOf(year);
        mDobDay = String.valueOf(day);
        mDobMonth = String.valueOf(month+1);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mLocation = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}