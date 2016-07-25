package com.flaredown.flaredownApp.Checkin;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flaredown.flaredownApp.Checkin.InputViews.InputContainerView;
import com.flaredown.flaredownApp.Checkin.InputViews.SmileyRating;
import com.flaredown.flaredownApp.Checkin.InputViews.TreatmentChangeListener;
import com.flaredown.flaredownApp.Checkin.InputViews.TreatmentDetails;
import com.flaredown.flaredownApp.Helpers.APIv2.APIResponse;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.ObservableHashSet;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.Trackable;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableCollection;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TreatmentTrackable;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings.Tracking;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings.Trackings;
import com.flaredown.flaredownApp.Helpers.APIv2.Error;
import com.flaredown.flaredownApp.Helpers.APIv2.ErrorDialog;
import com.flaredown.flaredownApp.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import rx.Subscriber;

public class CheckinCatalogQFragment extends ViewPagerFragmentBase{
    // Fragment Arguments.
    private static final String ARG_CHECKIN = "checkin argument";
    private static final String ARG_TRACKABLE_TYPE = "trackable type argument";

    // Views
    private FrameLayout fl_root;
    private TextView tv_catalog;
    private TextView tv_question;
    private LinearLayout ll_contentHolder;
    private ListView lv_questionHolder;
    private TextView tv_addTrackable;

    // Array for displaying current questions.
    private List<Trackable> currentQuestions = new ArrayList<>();
    private QuestionAdapter currentQuestionsAdapter;
    Subscriber<TrackableCollection.CollectionChange> trackableSubscriber;

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

        currentQuestions.clear(); // Remove any existing items inside the array
        currentQuestionsAdapter = new QuestionAdapter(getActivity(), currentQuestions);

        assignViews(layoutInflater, viewGroup);

        // Set up observers
        trackableSubscriber = new Subscriber<TrackableCollection.CollectionChange>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(TrackableCollection.CollectionChange collectionChange) {
                switch (collectionChange.getChangeType()) {
                    case ADD:
                        currentQuestions.add((Trackable) collectionChange.getObject());
                        currentQuestionsAdapter.notifyDataSetChanged();
                        break;
                    case REMOVE:
                        currentQuestions.remove(collectionChange.getObject());
                        currentQuestionsAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };

        getCheckInActivity().getCheckIn().getTrackables(trackableType).getCollectionObservable().subscribe(trackableSubscriber);

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
        ll_contentHolder = (LinearLayout) fl_root.findViewById(R.id.ll_contentHolder);
        lv_questionHolder = (ListView) fl_root.findViewById(R.id.lv_questionHolder);
        tv_addTrackable = (TextView) fl_root.findViewById(R.id.tv_addTrackable);
    }

    private void inflateQuestions() {
        lv_questionHolder.setAdapter(currentQuestionsAdapter);
        HashSet<Trackable> trackables = getCheckInActivity().getCheckIn().getTrackables(trackableType);
        for (final Trackable trackable : trackables) {
            trackableSubscriber.onNext(new ObservableHashSet.CollectionChange(trackable, ObservableHashSet.ChangeType.ADD));
        }
    }

    public void removeTrackable(Trackable trackable){
        //TODO: Implement delete trackables
    }

    /**
     * Array Adaptor for the questions list view.
     */
    private class QuestionAdapter extends ArrayAdapter<Trackable> {
        public QuestionAdapter(Context context, List<Trackable> trackables) {
            super(context, 0, trackables);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the item for this position.
            Trackable trackable = getItem(position);
            // Check if existing view is being 'reused', otherwise inflate the view.
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.checkin_question_blank, parent, false);
            }

            TextView tv_question = (TextView) convertView.findViewById(R.id.tv_question); // Question title.
            FrameLayout fl_inputContainer = (FrameLayout) convertView.findViewById(R.id.fl_inputContainer); // Contains the input view.
            fl_inputContainer.removeAllViews();

            SmileyRating sr_input = new SmileyRating(getContext());
            sr_input.setGravity(Gravity.START);
            fl_inputContainer.addView(sr_input);

            // Set the question title.
            tv_question.setText(trackable.getMetaTrackable().getName());

            return convertView;
        }
    }
}