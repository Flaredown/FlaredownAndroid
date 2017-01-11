package com.flaredown.flaredownApp.Activities.Settings.EditAccount;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.flaredown.flaredownApp.Helpers.APIv2.APIResponse;
import com.flaredown.flaredownApp.Helpers.APIv2.Communicate;
import com.flaredown.flaredownApp.Helpers.APIv2.Error;
import com.flaredown.flaredownApp.Helpers.FlaredownConstants;
import com.flaredown.flaredownApp.Helpers.PreferenceKeys;
import com.flaredown.flaredownApp.Activities.Login_old.ForceLogin;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Profile.Country;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Profile.Profile;
import com.flaredown.flaredownApp.R;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class FragmentEditAccount extends DialogFragment implements View.OnClickListener,Spinner.OnItemSelectedListener,DatePicker.OnDateChangedListener{
    private CheckBox mchkMale;
    private CheckBox mchkFemale;
    private CheckBox mchkOther;
    private CheckBox mchkNotSay;
    private Spinner mCountriesSpinner;
    private Button mbtnSave;
    private Communicate mFlaredownAPI;
    private Context mContext;
    private String mGender;
    private String mDobDay;
    private String mDobMonth;
    private String mDobYear;
    private String mLocation;
    private DatePicker mBirthDatePicker;
    private Profile mCurrentUser;
    private String userID;
    private List<Country> mCountries;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.edit_account_fragment, null);
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
        final LinearLayout llProgress = (LinearLayout) view.findViewById(R.id.llEditAccountProgress);
        final LinearLayout llEditAccount = (LinearLayout) view.findViewById(R.id.llEditAccount);

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

        if(!new Communicate(getActivity()).isCredentialsSaved()) { // Ensure the user is signed in.
            new ForceLogin(getActivity());
        } else {
            SharedPreferences sp = PreferenceKeys.getSharedPreferences(getActivity());
            userID = sp.getString(PreferenceKeys.SP_Av2_USER_ID, null);
            mFlaredownAPI = new Communicate(mContext);
            //Get current_user information
            mFlaredownAPI.getProfile(userID, new APIResponse<Profile, com.flaredown.flaredownApp.Helpers.APIv2.Error>() {
                @Override
                public void onSuccess(Profile result) {
                    mCurrentUser = result;
                    updateViews();
                    llProgress.setVisibility(View.GONE);
                    llEditAccount.setVisibility(View.VISIBLE);                }

                @Override
                public void onFailure(Error result) {
                    Toast.makeText(getActivity(),getResources().getString(R.string.locales_nice_errors_general_error),Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        /*if (mFlaredownAPI.apiFromCacheIsDirty("me")){
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
        }*/

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.chkMale:
                uncheckBoxes();
                mchkMale.setChecked(true);
                mCurrentUser.setSex_id(FlaredownConstants.PROFILE_SEX_ID_MALE);
                break;
            case R.id.chkFemale:
                uncheckBoxes();
                mchkFemale.setChecked(true);
                mCurrentUser.setSex_id(FlaredownConstants.PROFILE_SEX_ID_FEMALE);
                break;
            case R.id.chkOther:
                uncheckBoxes();
                mchkOther.setChecked(true);
                mCurrentUser.setSex_id(FlaredownConstants.PROFILE_SEX_ID_OTHER);
                break;
            case R.id.chkPreferNotToStay:
                uncheckBoxes();
                mchkNotSay.setChecked(true);
                mCurrentUser.setSex_id(FlaredownConstants.PROFILE_SEX_ID_DOESNT_SAY);
                break;
            case R.id.editAccountSave:
                //show saving spinner
                final ProgressDialog progress = new ProgressDialog(mContext);
                progress.setMessage(getResources().getString(R.string.locales_nav_saving));
                progress.setCancelable(false);
                progress.show();

                //update via api
                mFlaredownAPI.putProfile(mCurrentUser, new APIResponse<JSONObject, Error>() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        Toast.makeText(mContext, getResources().getString(R.string.locales_settings_saved), Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }

                    @Override
                    public void onFailure(Error result) {
                        Toast.makeText(mContext, getResources().getString(R.string.locales_nice_errors_general_error_description), Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
                });
                break;
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
        switch(mCurrentUser.getSex_id()){
            case FlaredownConstants.PROFILE_SEX_ID_MALE:
                mchkMale.setChecked(true);
                break;
            case FlaredownConstants.PROFILE_SEX_ID_FEMALE:
                mchkFemale.setChecked(true);
                break;
            case FlaredownConstants.PROFILE_SEX_ID_OTHER:
                mchkOther.setChecked(true);
                break;
            case FlaredownConstants.PROFILE_SEX_ID_DOESNT_SAY:
                mchkNotSay.setChecked(true);
                break;
        }

        //set birth date
        int year = mCurrentUser.getBirth_date().get(Calendar.YEAR);
        int month = mCurrentUser.getBirth_date().get(Calendar.MONTH);
        int day = mCurrentUser.getBirth_date().get(Calendar.DAY_OF_MONTH);
        mBirthDatePicker.updateDate(year, month, day);

        //set Countries

        mFlaredownAPI.getCountries(new APIResponse<List<Country>, Error>() {
            @Override
            public void onSuccess(List<Country> result) {
                mCountries = result;
                ArrayAdapter<String> countriesAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item);
                int userCountryID = 0;
                for (int i = 0; i < result.size(); i++) {
                    countriesAdapter.add(result.get(i).getName());
                    if (result.get(i).getId().toUpperCase().equals(mCurrentUser.getCountry_id().toUpperCase())){
                        userCountryID = i;
                    }
                }
                mCountriesSpinner.setAdapter(countriesAdapter);
                mCountriesSpinner.setSelection(userCountryID);
            }

            @Override
            public void onFailure(Error result) {

            }
        });
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
/*        mDobYear = String.valueOf(year);
        mDobDay = String.valueOf(day);
        if (month < 9){
            mDobMonth = String.valueOf("0" + (month + 1)); //Add 0 to single digits b/c API doesn't like just single digits
        }
        else {
            mDobMonth = String.valueOf(month + 1);
        }*/

        Calendar dob = Calendar.getInstance();
        dob.set(year,month,day);
        mCurrentUser.setBirth_date(dob);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mCurrentUser.setCountry_id(mCountries.get(i).getId());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}