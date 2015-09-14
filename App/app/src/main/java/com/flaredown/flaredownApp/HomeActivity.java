package com.flaredown.flaredownApp;

import android.support.annotation.NonNull;
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
import android.widget.ListView;
import android.widget.Toast;

import com.flaredown.flaredownApp.FlareDown.API;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity {
    Context mContext;
    API flareDownAPI;

    private static final int NUMBER_PAGES = 5;
    private ViewPager questionPager;
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

        if(!flareDownAPI.isLoggedIn(false)) {
            PreferenceKeys.log(PreferenceKeys.LOG_I, "HomeActivity", "User not logged in, redirecting to login activity");
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            this.finish();
        }
        setContentView(R.layout.activity_home);

        // FindViews
        mainToolbarView = (MainToolbarView) findViewById(R.id.main_toolbar_view);
        questionPager = (ViewPager) findViewById(R.id.questionPager);
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
        questionPager.setAdapter(questionPagerAdapter);
        questionPager.addOnPageChangeListener(vpp_questionProgress);


        bt_nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });


    }


    public void nextQuestion() {
        questionPager.setCurrentItem(questionPager.getCurrentItem() + 1);
    }

    @Override
    public void onBackPressed() {
        if(questionPager.getCurrentItem() == 0)
            super.onBackPressed();
        else
            questionPager.setCurrentItem(questionPager.getCurrentItem() - 1);
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
