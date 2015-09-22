package com.flaredown.flaredownApp;

import android.content.Context;
import android.flaredown.com.flaredown.R;
import android.os.Parcel;
import android.os.Parcelable;
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
    Checkin_Selector_View t;
    Context context;
    List<InputButton> buttons = new ArrayList<>();
    private int value = -1;
    private final String DEBUG_TAG = "checkin_selector_view";

    public Checkin_Selector_View(Context context) {
        super(context);
        setSaveEnabled(true);
        this.context = context;
        this.setOrientation(VERTICAL);
        t = this;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        if(value != null)
            selectView(value);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        if(getValue() != null)
            ss.value = getValue();
        PreferenceKeys.log(PreferenceKeys.LOG_D, DEBUG_TAG, "SAVING VIEW: " + String.valueOf(ss.value));
        return ss;
    }
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setValue(ss.value);
        PreferenceKeys.log(PreferenceKeys.LOG_D, DEBUG_TAG, "RESTORING VIEW: " + String.valueOf(ss.value));
    }

    private static class SavedState extends BaseSavedState {
        int value;

        SavedState (Parcelable superState) {
            super(superState);
        }
        private SavedState(Parcel in) {
            super(in);
            value = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(value);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }



    public Checkin_Selector_View setInputs(JSONArray inputs) throws JSONException {
        buttons.clear();
        this.removeAllViews();
        for(int i = 0; i < inputs.length(); i++) {
            JSONObject input = inputs.getJSONObject(i);
            InputButton button = new InputButton(new ContextThemeWrapper(context, R.style.AppTheme_Checkin_Selector_Button), null, R.style.AppTheme_Checkin_Selector_Button, input.getInt("value"));
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
                    PreferenceKeys.log(PreferenceKeys.LOG_D, DEBUG_TAG, "Selector button Press");
                    selectView(((InputButton) v).value);
                    if (onButtonClickListener != null) onButtonClickListener.onClick();
                }
            });
            this.addView(button);
            int margins  = (int) Styling.getInDP(context, 5);
            ((MarginLayoutParams) button.getLayoutParams()).setMargins(margins, margins, margins, margins);
        }
        return this;
    }

    private void selectView(int value) {
        for(int i = 0; i < buttons.size(); i++) {
            InputButton button = buttons.get(i);
            button.setSelected(value == button.value);
        }
        this.value = value;
    }


    public interface OnButtonClickListener {
        void onClick();
    }
    private OnButtonClickListener onButtonClickListener;
    public void setButtonClickListener (OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    class InputButton extends Button {
        public InputButton(Context context, AttributeSet attrs, int defStyleAttr, int value) {
            super(context, attrs, defStyleAttr);
            this.value = value;
        }
        int value;
    }
}
