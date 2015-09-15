package com.flaredown.flaredownApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.flaredown.com.flaredown.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONObject;

public class LocalesTest extends AppCompatActivity {

    private API api;
    private TextView textView;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_locales_test);

        textView = (TextView) findViewById(R.id.textview);


        textView.setText("Updating Locales... Please wait");

        api = new API(this);

        SharedPreferences localeSP = Locales.getSharedPreferences(mContext);
        //textView.setText(Html.fromHtml(localeSP.getString("showing_symptoms_and_treatments_over_days", "NOTHING")));

        textView.setText(Html.fromHtml(Locales.read(this, "showing_symptoms_and_treatments_over_das").replace("numSymptoms", "10").replace("numConditions","11").replace("numTreatments", "3").replace("numDays", "2").create()));
    }
}
