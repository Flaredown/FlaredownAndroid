package com.flaredown.flaredownApp;

import android.content.Context;
import android.flaredown.com.flaredown.R;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flaredown.flaredownApp.FlareDown.Locales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thunter on 19/09/15.
 */
public class Checkin_Selector_View extends LinearLayout{

    Context context;
    List<InputButton> buttons = new ArrayList<>();
    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        for(int i = 0; i < buttons.size(); i++) {
            InputButton button= buttons.get(i);
            if(button.value == value) {
                button.performClick();
                return;
            }
        }
    }

    public Checkin_Selector_View(Context context) {
        super(context);
        this.context = context;
        this.setOrientation(VERTICAL);
    }

    public Checkin_Selector_View setInputs(JSONArray inputs) throws JSONException {
        buttons.clear();
        this.removeAllViews();
        for(int i = 0; i < inputs.length(); i++) {
            JSONObject input = inputs.getJSONObject(i);
            InputButton button = new InputButton(new ContextThemeWrapper(context, R.style.AppTheme_Checkin_Selector_Button), null, R.style.AppTheme_Checkin_Selector_Button, input.getInt("value"));
            button.setId(Styling.getUniqueId());
            buttons.add(button);
            String label = String.valueOf(input.getInt("value") + 1);

            if(input.has("label")) {
                label = input.getString("label");
                label = Locales.read(context, "labels." + label).resultIfUnsuccessful(label).create();
            }

            button.setText(label);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(int i = 0; i < buttons.size(); i++) {
                        buttons.get(i).setSelected(false);
                    }
                    v.setSelected(true);
                    value = ((InputButton) v).value;
                }
            });
            this.addView(button);
            int margins  = (int) Styling.getInDP(context, 5);
            ((MarginLayoutParams) button.getLayoutParams()).setMargins(margins, margins, margins, margins);
        }
        return this;
    }

    class InputButton extends Button {
        public InputButton(Context context, AttributeSet attrs, int defStyleAttr, int value) {
            super(context, attrs, defStyleAttr);
            this.value = value;
        }
        int value;
    }
}
