package com.flaredown.flaredownApp;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.flaredown.com.flaredown.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.ForceLogin;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity {
    Context mContext;
    API flareDownAPI;

    private static final int NUMBER_PAGES = 5;
    private ViewPager vp_questions;
    private PagerAdapter questionPagerAdapter;
    private Button bt_nextQuestion;
    private ViewPagerProgress vpp_questionProgress;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        Styling.setFont();
        flareDownAPI = new API(mContext);
        MainToolbarView mainToolbarView;
        super.onCreate(savedInstanceState);
        // Checking if user is logged in, otherwise redirect to login screen.

        //new ForceLogin(mContext, flareDownAPI);
        setContentView(R.layout.activity_home);

        // FindViews
        mainToolbarView = (MainToolbarView) findViewById(R.id.main_toolbar_view);
        vp_questions = (ViewPager) findViewById(R.id.vp_questionPager);
        bt_nextQuestion = (Button) findViewById(R.id.bt_nextQuestion);
        vpp_questionProgress = (ViewPagerProgress) findViewById(R.id.vpp_questionProgress);

        mainToolbarView.setTitle("July 13");

        List<Fragment> fragments = new ArrayList<Fragment>();

        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());
        fragments.add(new HomeFragment());

        vpp_questionProgress.setNumberOfPages(fragments.size());

        questionPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments);
        vp_questions.setAdapter(questionPagerAdapter);
        vp_questions.addOnPageChangeListener(vpp_questionProgress);


        bt_nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });


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
     * Simple pager adapter, for previewing the question pages, may need switching out when loading questions from the api.
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
