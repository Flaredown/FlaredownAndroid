package com.flaredown.flaredownApp.Checkin.InputViews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.Trackable;

/**
 * Container for a question input, providing a question title above the input view.
 */
public class InputContainerView extends LinearLayout{
    Trackable trackable;
    TextView tv_questionTitle;
    View v_input = null;
    TextView tv_add;
    public InputContainerView(Context context, Trackable trackable) {
        super(context);

        this.trackable = trackable;
        // Set styling.
        this.setGravity(Gravity.CENTER_HORIZONTAL);
        this.setOrientation(VERTICAL);

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.START;


        this.setLayoutParams(lp);

        // Adding the question title
        tv_questionTitle = new TextView(context);
        this.addView(tv_questionTitle);
    }

    public Trackable getTrackable() {
        return trackable;
    }

    public void setTrackable(Trackable trackable) {
        this.trackable = trackable;
    }

    /**
     * Set the question title.
     * @param title The title for the question.
     * @return itself.
     */
    public InputContainerView setQuestionTitle(CharSequence title) {
        tv_questionTitle.setText(title);
        return this;
    }

    /**
     * Set the question title.
     * @param resId String id for the title.
     * @return itself.
     */
    public InputContainerView setQuestionTitle(int resId) {
        tv_questionTitle.setText(resId);
        return this;
    }

    /**
     * Set the input view
     * @param inputView The input view to display.
     * @return itself.
     */
    public InputContainerView setInputView(View inputView) {
        if(this.v_input != null)
            this.removeView(this.v_input);
        this.v_input = inputView;
        this.addView(this.v_input);
        return this;
    }

    /**
     * Get the input view set for the InputContainerView.
     * @return The input view for the InputContainerView.
     */
    @Nullable
    public View getInputView() {
        return this.v_input;
    }
}
