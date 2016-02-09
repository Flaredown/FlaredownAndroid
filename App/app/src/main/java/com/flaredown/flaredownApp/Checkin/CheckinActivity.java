package com.flaredown.flaredownApp.Checkin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.API_Error;
import com.flaredown.flaredownApp.FlareDown.DefaultErrors;
import com.flaredown.flaredownApp.FlareDown.ForceLogin;
import com.flaredown.flaredownApp.FlareDown.Locales;
import com.flaredown.flaredownApp.FlareDown.ResponseReader;
import com.flaredown.flaredownApp.InternetStatusBroadcastReceiver;
import com.flaredown.flaredownApp.SettingsActivity;
import com.flaredown.flaredownApp.Styling;
import com.flaredown.flaredownApp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckinActivity extends AppCompatActivity {
    Context mContext;
    API flareDownAPI;

    private enum Views {
        SPLASH_SCREEN, CHECKIN, NOT_CHECKED_IN_YET;
    }
    private Views currentView = null;
    private static final int ANIMATION_DURATION = 250;
    private void setView(Views showView) { setView(showView, true); }
    private void setView(Views showView, boolean animate) {
        if(showView != currentView) {
            if (currentView != null) {
                // Hide animations
                switch (currentView) {
                    case CHECKIN:
                        if(animate) {
                            rl_checkin.setAlpha(1);
                            rl_checkin.setTranslationY(0);
                            rl_checkin.setVisibility(View.VISIBLE);
                            rl_checkin.animate()
                                    .alpha(0)
                                    .translationY(Styling.getInDP(this, 100))
                                    .setDuration(ANIMATION_DURATION)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            rl_checkin.setVisibility(View.GONE);
                                            rl_checkin.setTranslationY(0);
                                            rl_checkin.setAlpha(1);
                                        }
                                    });
                        } else
                            rl_checkin.setVisibility(View.GONE);
                        break;
                    case SPLASH_SCREEN:
                        if(animate) {
                            ll_splashScreen.setAlpha(1);
                            ll_splashScreen.setVisibility(View.VISIBLE);
                            ll_splashScreen.animate()
                                    .alpha(0)
                                    .translationY(-Styling.getInDP(this, 100))
                                    .setDuration(ANIMATION_DURATION)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            ll_splashScreen.setVisibility(View.GONE);
                                            ll_splashScreen.setTranslationY(0);
                                            ll_splashScreen.setAlpha(1);
                                        }
                                    });
                        } else
                            ll_splashScreen.setVisibility(View.GONE);
                        break;
                    case NOT_CHECKED_IN_YET:
                        if(animate) {
                            ll_not_checked_in.setAlpha(1);
                            ll_not_checked_in.setTranslationY(0);
                            ll_not_checked_in.setVisibility(View.VISIBLE);
                            ll_not_checked_in.animate()
                                    .alpha(0)
                                    .translationY(Styling.getInDP(this, 100))
                                    .setDuration(ANIMATION_DURATION)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            ll_not_checked_in.setVisibility(View.GONE);
                                            ll_not_checked_in.setTranslationY(0);
                                            ll_not_checked_in.setAlpha(1);
                                        }
                                    });
                        } else {
                            ll_not_checked_in.setVisibility(View.GONE);
                        }
                        break;
                }
            }
            // Show animations
            switch (showView) {
                case CHECKIN:
                    if(animate) {
                        rl_checkin.setAlpha(0);
                        rl_checkin.setTranslationY(Styling.getInDP(this, 100));
                        rl_checkin.setVisibility(View.VISIBLE);
                        rl_checkin.animate()
                                .alpha(1)
                                .translationY(0)
                                .setDuration(ANIMATION_DURATION);
                    } else
                        rl_checkin.setVisibility(View.VISIBLE);
                    break;
                case SPLASH_SCREEN:
                    if(animate) {
                        ll_splashScreen.setAlpha(0);
                        ll_splashScreen.setVisibility(View.VISIBLE);
                        ll_splashScreen.animate()
                                .alpha(1)
                                .translationY(Styling.getInDP(this, 100));
                    } else
                        ll_splashScreen.setVisibility(View.VISIBLE);
                    break;
                case NOT_CHECKED_IN_YET:
                    if(animate) {
                        ll_not_checked_in.setAlpha(0);
                        ll_not_checked_in.setTranslationY(Styling.getInDP(this, 100));
                        ll_not_checked_in.setVisibility(View.VISIBLE);
                        ll_not_checked_in.animate()
                                .alpha(1)
                                .translationY(0)
                                .setDuration(ANIMATION_DURATION);
                    } else {
                        ll_not_checked_in.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
        currentView = showView;
    }


    private static final String DEBUG_TAG = "HomeActivity";

    private ViewPager vp_questions;
    private ScreenSlidePagerAdapter questionPagerAdapter;
    private Button bt_nextQuestion;
    private Button bt_prevQuestion;
    private Button bt_submitCheckin;
    private LinearLayout ll_not_checked_in;
    private Button bt_not_checked_in_checkin;
    private TextView tv_not_checked_in_checkin;
    private ViewPagerProgress vpp_questionProgress;
    private LinearLayout ll_splashScreen;
    private RelativeLayout rl_checkin;
    private int current_page = 0;
    private Date dateDisplaying = API.currentDate;
    private InternetStatusBroadcastReceiver internetStatusBroadcastReceiver;
    private Menu menu;
    private JSONObject entriesJSONObject = null;
    private JSONObject responseJSONObject = new JSONObject();
    private List<ViewPagerFragmentBase> fragment_questions = new ArrayList<>();

    public List<ViewPagerFragmentBase> getFragmentQuestions() {
        return fragment_questions;
    }
    public ScreenSlidePagerAdapter getScreenSlidePagerAdapter() {
        return questionPagerAdapter;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;
        Styling.setFont();

        // Set default structure to the responseJSONObject.
        try {
            responseJSONObject.put("responses", new JSONArray());
            responseJSONObject.put("tags", new JSONArray());
            responseJSONObject.put("treatments", new JSONArray());
        } catch (JSONException e) { e.printStackTrace(); }

        flareDownAPI = new API(mContext);
        if(!flareDownAPI.isLoggedIn()) {  // Prevent other code running if not logged in.
            new ForceLogin(this, flareDownAPI);
            return;
        }

        initialiseUI();
        if (savedInstanceState != null && savedInstanceState.getString(SI_currentView, "").equals(Views.CHECKIN.toString()) && savedInstanceState.containsKey(SI_entriesEndpoint)) {
            if (savedInstanceState.containsKey(SI_entriesEndpoint))
                try {
                    entriesJSONObject = new JSONObject(savedInstanceState.getString(SI_entriesEndpoint));
                    initialisePages(entriesJSONObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            if (savedInstanceState.containsKey(SI_responseJson))
                try {
                    responseJSONObject = new JSONObject(savedInstanceState.getString(SI_responseJson));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            setView(Views.CHECKIN, false);
        } else {
            createActivity();
        }

        checkMinimumVersion();
    }

    private void checkMinimumVersion() {
        //Validate minimum client version
        flareDownAPI.getMinimumClient(new API.OnApiResponse<JSONObject>() {
            @Override
            public void onFailure(API_Error error) {
                //unable to get min version from api, do nothing
            }

            @Override
            public void onSuccess(JSONObject result) {
                try {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    JSONObject android = result.getJSONObject("android");
                    String version = android.getString("major") + "." + android.getString("minor");
                    Double minVersion = Double.parseDouble(version);

                    if (pInfo.versionCode <= minVersion){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setTitle(Locales.read(mContext,"nice_errors.minimum_client_error").create());
                        dialog.setCancelable(false);
                        dialog.setMessage(Locales.read(mContext,"nice_errors.minimum_client_error_description").create());
                        dialog.setPositiveButton(Locales.read(mContext,"nav.update").create(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(Locales.read(mContext,"URI.android_app_uri").create()));
                                startActivity(intent);
                                finish();
                            }
                        });
                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createActivity() {
        final API.OnApiResponse<JSONObject> entriesResponse = new API.OnApiResponse<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                entriesJSONObject = jsonObject;
                initialisePages(jsonObject);
                setView(Views.NOT_CHECKED_IN_YET);
            }

            @Override
            public void onFailure(API_Error error) {
                new DefaultErrors(mContext, error);
            }
        };

        if(!flareDownAPI.checkInternet()) {
            final TextView tv_noInternetConnection = (TextView) findViewById(R.id.tv_noInternetConnection);
            tv_noInternetConnection.setVisibility(View.VISIBLE);
            internetStatusBroadcastReceiver.addOnConnect(new Runnable() {
                @Override
                public void run() {
                    tv_noInternetConnection.setVisibility(View.GONE);
                    flareDownAPI.entries(dateDisplaying, entriesResponse);
                }
            });
        } else
            flareDownAPI.entries(dateDisplaying, entriesResponse);
    }

    private void initialiseUI() {
        // Find the views.
        vp_questions = (ViewPager) findViewById(R.id.vp_questionPager);
        bt_nextQuestion = (Button) findViewById(R.id.bt_nextQuestion);
        bt_prevQuestion = (Button) findViewById(R.id.bt_prevQuestion);
        bt_submitCheckin = (Button) findViewById(R.id.bt_submitCheckin);
        vpp_questionProgress = (ViewPagerProgress) findViewById(R.id.vpp_questionProgress);
        ll_splashScreen = (LinearLayout) findViewById(R.id.ll_splashScreen);
        rl_checkin = (RelativeLayout) findViewById(R.id.rl_checkin);
        ll_not_checked_in = (LinearLayout) findViewById(R.id.ll_not_checked_in);
        tv_not_checked_in_checkin = (TextView) findViewById(R.id.tv_not_checked_in_checkin);
        bt_not_checked_in_checkin = (Button) findViewById(R.id.bt_not_checked_in_checkin);
        final Toolbar mainToolbarView = (Toolbar) findViewById(R.id.toolbar_top);
        TextView title = (TextView) findViewById(R.id.toolbar_title);


        bt_not_checked_in_checkin.setText(Locales.read(this, "onboarding.checkin").createAT());
        tv_not_checked_in_checkin.setText(Locales.read(this, "you_havent_checked_in_yet").createAT());

        // Set up the toolbar.
        title.setText(Styling.displayDateLong(dateDisplaying));
        setSupportActionBar(mainToolbarView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setView(Views.SPLASH_SCREEN, false);

        bt_nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });
        bt_prevQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousQuestion();
            }
        });
        bt_submitCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCheckin();
            }
        });
        vp_questions.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position <= 0) {
                    bt_prevQuestion.setVisibility(View.INVISIBLE);
                    bt_submitCheckin.setVisibility(View.GONE);
                    bt_nextQuestion.setVisibility(View.VISIBLE);
                } else if (position >= fragment_questions.size() - 1) {
                    bt_nextQuestion.setVisibility(View.GONE);
                    bt_prevQuestion.setVisibility(View.VISIBLE);
                    bt_submitCheckin.setVisibility(View.VISIBLE);
                } else {
                    bt_submitCheckin.setVisibility(View.GONE);
                    bt_nextQuestion.setVisibility(View.VISIBLE);
                    bt_prevQuestion.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bt_not_checked_in_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(Views.CHECKIN);
            }
        });
    }

    private void initialisePages(JSONObject entrys) {
        try {
            fragment_questions = createFragments(entrys.getJSONObject("entry"));
            vpp_questionProgress.setNumberOfPages(fragment_questions.size());
            questionPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragment_questions);
            questionPagerAdapter.setOnPageCountChange(new OnPageCountListener() {
                @Override
                public void onPageCountChange(int size) {
                    vpp_questionProgress.setNumberOfPages(size);
                }
            });
            vp_questions.setAdapter(questionPagerAdapter);
            vp_questions.addOnPageChangeListener(vpp_questionProgress);
            vp_questions.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    ViewPagerFragmentBase currentPageFragment = fragment_questions.get(current_page);
                    ViewPagerFragmentBase futurePageFragment = fragment_questions.get(position);

                    // Hide/Show Keyboard depending on page contents.
                    if(!futurePageFragment.hasFocusEditText()) { // Hide the keyboard
                        View currentFocus = ((Activity)mContext).getCurrentFocus();
                        if(currentFocus != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                        }
                    } else { // Show the keyboard
                        futurePageFragment.focusEditText();
                    }






                    currentPageFragment.onPageExit();
                    futurePageFragment.onPageEnter();
                    current_page = position;
                }
                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } catch (JSONException e) {
            API_Error apiError = new API_Error();
            DefaultErrors defaultErrors = new DefaultErrors(this, new API_Error().setStatusCode(500));
        }
    }

    private void submitCheckin() {
        final CheckinActivity activity = this;
        flareDownAPI.submitEntry(dateDisplaying, responseJSONObject, new API.OnApiResponse<JSONObject>() {
            @Override
            public void onFailure(API_Error error) {
                new DefaultErrors(activity, error);
                Toast.makeText(activity, "Checkin submition failed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(JSONObject result) {
                Toast.makeText(activity, "Checkin submission was a success.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private JSONObject getResponse() {
        try {
            JSONObject output = new JSONObject();
            JSONArray responses = new JSONArray();
            for (ViewPagerFragmentBase fragment_question : fragment_questions) {
                JSONArray fragmentResponse = fragment_question.getResponse();
                //Toast.makeText(this, fragmentResponse.toString(), Toast.LENGTH_SHORT).show();
                for(int i = 0; i < fragmentResponse.length(); i++) {
                    responses.put(fragmentResponse.get(i));
                }
            }
            output.put("responses", responses);
            return output;
        } catch (JSONException e) {
            return null;
        }
    }

    private List<ViewPagerFragmentBase> createFragments(JSONObject entry) throws JSONException{
        List<ViewPagerFragmentBase> fragments = new ArrayList<>();
        JSONObject catalog_definitions = entry.getJSONObject("catalog_definitions"); // The question descriptions.
        ResponseReader responses = new ResponseReader(entry.getJSONArray("responses"));
        Iterator<String> cd_iterator = catalog_definitions.keys(); // Used to iterate through the catalogues.

        while(cd_iterator.hasNext()) {
            String catalogueKey = cd_iterator.next();
            JSONArray catalogue = catalog_definitions.getJSONArray(catalogueKey);

            if(!responses.isEmpty()) {
                for(int i = 0; i < catalogue.length(); i++) {
                    JSONArray ja = catalogue.getJSONArray(i);
                    for(int j = 0; j < ja.length(); j++) {
                        JSONObject question = ja.getJSONObject(j);
                        String response = responses.getResponse(catalogueKey, question.getString("name"));
                        if (response != "")
                            question.put("response", response);
                    }
                }
            }

            if(catalogueKey.equals("symptoms") || catalogueKey.equals("conditions") || catalogueKey.equals("treatments")) {
                Checkin_catalogQ_fragment checkin_catalogQ_fragment = new Checkin_catalogQ_fragment();
                checkin_catalogQ_fragment.setQuestions(catalogue, 0, catalogueKey);
                fragments.add(checkin_catalogQ_fragment);
            } else {
                for (int i = 0; i < catalogue.length(); i++) { // Display each question on a separate display.
                    JSONArray questions = new JSONArray();
                    questions.put(catalogue.getJSONArray(i));
                    Checkin_catalogQ_fragment checkin_catalogQ_fragment = new Checkin_catalogQ_fragment();
                    checkin_catalogQ_fragment.setQuestions(questions, i + 1, catalogueKey);
                    fragments.add(checkin_catalogQ_fragment);

                }
            }


        }
        return fragments;
    }


    private static final String SI_entriesEndpoint = "entries endpoint string";
    private static final String SI_responseJson = "responseJson";
    private static final String SI_currentView = "current view";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(entriesJSONObject != null)
            outState.putString(SI_entriesEndpoint, entriesJSONObject.toString());
        outState.putString(SI_currentView, currentView.toString());
        outState.putString(SI_responseJson, responseJSONObject.toString());
    }

    public void updateResponseJson(ViewPagerFragmentBase.Trackable trackable, String questionName, Object value) {
        try {
            JSONArray responses = responseJSONObject.getJSONArray("responses");
            for(int i = 0; i < responses.length(); i++) {
                JSONObject response = responses.getJSONObject(i);
                if(response.getString("name").equals(questionName) && response.getString("catalog").equals(trackable.catalogue)) {
                    response.put("value", value);
                    return;
                }
            }

            JSONObject response = new JSONObject();
            response.put("name", questionName);
            response.put("catalog", trackable.catalogue);
            response.put("value", value);
            responses.put(response);

        } catch (JSONException e) { e.printStackTrace(); }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new ForceLogin(this, flareDownAPI);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar summary_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if(data != null && data.hasExtra(AddEditableActivity.RESULT))
            Toast.makeText(this, data.getStringExtra(AddEditableActivity.RESULT), Toast.LENGTH_LONG).show();*/
        if(onActivityResultListener != null) {
            onActivityResultListener.onActivityResult(requestCode, resultCode, data);
            onActivityResultListener = null;
        }
    }

    private OnActivityResultListener onActivityResultListener = null;
    public void setOnActivityResultListener(OnActivityResultListener onActivityResultListener){
        this.onActivityResultListener = onActivityResultListener;
    }
    public interface OnActivityResultListener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }


    public void nextQuestion() {
        int index = vp_questions.getCurrentItem() + 1;
        if(index < fragment_questions.size())
            vp_questions.setCurrentItem(vp_questions.getCurrentItem() + 1);
    }
    public void previousQuestion() {
        int index = vp_questions.getCurrentItem() - 1;
        if(index >= 0) {
             vp_questions.setCurrentItem(index);
        }
    }

    @Override
    public void onBackPressed() {
        if(vp_questions.getCurrentItem() == 0)
            super.onBackPressed();
        else
            previousQuestion();
    }

    /**
     * Simple pager adapter, for previewing the questions pages, may need switching out when loading questions from the api.
     */

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<ViewPagerFragmentBase> fragments;
        private OnPageCountListener onPageCountListener;
        public ScreenSlidePagerAdapter(FragmentManager fm, List<ViewPagerFragmentBase> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        public void addView(ViewPagerFragmentBase fragment, int index) {
            fragments.add(index, fragment);
            notifyDataSetChanged();
            onPageCountChange();

        }

        public void removeView(int index) {
            fragments.remove(index);
            notifyDataSetChanged();
            onPageCountChange();
        }

        public void setOnPageCountChange(OnPageCountListener onPageCountListener) {
            this.onPageCountListener = onPageCountListener;
        }
        private void onPageCountChange() {
            if(this.onPageCountListener != null)
                onPageCountListener.onPageCountChange(fragments.size());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
    public interface OnPageCountListener{
        void onPageCountChange(int size);
    }
}
