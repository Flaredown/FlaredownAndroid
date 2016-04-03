package com.flaredown.flaredownApp.Checkin.InputViews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flaredown.flaredownApp.Helpers.Styling;
import com.flaredown.flaredownApp.R;

import java.util.ArrayList;

/**
 * Input view for a smiley face rating used for symptom and condition tracking.
 */
public class SmileyRating extends LinearLayout{
    ArrayList<Button> buttons = new ArrayList<>();
    private ButtonClickerListener buttonClickerListener = new ButtonClickerListener();
    private Integer value = null;
    public SmileyRating(Context context) {
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
    }

    /**
     * Set the value of the SmileyRating.
     * @param value A value 0-4 or null to deselect
     */
    public void setValue(@Nullable Integer value) {
        this.value = value;
        if(this.value == null) {
            for (Button button : buttons) {
                button.setSelected(false);
            }
        } else {
            if (this.value < 0) this.value = 0;
            if (this.value > 4) this.value = 4;
            for (int i = 0; i < buttons.size(); i++) {
                Button button = buttons.get(i);
                button.setSelected(i <= this.value);
            }
        }
    }

    public class ButtonClickerListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if(v instanceof Button) {
                int value = buttons.indexOf(v);
                if(value != -1)
                    setValue(value);
            }
        }
    }
}
