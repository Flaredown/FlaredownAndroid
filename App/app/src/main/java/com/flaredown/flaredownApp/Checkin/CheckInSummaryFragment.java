package com.flaredown.flaredownApp.Checkin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaredown.flaredownApp.Helpers.API.API;
import com.flaredown.flaredownApp.Helpers.API.API_Error;
import com.flaredown.flaredownApp.Helpers.API.EntryParser.*;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.CheckIn;
import com.flaredown.flaredownApp.Helpers.DefaultErrors;
import com.flaredown.flaredownApp.Helpers.Locales;
import com.flaredown.flaredownApp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckInSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckInSummaryFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private View root;
    private CheckinActivity activity;
    private LinearLayout ll_fragmentHolder;
    private List<ViewPagerFragmentBase> fragments;
    private TextView tv_checkinSuccess;

    public CheckInSummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Checkin_summary_fragment.
     */
    public static CheckInSummaryFragment newInstance() throws JSONException{
        CheckInSummaryFragment fragment = new CheckInSummaryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Returns a list of fragments used in the summary view.
     * @return List of fragments used in the summary view.
     */
    public List<ViewPagerFragmentBase> getFragments() {
        return fragments;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (CheckinActivity) getActivity();
        setRetainInstance(true);
    }

    private void initUI() {
        ll_fragmentHolder = (LinearLayout) root.findViewById(R.id.ll_fragmentholder);
        tv_checkinSuccess = (TextView) root.findViewById(R.id.tv_checkinSuccess);
        assembleFragments();
    }



    private void assembleFragments() {
        fragments = CheckinActivity.createFragments(activity.getCheckIn());
        fragments.add(0, NotesQFragment.newInstance());
        int i = 0;
        for (ViewPagerFragmentBase fragment : fragments) {
            getChildFragmentManager().beginTransaction().add(ll_fragmentHolder.getId(), fragment, "summaryfrag" + i).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.checkin_fragment_summary, container, false);
        initUI();
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        for (ViewPagerFragmentBase fragment : fragments) {
            fragment.onPageExit();
        }
    }
}
