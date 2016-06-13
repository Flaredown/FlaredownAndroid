package com.flaredown.flaredownApp.WebView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.flaredown.flaredownApp.BuildConfig;
import com.flaredown.flaredownApp.Helpers.APIv2.Communicate;
import com.flaredown.flaredownApp.Helpers.Styling.Styling;
import com.flaredown.flaredownApp.Login.ForceLogin;
import com.flaredown.flaredownApp.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by thunter on 07/06/16.
 */
public class WebViewActivity extends Activity {
    private WebView wv_main;
    private CookieManager cookieManager;
    private Communicate API;
    private String oldUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Styling.forcePortraitOnSmallDevices(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_activity);
        API = new Communicate(this);
        if(!API.isCredentialsSaved()) { // Ensure the user is signed in.
            new ForceLogin(this);
            return;
        }

        // Assigning variables.
        wv_main = (WebView) findViewById(R.id.wv_main);


        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        // Managing the web view.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(wv_main.getContext());
            cookieSyncManager.sync();
        }

        try {
            cookieManager.setCookie(BuildConfig.WEB_URL, "ember_simple_auth:session=" + URLEncoder.encode(API.createSessionCookieData(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {}
        WebSettings webSettings = wv_main.getSettings();

        webSettings.setJavaScriptEnabled(true);

        // Set the website url
        if(savedInstanceState == null)
            wv_main.loadUrl(BuildConfig.WEB_URL);
        else
            wv_main.restoreState(savedInstanceState);


        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.v("WV", "URL CHANEGED TO " + url);
            }
        };
        wv_main.setWebViewClient(webViewClient);
        final WebAppInterface webAppInterface = new WebAppInterface();
        wv_main.addJavascriptInterface(webAppInterface, "androidWebView");


        // Temporary url change listener // TODO remove when JavaScript interface has been added.
        Thread urlListenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        Thread.sleep(200);
                        if(wv_main != null) {
                            WebViewActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!wv_main.getUrl().equals(oldUrl)) {
                                        oldUrl = wv_main.getUrl();
                                        webAppInterface.notifyUrlChange(oldUrl);
                                    }
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {

                }
            }
        });

        urlListenerThread.start();
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
        public void notifyUrlChange(String url) {
            Toast.makeText(WebViewActivity.this, "New Url " + url, Toast.LENGTH_SHORT).show();
        }
    }
}
