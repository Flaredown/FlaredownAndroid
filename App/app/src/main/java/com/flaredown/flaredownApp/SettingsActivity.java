package com.flaredown.flaredownApp;

import android.content.Context;
import android.content.Intent;
import android.flaredown.com.flaredown.R;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flaredown.flaredownApp.FlareDown.API;

import org.json.JSONObject;
import org.w3c.dom.Text;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatActivity {
    Context mContext;
    MainToolbarView mainToolbarView;
    TextView tv_EditAccount;
    TextView tv_SettingsLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = this;
        Styling.setFont();

        mainToolbarView = (MainToolbarView) findViewById(R.id.main_toolbar_view);
        tv_EditAccount = (TextView) findViewById(R.id.tv_editAccount);
        tv_SettingsLogout = (TextView) findViewById(R.id.tv_settingsLogout);

        mainToolbarView.setTitle("Settings");
        mainToolbarView.getActionBar().getMenu().clear();
        mainToolbarView.setBackButton(true);
        tv_EditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getString(R.string.edit_account_website)));
                startActivity(intent);
            }
        });
        tv_SettingsLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                API flareDownAPI = new API(mContext);
                flareDownAPI.users_sign_out(new API.OnApiResponse() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(API.API_Error error) {
                        Toast.makeText(mContext, "Failed to logout", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
