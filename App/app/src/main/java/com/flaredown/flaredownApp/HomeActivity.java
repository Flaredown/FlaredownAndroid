package com.flaredown.flaredownApp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.flaredown.com.flaredown.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.DefaultErrors;
import com.flaredown.flaredownApp.FlareDown.ForceLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity {
    Context mContext;
    API flareDownAPI;

    private static final String DEBUG_TAG = "HomeActivity";

    private ViewPager vp_questions;
    private PagerAdapter questionPagerAdapter;
    private Button bt_nextQuestion;
    private ViewPagerProgress vpp_questionProgress;
    private int current_page = 0;

    private List<ViewPagerFragmentBase> fragment_questions = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        createActivity();
    }

    private void createActivity() {
        mContext = this;
        Styling.setFont();

        flareDownAPI = new API(mContext);
        if(!flareDownAPI.isLoggedIn(false)) {  // Prevent other code running if not logged in.
            new ForceLogin(mContext, flareDownAPI);
            return;
        }

        MainToolbarView mainToolbarView;
        // Checking if user is logged in, otherwise redirect to login screen.



        // FindViews
        mainToolbarView = (MainToolbarView) findViewById(R.id.main_toolbar_view);
        vp_questions = (ViewPager) findViewById(R.id.vp_questionPager);
        bt_nextQuestion = (Button) findViewById(R.id.bt_nextQuestion);
        vpp_questionProgress = (ViewPagerProgress) findViewById(R.id.vpp_questionProgress);

        mainToolbarView.setTitle("September 16");


        try {
            flareDownAPI.entries(API.API_DATE_FORMAT.parse("Sep 16 2015"), new API.OnApiResponseObject() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    try {
                        fragment_questions = createFragments(jsonObject.getJSONObject("entry"));
                        vpp_questionProgress.setNumberOfPages(fragment_questions.size());
                        questionPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragment_questions);
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
                        Toast.makeText(mContext, "ERROR PARSING JSON", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(API.API_Error error) {
                    new DefaultErrors(mContext, error);
                }
            });
        } catch (ParseException e ){ e.printStackTrace(); }



        bt_nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });
    }

    private List<ViewPagerFragmentBase> createFragments(JSONObject entry) throws JSONException{
        List<ViewPagerFragmentBase> fragments = new ArrayList<ViewPagerFragmentBase>();

        JSONObject catalog_definitions = entry.getJSONObject("catalog_definitions");

        Iterator<String> cd_iterator = catalog_definitions.keys();

        while(cd_iterator.hasNext()) {
            String catalogueKey = cd_iterator.next();
            JSONArray catalogue = catalog_definitions.getJSONArray(catalogueKey);


            for(int i = 0; i < catalogue.length(); i++) {
                JSONArray questions = catalogue.getJSONArray(i);
                Checkin_catalogQ_fragment checkin_catalogQ_fragment = new Checkin_catalogQ_fragment();
                checkin_catalogQ_fragment.setQuestion(questions, i + 1, catalogueKey);
                //checkin_catalogQ_fragment.setRetainInstance(true);
                if(!catalogueKey.equals("hbi") && !catalogueKey.equals("rapid3"))
                    fragments.add(checkin_catalogQ_fragment);

            }


        }
        return fragments;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new ForceLogin(mContext, flareDownAPI);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, data.getStringExtra(AddADialogActivity.RESULT), Toast.LENGTH_LONG).show();
    }

    public void nextQuestion() {
        vp_questions.setCurrentItem(vp_questions.getCurrentItem() + 1);
    }

    @Override
    public void onBackPressed() {
        if(vp_questions.getCurrentItem() == 0)
            super.onBackPressed();
        else
            vp_questions.setCurrentItem(vp_questions.getCurrentItem() - 1);
    }

    /**
     * Simple pager adapter, for previewing the questions pages, may need switching out when loading questions from the api.
     */

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<ViewPagerFragmentBase> fragments;
        public ScreenSlidePagerAdapter(FragmentManager fm, List<ViewPagerFragmentBase> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

}
