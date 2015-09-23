package com.flaredown.flaredownApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.flaredown.com.flaredown.R;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by thunter on 13/09/15.
 */
public class MainToolbarView extends LinearLayout{

    private Toolbar toolbar;
    private TextView title;
    private Context mContext;

    public MainToolbarView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.toolbar, this, true);

        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        title = (TextView) findViewById(R.id.toolbar_title);
        toolbar.getMenu()
                .add("Settings")
                .setIcon(R.drawable.ic_settings)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(context, SettingsActivity.class);
                        context.startActivity(intent);
                        return true;
                    }
                })
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
    public void setBackButton(boolean show) {
        if(show) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ((Activity) mContext).finish();
                    } catch (Exception e ) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(null);
        }
    }

    public MainToolbarView(Context context) {
        this(context, null);
    }
    public void setTitle (String text) {
        if(title != null) title.setText(text);
    }
    public Toolbar getActionBar() {
        return toolbar;
    }
}
