package com.flaredown.flaredownApp.Settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.flaredown.flaredownApp.Helpers.FlaredownConstants;
import com.flaredown.flaredownApp.Helpers.Locales;
import com.flaredown.flaredownApp.R;

public class PolicyWebView extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_policy_web_view);

        //Set Toolbar
        Toolbar mainToolbarView = (Toolbar) findViewById(R.id.toolbar_top);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(mainToolbarView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Bundle bundle = getIntent().getExtras();
        WebView webview = (WebView) findViewById(R.id.blankWebView);
        Context context = PolicyWebView.this;
        String identifier;

        if (bundle != null) {
            identifier = bundle.getString(FlaredownConstants.BUNDLE_IDENTIFIER_KEY);
            if (identifier.equals(FlaredownConstants.BUNDLE_IDENTIFIER_VALUE_TERMS)) {
                title.setText(R.string.title_terms_of_service);
                String html = Locales.read(context, "terms_of_service").create();
                String mime = "text/html";
                String encoding = "utf-8";
                webview.loadDataWithBaseURL(null, html, mime, encoding, null);
            } else if (identifier.equals(FlaredownConstants.BUNDLE_IDENTIFIER_VALUE_POLICY)) {
                title.setText(R.string.title_privacy_policy);
                String html = Locales.read(context, "privacy_policy").create();
                String mime = "text/html";
                String encoding = "utf-8";
                webview.loadDataWithBaseURL(null, html, mime, encoding, null);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id ==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
