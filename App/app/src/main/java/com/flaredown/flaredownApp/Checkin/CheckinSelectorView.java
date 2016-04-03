package com.flaredown.flaredownApp.Checkin;

import android.content.Context;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.flaredown.flaredownApp.Helpers.API.EntryParser.*;
import com.flaredown.flaredownApp.Helpers.Locales;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Helpers.Styling.Styling;

import java.util.ArrayList;
import java.util.List;

public class CheckinSelectorView extends LinearLayout { //TODO check no need for saved instance state
    private List<InputButton> buttons = new ArrayList<>();
    private List<Input> inputs;
    private Object value = null;
    private OnValueChangeListener onValueChangeListener = null;

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    public CheckinSelectorView(Context context) {
        super(context);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        for (InputButton button : buttons) {
            try {
                if (button.getValue().equals(value)) {
                    button.selectButton();
                    return;
                }
                if(((Integer) button.getValue()).doubleValue() == ((Double) value).doubleValue()){
                    button.selectButton();
                    return;
                }
            } catch(Exception e){}
        }
    }

    public CheckinSelectorView setInputs(List<Input> inputs) {
        if(inputs == null) return this;
        final CheckinSelectorView t = this;
        this.inputs = inputs;
        this.removeAllViews();
        this.buttons = new ArrayList<>();
        this.setOrientation(VERTICAL);
        for (Input input : inputs) {
            InputButton inputButton = new InputButton(new ContextThemeWrapper(getContext(), R.style.AppTheme_Checkin_Selector_Button), null, R.style.AppTheme_Checkin_Selector_Button, input);
            inputButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputButton clickView = (InputButton) v;
                    if (onValueChangeListener != null) {
                        onValueChangeListener.onValueChange(clickView.getValue());
                    }
                    setValue(clickView.getValue());
                }
            });
            buttons.add(inputButton);
            this.addView(inputButton);
            int margins  = (int) Styling.getInDP(getContext(), 5);
            ((MarginLayoutParams) inputButton.getLayoutParams()).setMargins(margins, margins, margins, margins);
            if(input.getLabel() != null) {
                inputButton.setText(Locales.read(getContext(), "labels." + input.getLabel()).resultIfUnsuccessful(input.getLabel()).createAT());
            }
            else {
                inputButton.setIconMode(this);
            }
        }
        return this;
    }

    private class InputButton extends Button {
        private Input entryInput;
        private OnClickListener customOnClickListener;
        private boolean iconMode = false;

        public InputButton(Context context, AttributeSet attrs, int defStyleAttr, Input input) {
            super(context, attrs, defStyleAttr);
            this.entryInput = input;
            super.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(customOnClickListener!=null) customOnClickListener.onClick(v);
                    selectButton();
                }
            });
        }

        @Override
        public void setOnClickListener(OnClickListener l) {
            this.customOnClickListener = l;
        }

        public void setMetaLabel(String metaLabel) {
            if(metaLabel != null && metaLabel.equals("smiley")) {
                Styling.setBackground(getContext(), this, R.drawable.button_selector_meta_smiley_selector);
            }
        }

        public void selectButton() {
            /*for (InputButton button : buttons)
                if(!iconMode)
                    button.setSelected(button.equals(this));
                else
                    button.setSelected((button.tryGetValue() <= this.tryGetValue() && this.tryGetValue() != 0));
                    */
            for (int i = 0; i < buttons.size(); i++) {
                InputButton button = buttons.get(i);
                if(!iconMode) {
                    button.setSelected(button.equals(this));
                } else {
                    button.setSelected((i == 0 && this.tryGetValue() == 0) || (i != 0 && this.tryGetValue() >= button.tryGetValue()));
                }
            }
        }

        public void setIconMode(LinearLayout ll) {
            this.iconMode = true;
            ll.setOrientation(HORIZONTAL);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);
            this.setText("");
            this.getLayoutParams().height = this.getLayoutParams().width = (int) Styling.getInDP(getContext(), 30);
            this.setMetaLabel(entryInput.getMetaLabel());
        }

        public Object getValue() {
            return entryInput.getValue();
        }

        public Integer tryGetValue() {
            try {
                return (Integer) entryInput.getValue();
            } catch (ClassCastException e) {
                return 0;
            }
        }
    }

    public interface OnValueChangeListener {
        void onValueChange(Object value);
    }
}