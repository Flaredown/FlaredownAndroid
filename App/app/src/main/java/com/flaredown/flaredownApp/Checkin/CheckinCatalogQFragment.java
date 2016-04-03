package com.flaredown.flaredownApp.Checkin;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flaredown.flaredownApp.Checkin.InputViews.InputContainerView;
import com.flaredown.flaredownApp.Checkin.InputViews.SmileyRating;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.CheckIn;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.Trackable;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.R;

import org.json.JSONException;

import java.util.ArrayList;

public class CheckinCatalogQFragment extends ViewPagerFragmentBase {
    // Fragment Arguments.
    private static final String ARG_CHECKIN = "checkin argument";
    private static final String ARG_TRACKABLE_TYPE = "trackable type argument";

    // Views
    private FrameLayout fl_root;
    private TextView tv_catalog;
    private TextView tv_question;
    private LinearLayout ll_questionHolder;

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
    }

    private void inflateQuestions() {
        ArrayList<Trackable> trackables = getCheckInActivity().getCheckIn().getTrackables(trackableType);
        for (Trackable trackable : trackables) {
            SmileyRating smileyRating = new SmileyRating(getContext());

            InputContainerView inputContainerView = new InputContainerView(getContext(), trackable)
                    .setQuestionTitle("TEMP")
                    .setInputView(smileyRating);

            inputContainerViews.add(inputContainerView);
            ll_questionHolder.addView(inputContainerView);
        }
    }
}