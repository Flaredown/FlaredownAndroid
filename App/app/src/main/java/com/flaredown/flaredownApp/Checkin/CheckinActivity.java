package com.flaredown.flaredownApp.Checkin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flaredown.flaredownApp.Checkin.tags.TagFragment;
import com.flaredown.flaredownApp.Helpers.APIv2.APIResponse;
import com.flaredown.flaredownApp.Helpers.APIv2.Communicate;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.CheckIn;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.Trackable;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.Tag;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings.Tracking;
import com.flaredown.flaredownApp.Helpers.APIv2.Error;
import com.flaredown.flaredownApp.Helpers.APIv2.ErrorDialog;
import com.flaredown.flaredownApp.Helpers.FlaredownConstants;
import com.flaredown.flaredownApp.Helpers.PreferenceKeys;
import com.flaredown.flaredownApp.Helpers.Styling.SnackbarStyling;
import com.flaredown.flaredownApp.Helpers.Styling.Styling;
import com.flaredown.flaredownApp.Login.ForceLogin;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Settings.SettingsActivity;
import com.flaredown.flaredownApp.Toolbars.MainToolbarView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckinActivity extends AppCompatActivity{
    Communicate API;
    private static String DEBUG_KEY = "CHECKIN";
    private Calendar checkinDate = null;
    private boolean isLoadingCheckin = false;
    private boolean activityPaused = false;
    private GestureDetectorCompat gestureDetector;

    /**
     * Get the current check in for the activity.
     * @return The current check in for the activity.
     */
    public CheckIn getCheckIn() {
        return checkIn;
    }

    private CheckIn checkIn;

    /*
        View Variables.
     */
    private ImageButton bt_nextQuestion;
    private ImageButton bt_prevQuestion;
    private Button bt_submitCheckin;
    private Button bt_not_checked_in_checkin;

    private TextView tv_not_checked_in_checkin;

    private MainToolbarView mainToolbarView;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private LinearLayout ll_not_checked_in;
    private LinearLayout ll_splashScreen;
    private RelativeLayout rl_checkin;
    private FrameLayout fl_checkin_summary;
    private Fragment f_checkin_sumary;

    private ViewPager vp_questions;
    private ViewPagerAdapter vpa_questions;

    private OnActivityResultListener onActivityResultListener;
    private ArrayList<ActivityPauseEventListener> activityPauseEventListeners = new ArrayList<>();

    /*
        Instance constant arguments.
     */
    private static final String SI_CHECKIN = "entries endpoint";
    private static final String SI_CURRENT_VIEW = "current view";
    private static final String SI_CHECKIN_DATE = "checkin date";
    private static final String SI_CHECKIN_PAGE_NUMBER = "checkin page number";
    private static final String KEY_FIRST_CHECKIN = "first_checkin";

    private enum Views {
        SPLASH_SCREEN, CHECKIN, NOT_CHECKED_IN_YET, SUMMARY
    }

    private Views currentView = null;
    private Integer currentQuestionPage = 0;
    private static final int ANIMATION_DURATION = 250;
    private boolean setViewAnimationInProgress = false;
    private class SetViewQueueItem {
        private Views views;
        private boolean animate;
        SetViewQueueItem(Views views, boolean animate) {
            this.views = views;
            this.animate = animate;
        }

        public Views getViews() {
            return views;
        }

        public boolean getAnimate() {
            return animate;
        }
    }
    private LinkedBlockingQueue<SetViewQueueItem> setViewQueueItems = new LinkedBlockingQueue<>();
    private void setViewAnimationComplete() {
        setViewAnimationInProgress = false;
        if(setViewQueueItems.size() > 0) {
            SetViewQueueItem setViewQueueItem = setViewQueueItems.poll();
            setView(setViewQueueItem.getViews(), setViewQueueItem.getAnimate());
        }

    }
    private void setView(Views showView) { setView(showView, true); }
    private void setView(Views showView, boolean animate) {
        if(setViewAnimationInProgress) {
            try {
                setViewQueueItems.put(new SetViewQueueItem(showView, animate));
                return;
            } catch (InterruptedException e){}
        }
        if(showView != currentView) {
            if(animate) setViewAnimationInProgress = true;
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
                    case SUMMARY:
                        if(animate) {
                            fl_checkin_summary.setAlpha(1);
                            fl_checkin_summary.setTranslationY(0);
                            fl_checkin_summary.setVisibility(View.VISIBLE);
                            fl_checkin_summary.animate()
                                    .alpha(0)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            fl_checkin_summary.setVisibility(View.GONE);
                                            fl_checkin_summary.setAlpha(1);
                                        }
                                    });
                        } else
                            fl_checkin_summary.setVisibility(View.GONE);
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
                                .setDuration(ANIMATION_DURATION)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                setViewAnimationComplete();
                            }
                        });
                    } else
                        rl_checkin.setVisibility(View.VISIBLE);
                    break;
                case SPLASH_SCREEN:
                    if(animate) {
                        ll_splashScreen.setAlpha(0);
                        ll_splashScreen.setVisibility(View.VISIBLE);
                        ll_splashScreen.setTranslationY(0);
                        ll_splashScreen.animate()
                                .alpha(1)
                                .translationY(Styling.getInDP(this, 0))
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        setViewAnimationComplete();
                                    }
                                });
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
                                .setDuration(ANIMATION_DURATION)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                setViewAnimationComplete();
                            }
                        });
                    } else {
                        ll_not_checked_in.setVisibility(View.VISIBLE);
                    }
                    break;
                case SUMMARY:
                    if(animate) {
                        fl_checkin_summary.setAlpha(0);
                        fl_checkin_summary.setVisibility(View.VISIBLE);
                        fl_checkin_summary.setTranslationY(Styling.getInDP(this, 100));
                        fl_checkin_summary.animate()
                                .translationY(0)
                                .alpha(1)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                setViewAnimationComplete();
                                fl_checkin_summary.setAlpha(1);
                                fl_checkin_summary.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        fl_checkin_summary.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
        currentView = showView;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Styling.forcePortraitOnSmallDevices(this);
        super.onCreate(savedInstanceState);
        gestureDetector = new GestureDetectorCompat(this, new GestureListener());
        setContentView(R.layout.checkin_activity);
        API = new Communicate(this);
        if(!API.isCredentialsSaved()) { // Ensure the user is signed in.
            new ForceLogin(this);
            return;
        }
        Styling.setFont(); // Uses the Calligraphy library inject the font.
        assignViews();
        initialise();

        if(savedInstanceState != null && savedInstanceState.containsKey(SI_CURRENT_VIEW) && savedInstanceState.containsKey(SI_CHECKIN_DATE) && savedInstanceState.containsKey(SI_CHECKIN)) {
            // Restore previous view.
            Views savedViewState = (Views) savedInstanceState.getSerializable(SI_CURRENT_VIEW);
            Calendar savedCheckinDate = Calendar.getInstance();
            savedCheckinDate.setTime(new Date(savedInstanceState.getLong(SI_CHECKIN_DATE)));
            CheckIn savedCheckIn = (CheckIn) savedInstanceState.getSerializable(SI_CHECKIN);
            displayCheckin(savedCheckinDate, savedCheckIn);
            setView(savedViewState, false);
        } else {
            setView(Views.SPLASH_SCREEN, false);
            displayCheckin(Calendar.getInstance());
        }

        //checkMinimumVersion();
    }
// TODO ensure implemented into the new API (same endpoint and formatting preferably).
//    private void checkMinimumVersion() {
//        //Validate minimum client version
//        flareDownAPI.getMinimumClient(new API.OnApiResponse<JSONObject>() {
//            @Override
//            public void onFailure(API_Error error) {
//                //unable to get min version from api, do nothing
//            }
//
//            @Override
//            public void onSuccess(JSONObject result) {
//                try {
//                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//                    JSONObject android = result.getJSONObject("android");
//                    String version = android.getString("major") + "." + android.getString("minor");
//                    Double minVersion = Double.parseDouble(version);
//
//                    if (pInfo.versionCode <= minVersion) {
//                        AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
//                        dialog.setTitle(Locales.read(getApplicationContext(), "nice_errors.minimum_client_error").create());
//                        dialog.setCancelable(false);
//                        dialog.setMessage(Locales.read(getApplicationContext(), "nice_errors.minimum_client_error_description").create());
//                        dialog.setPositiveButton(Locales.read(getApplicationContext(), "nav.update").create(), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                intent.setData(Uri.parse(Locales.read(getApplicationContext(), "URI.android_app_uri").create()));
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//                        dialog.show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (PackageManager.NameNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(checkIn != null) {
            outState.putSerializable(SI_CHECKIN, checkIn);
            outState.putLong(SI_CHECKIN_DATE, checkinDate.getTime().getTime());
            outState.putInt(SI_CHECKIN_PAGE_NUMBER, currentQuestionPage);
        }
        outState.putSerializable(SI_CURRENT_VIEW, currentView);
    }

    /**
     * Finds the views of relevant views and assigns it's corresponding variable.
     */
    private void assignViews() {
        bt_nextQuestion = (ImageButton) findViewById(R.id.bt_nextQuestion);
        bt_prevQuestion = (ImageButton) findViewById(R.id.bt_prevQuestion);
        bt_submitCheckin = (Button) findViewById(R.id.bt_submitCheckin);
        bt_not_checked_in_checkin = (Button) findViewById(R.id.bt_not_checked_in_checkin);
        vp_questions = (ViewPager) findViewById(R.id.vp_questionPager);

        tv_not_checked_in_checkin = (TextView) findViewById(R.id.tv_not_checked_in_checkin);

        mainToolbarView = (MainToolbarView) findViewById(R.id.main_toolbar_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        ll_not_checked_in = (LinearLayout) findViewById(R.id.ll_not_checked_in);
        ll_splashScreen = (LinearLayout) findViewById(R.id.ll_splashScreen);
        rl_checkin = (RelativeLayout) findViewById(R.id.rl_checkin);
        fl_checkin_summary = (FrameLayout) findViewById(R.id.fl_checkin_summary);
    }

    private void initialise() {
        //Set up the toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bt_not_checked_in_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setView(Views.CHECKIN);
                Answers.getInstance().logCustom(new CustomEvent("New Check In"));
            }
        });

        vp_questions.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position <= 0) {
                    bt_prevQuestion.setVisibility(View.INVISIBLE);
                    bt_submitCheckin.setVisibility(View.GONE);
                    bt_nextQuestion.setVisibility(View.VISIBLE);
                } else if (position >= vpa_questions.getFragments().size() - 1) {
                    bt_nextQuestion.setVisibility(View.GONE);
                    bt_prevQuestion.setVisibility(View.VISIBLE);
                    bt_submitCheckin.setVisibility(View.VISIBLE);
                } else {
                    bt_submitCheckin.setVisibility(View.GONE);
                    bt_nextQuestion.setVisibility(View.VISIBLE);
                    bt_prevQuestion.setVisibility(View.VISIBLE);
                }

                if (currentQuestionPage != null)
                    vpa_questions.getFragments().get(currentQuestionPage).onPageExit();
                vpa_questions.getFragments().get(position).onPageEnter();
                currentQuestionPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bt_nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_questions.setCurrentItem(vp_questions.getCurrentItem() + 1, true);
            }
        });
        bt_prevQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vp_questions.setCurrentItem(vp_questions.getCurrentItem() - 1, true);
            }
        });

        bt_submitCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCheckin();
                displaySummary();
                setView(Views.SUMMARY);
            }
        });

        mainToolbarView.setNextOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoadingCheckin) {
                    setView(Views.SPLASH_SCREEN);
                    Calendar c = (Calendar) checkinDate.clone();
                    c.add(Calendar.DATE, 1);
                    displayCheckin(c);
                }
            }
        });
        mainToolbarView.setPrevOnClickListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoadingCheckin) {
                    setView(Views.SPLASH_SCREEN);
                    Calendar c = (Calendar) checkinDate.clone();
                    c.add(Calendar.DATE, -1);
                    displayCheckin(c);
                }
            }
        });
    }

    private void updateDateButtons(Calendar date) {
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        if(today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) && today.get(Calendar.YEAR) == date.get(Calendar.YEAR)) {
            mainToolbarView.setPrevButtonState(MainToolbarView.ButtonState.VISIBLE);
            mainToolbarView.setNextButtonState(MainToolbarView.ButtonState.HIDDEN);
        } else {
            mainToolbarView.setPrevButtonState(MainToolbarView.ButtonState.VISIBLE);
            mainToolbarView.setNextButtonState(MainToolbarView.ButtonState.VISIBLE);
        }
    }

    /**
     * Submits the checkin to flaredown and displays the summary page.
     */
    private void submitCheckin() {
        // Auto saves so no need to submit data to the api.
    }

    private void displaySummary() {
        try {
            f_checkin_sumary = CheckInSummaryFragment.newInstance();
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();

            trans.replace(fl_checkin_summary.getId(), f_checkin_sumary).commit();
        } catch (Exception e) { // was jsonException
            new ErrorDialog(CheckinActivity.this, new Error().setExceptionThrown(e).setDebugString("CheckinActivity:displaySummary..JSONException")).setCancelable(false).show();
        }
    }

    private void removeSummary() {
        /*if(f_checkin_sumary != null) {
            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.remove(f_checkin_sumary);
            trans.();
            f_checkin_sumary = null;
        }*/
    }

    Calendar lastUpdate = null;

    /**
     * Call when the check in has been changed, this will notify and update the API.
     */
    public void checkInUpdate() {
        final Calendar updateTime = lastUpdate = Calendar.getInstance();
        API.submitCheckin(checkIn, new APIResponse<CheckIn, Error>() {
            @Override
            public void onSuccess(CheckIn result) {
                PreferenceKeys.log(PreferenceKeys.LOG_D, DEBUG_KEY, "Check in saved successfully");
                if(isActivityPaused()) {
                    // Display toast if the application is not visible.
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.locales_summary_title), Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(Calendar.getInstance().getTimeInMillis() - updateTime.getTimeInMillis() + 1000);
                                if (updateTime.equals(lastUpdate))
                                    SnackbarStyling.colorSnackBar(Snackbar.make(findViewById(R.id.cl_root_view), R.string.locales_summary_title, Snackbar.LENGTH_SHORT), getResources().getColor(R.color.background)).show();
                            } catch (InterruptedException e) {
                            }
                        }
                    }).start();
                }
            }

            @Override
            public void onFailure(Error result) {
                new ErrorDialog(CheckinActivity.this, result).setCancelable(false).show();
            }
        });
    }

    /**
     * Display the check in for a specific date.
     * @param date The date for the check in.
     */
    private void displayCheckin(final Calendar date) {
        removeSummary();
        updateDateButtons(date);
        checkIn = null;
        toolbarTitle.setText(Styling.displayDateLong(date));
        checkinDate = date;
        isLoadingCheckin = true;
        API.checkIn(date, new APIResponse<CheckIn, com.flaredown.flaredownApp.Helpers.APIv2.Error>() {
            @Override
            public void onSuccess(CheckIn result) {
                displayCheckin(date, result);
            }

            @Override
            public void onFailure(Error result) {
                new ErrorDialog(CheckinActivity.this, result).setCancelable(false).show();
            }
        });
    }

    /**
     * Display the check in for a specific date, passing the entries json object.
     * @param date The date for the check in.
     * @param checkIn Prefetched entries json object, including response.
     */
    private void displayCheckin(final Calendar date, CheckIn checkIn) {
        this.checkinDate = date;
        this.checkIn = checkIn;
        removeSummary();
        updateDateButtons(date);
        toolbarTitle.setText(Styling.displayDateLong(date));
        if(currentView == Views.SPLASH_SCREEN) {
            if(checkIn.hasResponse()) // Show the correct view
            {
                setView(Views.SUMMARY);
                displaySummary(); // TODO display summary if check in completed
            } else
            setView(Views.NOT_CHECKED_IN_YET);
        }
        List<ViewPagerFragmentBase> fragments = createFragments(checkIn);
        if(vpa_questions == null) {
            vpa_questions = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
            vp_questions.setAdapter(vpa_questions);
        } else {
            vpa_questions.removeAllFragments();
            vpa_questions.setFragments(fragments);
            vp_questions.setCurrentItem(0, false);
        }
        isLoadingCheckin = false;
    }

    /**
     * Creates a list array of fragment objects for each question specified in the JSON object
     * @param checkIn Specification for each question.
     * @return A list array of Fragments extending the View Pager Fragment.
     * @throws JSONException
     */
    public static List<ViewPagerFragmentBase> createFragments(CheckIn checkIn) {
        List<ViewPagerFragmentBase> fragments = new ArrayList<>();
        for (TrackableType trackableType : TrackableType.trackableValues()) {
            CheckinCatalogQFragment checkinCatalogQFragment = CheckinCatalogQFragment.newInstance(trackableType);
            fragments.add(checkinCatalogQFragment);
        }
        fragments.add(TagFragment.newInstance());
        return fragments;
    }

    public List<ViewPagerFragmentBase> getFragmentQuestions() {
        return vpa_questions.getFragments();
    }

    /**
     * Get the tag fragment.
     * @return
     */
    public TagFragment getTagFragment() {
        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();
        for (Fragment fragment : fragments) {
            if(fragment instanceof TagFragment) {
                return (TagFragment) fragment;
            } else if(fragment instanceof CheckInSummaryFragment && currentView.equals(Views.SUMMARY)) {
                List<ViewPagerFragmentBase> subFragments = ((CheckInSummaryFragment) fragment).getFragments();
                for (ViewPagerFragmentBase subFragment : subFragments) {
                    if(subFragment instanceof TagFragment) {
                        return (TagFragment) subFragment;
                    }
                }
            }
        }
        return null;
    }

    public ViewPagerAdapter getScreenSlidePagerAdapter() {
        return vpa_questions;
    }

    public void setOnActivityResultListener(OnActivityResultListener onActivityResultListener){
        this.onActivityResultListener = onActivityResultListener;
    }

    public interface OnActivityResultListener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    /**
     * The adapter for the view pager, used to display the questions.
     */
    public static class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<ViewPagerFragmentBase> fragments;
        private FragmentManager fragmentManager;
        private OnPageViewCountListener onPageViewCountListener;

        /**
         * Constructs the custom view pager adapte with fragments that extent the ViewPagerFragmentBase class.
         * @param fragmentManager
         * @param fragments The fragments to display in view pager.
         */
        public ViewPagerAdapter(FragmentManager fragmentManager, List<ViewPagerFragmentBase> fragments) {
            super(fragmentManager);
            this.fragmentManager = fragmentManager;
            this.fragments = fragments;
            attachFragments();
        }

        /**
         * Remove all fragments from the view pager.
         */
        public void removeAllFragments() {
            final FragmentTransaction trans = fragmentManager.beginTransaction();
            for (int i = 0; i < fragments.size(); i++) {
                trans.remove(fragments.get(i));
            }
            trans.commit();
            fragments = new ArrayList<>();
            notifyDataSetChanged();
            triggerOnViewPageCountChange();
        }

        private void attachFragments() {
            final FragmentTransaction trans = fragmentManager.beginTransaction();
            for (int i = 0; i < fragments.size(); i++) {
                trans.show(fragments.get(i));
            }
            trans.commit();
        }

        private void triggerOnViewPageCountChange() {
            if(this.onPageViewCountListener != null)
                this.onPageViewCountListener.onPageViewCountChange(fragments.size());
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

        /*
                                    Getters and setters.
                                 */
        public List<ViewPagerFragmentBase> getFragments() {
            return fragments;
        }

        public void setFragments(List<ViewPagerFragmentBase> viewPagerFragmentBase) {
            this.fragments = viewPagerFragmentBase;
            attachFragments();
            notifyDataSetChanged();
            triggerOnViewPageCountChange();
        }

        public void setOnPageViewCountListener(OnPageViewCountListener onPageViewCountListener) {
            this.onPageViewCountListener = onPageViewCountListener;
        }
    }

    /**
     * An event listener which is triggered when the page size changes for a view pager.
     */
    public interface OnPageViewCountListener {
        void onPageViewCountChange(int size);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Used to inject a custom font with Calligraphy library to the activity.
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(onActivityResultListener != null) {
            onActivityResultListener.onActivityResult(requestCode, resultCode, data);
            onActivityResultListener = null;
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == FlaredownConstants.ADD_TRACKABLE_REQUEST_CODE) {
                //Retrieve trackable from intent
                if (data.hasExtra(FlaredownConstants.RETURN_TRACKABLE_KEY)){
                    Bundle bundle = data.getExtras();
                    final Trackable trackable = (Trackable) bundle.get(FlaredownConstants.RETURN_TRACKABLE_KEY);
                    if (trackable != null){
                        trackable.setCheckInId(checkIn.getId());
                        if (DateUtils.isToday(checkIn.getDate().getTimeInMillis())){
                            //If checkin is today, add trackable to /tracking
                            Tracking tracking = new Tracking();
                            tracking.setTrackable_id(trackable.getTrackableId());
                            //tracking.setId(trackable.getId());
                            tracking.setTrackable_type(trackable.getType());
                            API.submitTracking(tracking, new APIResponse<Tracking, Error>() {
                                @Override
                                public void onSuccess(Tracking result) {
                                    updateLocalCheckinAndUI(trackable);
                                }

                                @Override
                                public void onFailure(Error result) {
                                    //Alert use that adding failed
                                    //Cleanup
                                    Log.d("Error",result.toString());
                                }
                            });
                        } else {
                            updateLocalCheckinAndUI(trackable);
                        }
                    }
                } else if(data.hasExtra(AddEditableActivity.RETURN_TAG_KEY)) {
                    Bundle bundle = data.getExtras();
                    final Tag tag = (Tag) bundle.getSerializable(AddEditableActivity.RETURN_TAG_KEY);
                    if(tag != null) {
                        getTagFragment().addTag(tag);
                    }
                }
            }
        }
    }

    private void updateLocalCheckinAndUI(final Trackable trackable) {
        //Update Checkin
        if (trackable.getType() == TrackableType.CONDITION){
            checkIn.getConditions().add(trackable);
        } else if (trackable.getType() == TrackableType.SYMPTOM){
            checkIn.getSymptoms().add(trackable);
        } else if (trackable.getType() == TrackableType.TREATMENT){
            checkIn.getTreatments().add(trackable);
        }

        API.submitCheckin(checkIn, new APIResponse<CheckIn, Error>() {
            @Override
            public void onSuccess(CheckIn result) {
                for (Trackable newTrackable : result.getTrackables(trackable.getType())){
                    if (newTrackable.getTrackableId().equals(trackable.getTrackableId())){
                        trackable.setId(newTrackable.getId());
                    }
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                List<Fragment> nestedFragments = fragmentManager.getFragments();

                if (nestedFragments != null || nestedFragments.size() != 0) {
                    for (Fragment fragment : nestedFragments){
                        if (fragment instanceof CheckInSummaryFragment){
                            List<ViewPagerFragmentBase> nestedCatalogQFragments = ((CheckInSummaryFragment) fragment).getFragments();
                            for (Fragment frag: nestedCatalogQFragments){
                                if (frag instanceof CheckinCatalogQFragment){
                                    if (((CheckinCatalogQFragment) frag).getTrackableType() == trackable.getType()){
                                        ((CheckinCatalogQFragment) frag).addTrackable(trackable);
                                    }
                                }
                            }
                        } else if (fragment instanceof CheckinCatalogQFragment){
                            if (((CheckinCatalogQFragment) fragment).getTrackableType() == trackable.getType()){
                                ((CheckinCatalogQFragment) fragment).addTrackable(trackable);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Error result) {
                Log.d("Checkin Error",result.toString());
            }
        });
    }


    // On activity Pause listener

    /**
     * Add an activity paused listener, which is called when the application is paused.
     * @param activityPauseEventListener The interface object which is called when the program is paused.
     */
    public void addActivityPauseListener(ActivityPauseEventListener activityPauseEventListener) {
        activityPauseEventListeners.add(activityPauseEventListener);
    }

    /**
     * Trigger all attached activty pause listeners.
     */
    private void triggerActivityPauseListener() {
        for (ActivityPauseEventListener activityPauseEventListener : activityPauseEventListeners) {
            activityPauseEventListener.onPause();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        triggerActivityPauseListener();
        activityPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityPaused = false;
    }

    /**
     * Returns true if the activity is paused.
     * @return True if the activity is paused.
     */
    public boolean isActivityPaused() {
        return activityPaused;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Patching in the gesture detector.
        this.gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent ev) {
            View v = getCurrentFocus();
            if((v instanceof EditText) && !(v instanceof AutoCompleteTextView)) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    EditText et = (EditText) v;
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    et.clearFocus();
                    et.setFocusable(false);
                    et.setFocusableInTouchMode(false);
                    et.setFocusable(true);
                    et.setFocusableInTouchMode(true);
                }
            }
            return super.onSingleTapConfirmed(ev);
        }
    }
}
