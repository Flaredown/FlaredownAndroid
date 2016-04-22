package com.flaredown.flaredownApp.Checkin.InputViews;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TreatmentTrackable;
import com.flaredown.flaredownApp.R;

/**
 * Input view for a treatments used for tracking treatments.
 */
public class TreatmentDetails extends LinearLayout implements Switch.OnClickListener{

    private TreatmentTrackable mTrackable;
    private Switch mTreatmentSwitch;
    private TextView mDose;
    private TextView mTreatmentName;
    private TextView mRemove;

    private TreatmentChangeListener mChangeListeners;

    public TreatmentDetails(final Context context, TreatmentTrackable trackable) {
        super(context);
        mTrackable = trackable;

        // Set parameters.
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER_HORIZONTAL);

        //Inflate TreatmentItem View
        View treatmentItemLayout = inflate(getContext(),R.layout.treatment_view,null);
        this.addView(treatmentItemLayout);

        mTreatmentSwitch = (Switch) treatmentItemLayout.findViewById(R.id.treatmentSwitch);
        mTreatmentSwitch.setOnClickListener(this);
        mDose = (TextView) treatmentItemLayout.findViewById(R.id.treatmentDose);
        mDose.setOnClickListener(this);
        mTreatmentName = (TextView) treatmentItemLayout.findViewById(R.id.treatmentName);
        mRemove = (TextView) treatmentItemLayout.findViewById(R.id.treatmentRemove);
        mRemove.setOnClickListener(this);

        mTreatmentName.setText(trackable.getMetaTrackable().getName());
        mDose.setText(trackable.getValue());
        if (trackable.getIsTaken()){
            mTreatmentSwitch.setChecked(true);
            toggleSwitchAndVisibility();
        } else {
            mTreatmentSwitch.setChecked(false);
            toggleSwitchAndVisibility();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.treatmentRemove){
            mChangeListeners.onRemove();
        } else if (view.getId() == R.id.treatmentDose){
            mChangeListeners.onUpdateDose("");
        } else if (view.getId() == R.id.treatmentSwitch){
            mChangeListeners.onIsTakenUpdate(mTreatmentSwitch.isChecked());
            toggleSwitchAndVisibility();
        }

    }

    private void toggleSwitchAndVisibility() {
        if (mTreatmentSwitch.isChecked()){
            mDose.setVisibility(View.VISIBLE);
            mTreatmentName.setAlpha((float) 1);
        } else {
            mDose.setVisibility(View.GONE);
            mTreatmentName.setAlpha((float) .5);
        }
    }

    public void addOnChangeListener(TreatmentChangeListener listeners) {
        mChangeListeners = listeners;
    }
}
