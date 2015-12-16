package com.flaredown.flaredownApp.Checkin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.flaredown.com.flaredown.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.flaredown.flaredownApp.FlareDown.ResponseReader;
import com.flaredown.flaredownApp.InternetStatusBroadcastReceiver;
import com.flaredown.flaredownApp.PreferenceKeys;
import com.flaredown.flaredownApp.SettingsActivity;
import com.flaredown.flaredownApp.Styling;

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
        SPLASH_SCREEN, CHECKIN
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
                            rl_checkin.setAlpha(0);
                            rl_checkin.setTranslationY(0);
                            rl_checkin.setVisibility(View.VISIBLE);
                            rl_checkin.animate()
                                    .alpha(1)
                                    .translationY(Styling.getInDP(this, 100))
                                    .setDuration(ANIMATION_DURATION)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            rl_checkin.setTranslationY(0);
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
            }
        }
        currentView = showView;
    }


    private static final String DEBUG_TAG = "HomeActivity";

    private ViewPager vp_questions;
    private ScreenSlidePagerAdapter questionPagerAdapter;
    private Button bt_nextQuestion;
    private Button bt_prevQuestion;
    private ViewPagerProgress vpp_questionProgress;
    private LinearLayout ll_splashScreen;
    private RelativeLayout rl_checkin;
    private int current_page = 0;
    private Date dateDisplaying = API.currentDate;
    private InternetStatusBroadcastReceiver internetStatusBroadcastReceiver;
    private Menu menu;
    private JSONObject entriesJSONObject = null;

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

        flareDownAPI = new API(mContext);
        if(!flareDownAPI.isLoggedIn()) {  // Prevent other code running if not logged in.
            new ForceLogin(this, flareDownAPI);
            return;
        }
        initialiseUI();
        if(savedInstanceState != null && savedInstanceState.getString(SI_currentView, "").equals(Views.CHECKIN.toString()) && savedInstanceState.containsKey(SI_entriesEndpoint)){
            if(savedInstanceState.containsKey(SI_entriesEndpoint))
                try {
                    entriesJSONObject = new JSONObject(savedInstanceState.getString(SI_entriesEndpoint));
                    initialisePages(entriesJSONObject);
                } catch(JSONException e) { e.printStackTrace(); }

            setView(Views.CHECKIN, false);
        } else {
            createActivity();
        }
    }

    private void createActivity() {
        final API.OnApiResponse<JSONObject> entriesResponse = new API.OnApiResponse<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                entriesJSONObject = jsonObject;
                initialisePages(jsonObject);
                setView(Views.CHECKIN);
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
        vpp_questionProgress = (ViewPagerProgress) findViewById(R.id.vpp_questionProgress);
        ll_splashScreen = (LinearLayout) findViewById(R.id.ll_splashScreen);
        rl_checkin = (RelativeLayout) findViewById(R.id.rl_checkin);
        final Toolbar mainToolbarView = (Toolbar) findViewById(R.id.toolbar_top);
        TextView title = (TextView) findViewById(R.id.toolbar_title);

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
        vp_questions.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) bt_prevQuestion.setVisibility(View.INVISIBLE);
                else bt_prevQuestion.setVisibility(View.VISIBLE);

                if (fragment_questions.size() - 1 <= position)
                    bt_nextQuestion.setVisibility(View.INVISIBLE);
                else bt_nextQuestion.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
                        PreferenceKeys.log(PreferenceKeys.LOG_V, DEBUG_TAG, "Hiding the keyboard");
                        View currentFocus = ((Activity)mContext).getCurrentFocus();
                        if(currentFocus != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                        }
                    } else { // Show the keyboard
                        PreferenceKeys.log(PreferenceKeys.LOG_V, DEBUG_TAG, "Displaying the keyboard");
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
    private static final String SI_currentView = "current view";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(entriesJSONObject != null)
            outState.putString(SI_entriesEndpoint, entriesJSONObject.toString());
        outState.putString(SI_currentView, currentView.toString());
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
