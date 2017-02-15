package com.flaredown.flaredownApp.Activities.Register;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.flaredown.flaredownApp.Activities.Checkin.CheckinFragment;
import com.flaredown.flaredownApp.Activities.Main.MainActivity;
import com.flaredown.flaredownApp.BuildConfig;
import com.flaredown.flaredownApp.Helpers.APIv2_old.Communicate;
import com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.Session.Session;
import com.flaredown.flaredownApp.Helpers.Styling.Styling;
import com.flaredown.flaredownApp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private WebView wv_main;
    private LinearLayout ll_splashScreen;

    private CookieManager cookieManager;

    /**
     * Starts the acivity from another activity.
     * @param activity The activity to start this activity from.
     */
    public static void startActivity(Activity activity) {
        activity.startActivity(new Intent(activity, RegisterActivity.class));
    }

    private Communicate API;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_fragment);

        API = new Communicate(this);

        wv_main = (WebView) findViewById(R.id.wv_main);
        ll_splashScreen = (LinearLayout) findViewById(R.id.ll_splashScreen);

        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if(savedInstanceState == null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                    @Override
                    public void onReceiveValue(Boolean aBoolean) {

                    }
                });
            } else
                cookieManager.removeAllCookie();
        }

        // Managing the web view.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(wv_main.getContext());
            cookieSyncManager.sync();
        }

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
                            .translationY(-Styling.getInDP(RegisterActivity.this, 100))
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
        wv_main.addJavascriptInterface(new WebAppInterface(), "AndroidInterface");
        WebSettings webSettings = wv_main.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if(savedInstanceState == null) {
            wv_main.loadUrl(BuildConfig.WEB_URL_SIGNUP);
        } else {
            wv_main.restoreState(savedInstanceState);
            ll_splashScreen.setVisibility(View.GONE);
        }
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
            CookieSyncManager.getInstance().stopSync();
        }
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void pageChanged(String url) {
            Matcher urlMatcher = Pattern.compile("https?://[^/]+/([^/]+)/?([\\s\\S]+)?").matcher(url);
            if(urlMatcher.matches()) {
                String endPoint = urlMatcher.group(1);
                String remainder = urlMatcher.group(2);
                if(endPoint.equals("onboarding") && remainder.equals("completed")) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    Pattern cookiePattern = Pattern.compile("ember_simple_auth:session=(.*?)($|;|,(?! ))", Pattern.MULTILINE);
                    Matcher cookieMatch = cookiePattern.matcher(cookieManager.getCookie(BuildConfig.WEB_URL));
                    if(cookieMatch.matches()) {
                        try {
                            String cookie = Uri.decode(cookieMatch.group(1));
                            Session session = new Session(new JSONObject(cookie).getJSONObject("authenticated"));
                            Communicate communicate = new Communicate(RegisterActivity.this);
                            communicate.userSignIn(session);
                            Intent startCheckInIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(startCheckInIntent);
                            finish();
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    } else Log.d("cookie", "No match::: " + cookieManager.getCookie(BuildConfig.WEB_URL));
                }
            }
        }
    }
}
