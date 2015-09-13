package com.flaredown.flaredownApp;

import android.content.Context;
import android.content.Intent;
import android.flaredown.com.flaredown.R;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by thunter on 13/09/15.
 */
public class MainToolbarView extends LinearLayout{

    private Toolbar toolbar;
    private TextView title;

    public MainToolbarView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.toolbar, this, true);

        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        title = (TextView) findViewById(R.id.toolbar_title);
        toolbar.getMenu()
                .add("Settings")
                .setIcon(R.drawable.ic_settings_tinted)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                        return true;
                    }
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
    public MainToolbarView(Context context) {
        this(context, null);
    }
    public void setTitle (String text) {
        title.setText(text);
    }
    public Toolbar getActionBar() {
        return toolbar;
    }
}
