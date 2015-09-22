package com.flaredown.flaredownApp;

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

    private ViewPager vp_questions;
    private PagerAdapter questionPagerAdapter;
    private Button bt_nextQuestion;
    private ViewPagerProgress vpp_questionProgress;

    private List<Fragment> fragment_questions = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        mContext = this;
        Styling.setFont();

        flareDownAPI = new API(mContext);
        super.onCreate(savedInstanceState);
        if(!flareDownAPI.isLoggedIn(false)) {  // Prevent other code running if not logged in.
            new ForceLogin(mContext, flareDownAPI);
            return;
        }

        MainToolbarView mainToolbarView;
        // Checking if user is logged in, otherwise redirect to login screen.

        setContentView(R.layout.activity_home);

        // FindViews
        mainToolbarView = (MainToolbarView) findViewById(R.id.main_toolbar_view);
        vp_questions = (ViewPager) findViewById(R.id.vp_questionPager);
        bt_nextQuestion = (Button) findViewById(R.id.bt_nextQuestion);
        vpp_questionProgress = (ViewPagerProgress) findViewById(R.id.vpp_questionProgress);

        mainToolbarView.setTitle("July 13");


        try {
            flareDownAPI.entries(API.API_DATE_FORMAT.parse("Sep 16 2015"), new API.OnApiResponse() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    try {
                        fragment_questions = createFragments(jsonObject.getJSONObject("entry"));
                        vpp_questionProgress.setNumberOfPages(fragment_questions.size());
                        questionPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragment_questions);
                        vp_questions.setAdapter(questionPagerAdapter);
                        vp_questions.addOnPageChangeListener(vpp_questionProgress);
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

    private List<Fragment> createFragments(JSONObject entry) throws JSONException{
        List<Fragment> fragments = new ArrayList<Fragment>();

        JSONObject catalog_definitions = entry.getJSONObject("catalog_definitions");

        Iterator<String> cd_iterator = catalog_definitions.keys();

        while(cd_iterator.hasNext()) {
            String catalogueKey = cd_iterator.next();
            JSONArray catalogue = catalog_definitions.getJSONArray(catalogueKey);


            for(int i = 0; i < catalogue.length(); i++) {
                JSONArray questions = catalogue.getJSONArray(i);
                Checkin_catalogQ_fragment checkin_catalogQ_fragment = new Checkin_catalogQ_fragment();
                checkin_catalogQ_fragment.setQuestion(questions, i + 1, catalogueKey);
                checkin_catalogQ_fragment.setRetainInstance(true);
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
        private List<Fragment> fragments;
        public ScreenSlidePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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
