package com.flaredown.flaredownApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.flaredown.com.flaredown.R;
import android.view.View;
import android.widget.TextView;

public class AddADialogActivity extends AppCompatActivity {

    TextView tv_cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_dialog);


        tv_cancelButton = (TextView) findViewById(R.id.tv_cancel_button);


        tv_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
