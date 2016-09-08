package com.flaredown.flaredownApp.Settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.TextView;

import com.flaredown.flaredownApp.Helpers.FlaredownConstants;
import com.flaredown.flaredownApp.R;

public class PolicyWebView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_policy_web_view);

        TextView title = (TextView) findViewById(R.id.toolbar_title);


        Bundle bundle = getIntent().getExtras();
        WebView webview = (WebView) findViewById(R.id.blankWebView);
        String identifier;
        String mime = "text/html";
        String encoding = "utf-8";

        if (bundle != null) {
            identifier = bundle.getString(FlaredownConstants.BUNDLE_IDENTIFIER_KEY);
            if (identifier.equals(FlaredownConstants.BUNDLE_IDENTIFIER_VALUE_TERMS)) {
                title.setText(R.string.title_terms_of_service);
                String html = getResources().getString(R.string.locales_terms_of_service);
                webview.loadDataWithBaseURL("",html, mime, encoding,"");
            } else if (identifier.equals(FlaredownConstants.BUNDLE_IDENTIFIER_VALUE_POLICY)) {
                title.setText(R.string.title_privacy_policy);
                String html = getResources().getString(R.string.locales_privacy_policy);
                webview.loadDataWithBaseURL("",html, mime, encoding,"");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_policy_webview, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
