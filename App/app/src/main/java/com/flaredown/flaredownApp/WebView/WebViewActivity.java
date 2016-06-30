package com.flaredown.flaredownApp.WebView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flaredown.flaredownApp.BuildConfig;
import com.flaredown.flaredownApp.Checkin.CheckinActivity;
import com.flaredown.flaredownApp.Helpers.APIv2.Communicate;
import com.flaredown.flaredownApp.Helpers.Styling.Styling;
import com.flaredown.flaredownApp.Login.ForceLogin;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Settings.SettingsActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thunter on 07/06/16.
 */
public class WebViewActivity extends Activity {
    private WebView wv_main;
    private FloatingActionButton fab_settings;
    private LinearLayout ll_splashScreen;

    private CookieManager cookieManager;
    private Communicate API;
    private String oldUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO remove
        Intent intent = new Intent(WebViewActivity.this, CheckinActivity.class);
        startActivity(intent);


        Styling.forcePortraitOnSmallDevices(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity);
        API = new Communicate(this);
        if (!API.isCredentialsSaved()) { // Ensure the user is signed in.
            new ForceLogin(this);
            return;
        }

        // Assigning variables.
        wv_main = (WebView) findViewById(R.id.wv_main);
        fab_settings = (FloatingActionButton) findViewById(R.id.fab_settings);
        ll_splashScreen = (LinearLayout) findViewById(R.id.ll_splashScreen);



        fab_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebViewActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


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
                            .translationY(-Styling.getInDP(WebViewActivity.this, 100))
                            .setDuration(CheckinActivity.ANIMATION_DURATION)
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && wv_main.canGoBack()) {
            wv_main.goBack();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        wv_main.saveState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().stopSync();
        }
    }

    @Override
    protected void onResume() {
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

                    Intent intent = new Intent(WebViewActivity.this, CheckinActivity.class);
                    intent.putExtra(CheckinActivity.I_CHECK_IN_ID, checkinId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                WebViewActivity.this.runOnUiThread(new Runnable() {
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
                    new ForceLogin(WebViewActivity.this);
                    finish();
                }
            }
        }
    }
}
