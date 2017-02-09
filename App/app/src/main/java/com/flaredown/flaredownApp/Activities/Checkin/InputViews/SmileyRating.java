package com.flaredown.flaredownApp.Activities.Checkin.InputViews;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flaredown.flaredownApp.Helpers.APIv2_old.EndPoints.CheckIns.Trackable;
import com.flaredown.flaredownApp.Helpers.Styling.Styling;
import com.flaredown.flaredownApp.R;

import java.util.ArrayList;

import rx.Subscriber;

/**
 * Input view for a smiley face rating used for symptom and condition tracking.
 */
public class SmileyRating extends LinearLayout{
    private ArrayList<Button> buttons = new ArrayList<>();
    private ArrayList<SmileyRatingOnValueChange> onValueChangeListeners = new ArrayList<>();
    private ButtonClickerListener buttonClickerListener = new ButtonClickerListener();
    private Trackable trackable;
    public SmileyRating(final Context context, Trackable trackable) {
        super(context);

        // Set parameters.
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_HORIZONTAL);

        // Add the buttons
        for (int i = 0; i < 5; i++) {
            Button newButton = new Button(new ContextThemeWrapper(context, R.style.AppTheme_Checkin_Selector_Button), null, R.style.AppTheme_Checkin_Selector_Button);

            // Generic button styling.
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.width = lp.height = (int) Styling.getInDP(context, 30);
            newButton.setLayoutParams(lp);
            newButton.setOnClickListener(buttonClickerListener);
            newButton.setText("");
            int margins = (int) Styling.getInDP(context, 5);
            ((MarginLayoutParams) newButton.getLayoutParams()).setMargins(margins, margins, margins, margins);
            buttons.add(newButton);
            this.addView(newButton);

        }
        // Set a smiley face for the 1st button
        Styling.setBackground(context, buttons.get(0), R.drawable.button_selector_meta_smiley_selector);
        this.setTrackable(trackable);
    }

    /**
     * Set the value of the SmileyRating.
     * @param trackable A value 0-4 or null to deselect
     */
    public void setTrackable(Trackable trackable) {
        this.trackable = trackable; // TODO disassociate old subscription

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String v) {
                Integer value = (v == null) ? null : Integer.valueOf(v);
                if(value == null) {
                    for (Button button : buttons) {
                        button.setSelected(false);
                    }
                } else {
                    if (value < 0) value = 0;
                    if (value > 4) value = 4;
                    for (int i = 0; i < buttons.size(); i++) {
                        Button button = buttons.get(i);
                        button.setSelected(i <= value);
                    }
                    if(value != 0)
                        buttons.get(0).setSelected(false);
                }
            }
        };

        trackable.subscribeValueObservable(subscriber);

        subscriber.onNext(trackable.getValue());
    }

    private class ButtonClickerListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if(v instanceof Button) {
                int value = buttons.indexOf(v);
                if(value != -1) {
                    trackable.setValue(String.valueOf(value));
                }
            }
        }
    }
}
