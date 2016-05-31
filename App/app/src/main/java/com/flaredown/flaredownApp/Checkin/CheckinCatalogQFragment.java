package com.flaredown.flaredownApp.Checkin;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaredown.flaredownApp.Checkin.InputViews.InputContainerView;
import com.flaredown.flaredownApp.Checkin.InputViews.SmileyRating;
import com.flaredown.flaredownApp.Checkin.InputViews.SmileyRatingOnValueChange;
import com.flaredown.flaredownApp.Checkin.InputViews.TreatmentChangeListener;
import com.flaredown.flaredownApp.Checkin.InputViews.TreatmentDetails;
import com.flaredown.flaredownApp.Helpers.APIv2.APIResponse;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.Trackable;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TreatmentTrackable;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings.Tracking;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings.Trackings;
import com.flaredown.flaredownApp.Helpers.APIv2.Error;
import com.flaredown.flaredownApp.Helpers.FlaredownConstants;
import com.flaredown.flaredownApp.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

public class CheckinCatalogQFragment extends ViewPagerFragmentBase{
    // Fragment Arguments.
    private static final String ARG_CHECKIN = "checkin argument";
    private static final String ARG_TRACKABLE_TYPE = "trackable type argument";

    // Views
    private FrameLayout fl_root;
    private TextView tv_catalog;
    private TextView tv_question;
    private LinearLayout ll_questionHolder;
    private TextView tv_addTrackable;

    public TrackableType getTrackableType() {
        return trackableType;
    }

    private TrackableType trackableType;
    private ArrayList<InputContainerView> inputContainerViews = new ArrayList<>();

    /**
     * Create a new instance for the fragment.
     * @return A new fragment instance.
     * @throws JSONException
     */
    public static CheckinCatalogQFragment newInstance(TrackableType trackableType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRACKABLE_TYPE, trackableType);
        CheckinCatalogQFragment fragment = new CheckinCatalogQFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Bundle bundleArguments = getArguments();
            trackableType = (TrackableType) bundleArguments.getSerializable(ARG_TRACKABLE_TYPE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        assignViews(layoutInflater, viewGroup);
        tv_catalog.setText(trackableType.getNameResId());
        tv_question.setText(trackableType.getQuestionResId());
        inflateQuestions();

        tv_addTrackable.setText("Add " + trackableType.toString().toLowerCase());
        tv_addTrackable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddEditableActivity.startActivity(getActivity(), trackableType);
            }
        });
        return fl_root;
    }

    /**
     * Inflates and assigns view fields.
     * @param layoutInflater
     * @param viewGroup
     */
    private void assignViews(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        fl_root = (FrameLayout) layoutInflater.inflate(R.layout.checkin_fragment_catalog_q, viewGroup, false);
        tv_catalog = (TextView) fl_root.findViewById(R.id.tv_catalog);
        tv_question = (TextView) fl_root.findViewById(R.id.tv_question);
        ll_questionHolder = (LinearLayout) fl_root.findViewById(R.id.ll_questionHolder);
        tv_addTrackable = (TextView) fl_root.findViewById(R.id.tv_addTrackable);
    }

    private void inflateQuestions() {
        ArrayList<Trackable> trackables = getCheckInActivity().getCheckIn().getTrackables(trackableType);
        for (final Trackable trackable : trackables) {
            addTrackable(trackable);
        }
    }

    public void addTrackable(final Trackable trackable){
        if (trackable.getType() == TrackableType.TREATMENT){
            try {
                final TreatmentTrackable treatment;
                if (trackable instanceof  TreatmentTrackable){
                    treatment = (TreatmentTrackable)trackable;
                } else {
                    treatment = new TreatmentTrackable(trackable);
                }
                TreatmentDetails treatmentDetailsView = new TreatmentDetails(getActivity(),treatment);
                final InputContainerView inputContainerView = new InputContainerView(getActivity(), trackable).setInputView(treatmentDetailsView);
                treatmentDetailsView.addOnChangeListener(new TreatmentChangeListener() {
                    @Override
                    public void onIsTakenUpdate(boolean isTaken) {
                        treatment.setIsTaken(isTaken);
                        getCheckInActivity().checkInUpdate();
                    }
                    @Override
                    public void onRemove() {
                        getCheckInActivity().API.getTrackings(trackable.getType(), Calendar.getInstance(), new APIResponse<Trackings, Error>() {
                            @Override
                            public void onSuccess(Trackings trackings) {
                                for (Tracking tracking : trackings){
                                    if (tracking.getTrackable_id() == ((TreatmentTrackable) trackable).getTreatment_id()){
                                        getCheckInActivity().API.removeTrackings(tracking.getId(), new APIResponse<String, Error>() {
                                            @Override
                                            public void onSuccess(String result) {
                                                trackable.setDestroy("1");
                                                getCheckInActivity().checkInUpdate();
                                                getCheckInActivity().getCheckIn().removeTrackable(trackable);
                                                inputContainerView.setVisibility(View.GONE);
                                            }
                                            @Override
                                            public void onFailure(Error result) {
                                                Log.d("Remove Tracking Error",result.toString());
                                            }
                                        });
                                    }
                                }
                            }
                            @Override
                            public void onFailure(Error result) {
                            }
                        });
                    }
                    @Override
                    public void onUpdateDose(String dose) {
                        treatment.setValue(dose);
                        getCheckInActivity().checkInUpdate();
                    }
                });
                inputContainerViews.add(inputContainerView);
                ll_questionHolder.addView(inputContainerView);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            SmileyRating smileyRating = new SmileyRating(getActivity());
            if (trackable.getValue() != null){
                smileyRating.setValue(Integer.valueOf(trackable.getValue()));
            } else {
                smileyRating.setValue(null);
            }
            smileyRating.addOnValueChangeListener(new SmileyRatingOnValueChange() {
                @Override
                public void onClick(int value, Integer oldValue) {
                    trackable.setValue(String.valueOf(value));
                    getCheckInActivity().checkInUpdate();
                }
            });
            try {
                InputContainerView inputContainerView = new InputContainerView(getActivity(), trackable);
                inputContainerView.setQuestionTitle(trackable.getMetaTrackable().getName());
                inputContainerView.setInputView(smileyRating);
                inputContainerViews.add(inputContainerView);
                ll_questionHolder.addView(inputContainerView);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeTrackable(Trackable trackable){
        //TODO: Implement delete trackables
    }
}