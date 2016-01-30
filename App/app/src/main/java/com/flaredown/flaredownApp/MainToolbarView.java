package com.flaredown.flaredownApp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by thunter on 13/09/15.
 */
public class MainToolbarView extends LinearLayout{

    private Toolbar toolbar;
    private Button bt_next;
    private Button bt_prev;
    private TextView title;
    private Context mContext;
    private View rootView;

    public MainToolbarView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.toolbar, this, true);

        toolbar = (Toolbar) findViewById(R.id.toolbar_top);

        bt_next = (Button) rootView.findViewById(R.id.bt_toolbar_next);
        bt_prev = (Button) rootView.findViewById(R.id.bt_toolbar_prev);


        //title = (TextView) findViewById(R.id.toolbar_title);
        /*toolbar.getMenu()
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
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);*/
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

    public enum ButtonState {
        HIDDEN, VISIBLE
    }

    /**
     * Changes the visibility state of the next button inside the toolbar.
     * @param buttonState The button state.
     */
    public void setNextButtonState(ButtonState buttonState) {
        bt_next.setVisibility((buttonState == ButtonState.VISIBLE) ? VISIBLE : INVISIBLE);
        if(bt_prev.getVisibility() == GONE)
            bt_prev.setVisibility(INVISIBLE);
    }

    /**
     * Changes the visibility state of the previous button inside the toolbar.
     * @param buttonState The button state.
     */
    public void setPrevButtonState(ButtonState buttonState) {
        bt_prev.setVisibility((buttonState == ButtonState.VISIBLE) ? VISIBLE : INVISIBLE);
        if(bt_next.getVisibility() == GONE)
            bt_next.setVisibility(INVISIBLE);
    }

    /**
     * Set the on click listener for the previous button.
     * @param onClickListner On click listener interface.
     */
    public void setPrevOnClickListner(OnClickListener onClickListner) {
        bt_prev.setOnClickListener(onClickListner);
    }

    /**
     * Set the on click listener for the next button.
     * @param onClickListener On click listener interface.
     */
    public void setNextOnClickListener(OnClickListener onClickListener) {
        bt_next.setOnClickListener(onClickListener);
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
