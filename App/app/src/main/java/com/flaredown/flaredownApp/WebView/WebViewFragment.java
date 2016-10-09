package com.flaredown.flaredownApp.WebView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.flaredown.flaredownApp.BuildConfig;
import com.flaredown.flaredownApp.Checkin.CheckinFragment;
import com.flaredown.flaredownApp.Helpers.APIv2.Communicate;
import com.flaredown.flaredownApp.Helpers.Styling.Styling;
import com.flaredown.flaredownApp.Login.ForceLogin;
import com.flaredown.flaredownApp.Main.MainActivity;
import com.flaredown.flaredownApp.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thunter on 07/06/16.
 */
public class WebViewFragment extends Fragment {
    private WebView wv_main;
    private LinearLayout ll_splashScreen;

    private CookieManager cookieManager;
    private Communicate API;

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
        return inflater.inflate(R.layout.web_view_fragment,container,false);
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

        Styling.forcePortraitOnSmallDevices(getActivity());
        super.onCreate(savedInstanceState);

        API = new Communicate(getActivity());
        if (!API.isCredentialsSaved()) { // Ensure the user is signed in.
            new ForceLogin(getActivity());
            return;
        }

        // Assigning variables.
        wv_main = (WebView) view.findViewById(R.id.wv_main);
        ll_splashScreen = (LinearLayout) view.findViewById(R.id.ll_splashScreen);

        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        // Managing the web view.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(wv_main.getContext());
            cookieSyncManager.sync();
        }

        try {
            cookieManager.setCookie(BuildConfig.WEB_URL, "ember_simple_auth:session=" + URLEncoder.encode(API.createSessionCookieData(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }

        WebSettings webSettings = wv_main.getSettings();

        webSettings.setJavaScriptEnabled(true);

        // Set the website url
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Hide the splash screen... Only if it isn't already hidden.
                if(ll_splashScreen.getVisibility() != View.GONE) {
                    ll_splashScreen.setAlpha(1);
                    ll_splashScreen.setVisibility(View.VISIBLE);
                    ll_splashScreen.animate()
                            .alpha(0)
                            .translationY(-Styling.getInDP(getActivity(), 100))
                            .setDuration(CheckinFragment.ANIMATION_DURATION)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    ll_splashScreen.setVisibility(View.GONE);
                                    ll_splashScreen.setTranslationY(0);
                                    ll_splashScreen.setAlpha(1);
                                }
                            });
                }
            }
        };
        wv_main.setWebViewClient(webViewClient);
        final WebAppInterface webAppInterface = new WebAppInterface();
        wv_main.addJavascriptInterface(webAppInterface, "AndroidInterface");

        if (savedInstanceState == null)
            wv_main.loadUrl(BuildConfig.WEB_URL);
        else {
            wv_main.restoreState(savedInstanceState);
            ll_splashScreen.setVisibility(View.GONE);
        }

    }

/*    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && wv_main.canGoBack()) {
            wv_main.goBack();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }*/


    @Override
    public void onPause() {
        super.onPause();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().stopSync();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        }
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void pageChanged(String url) {
            Matcher urlMatcher = Pattern.compile("https?://[^/]+/([^/]+)/?([\\s\\S]+)?").matcher(url);
            if(urlMatcher.matches()) {
                String endPoint = urlMatcher.group(1);
                String remainder = urlMatcher.group(2);
                if(endPoint.equals("checkin")) {
                    String checkinId = remainder.split("/", 2)[0];
                    // TODO launch android activity and tell which check in to view.

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra(MainActivity.I_VIEW, MainActivity.Views.CHECK_IN);
                    intent.putExtra(CheckinFragment.I_CHECK_IN_ID, checkinId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        wv_main.goBack();
                                    }
                                });
                            } catch (InterruptedException e) {}
                        }
                    }).start();
//                    wv_main.goBack();
                } else if(endPoint.equals("login")) {
                    API.userSignOut();
                    new ForceLogin(getActivity());
                    return;
                }
            }
        }
    }
}
