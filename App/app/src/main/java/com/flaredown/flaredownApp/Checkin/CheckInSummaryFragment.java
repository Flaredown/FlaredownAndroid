package com.flaredown.flaredownApp.Checkin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flaredown.flaredownApp.Helpers.API.API;
import com.flaredown.flaredownApp.Helpers.API.API_Error;
import com.flaredown.flaredownApp.Helpers.API.EntryParser.*;
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
    private static final String ARG_ENTRY_JSON = "entryJson";
    private static final String ARG_DATE_JSON = "date";

    private JSONObject argEntryJson;
    private Entry entry;
    private Date argDate;
    private View root;
    private LinearLayout ll_fragmentHolder;
    private List<ViewPagerFragmentBase> fragments;
    private TextView tv_checkinSuccess;
    private API flaredownAPI;

    public CheckInSummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param entry The entry for the check in
     * @return A new instance of fragment Checkin_summary_fragment.
     */
    public static CheckInSummaryFragment newInstance(Entry entry, Date date) throws JSONException{
        CheckInSummaryFragment fragment = new CheckInSummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ENTRY_JSON, entry.toJSONObject().toString());
        args.putLong(ARG_DATE_JSON, date.getTime());
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
        setRetainInstance(true);
        if (getArguments() != null) {
            try {
                argEntryJson = new JSONObject(getArguments().getString(ARG_ENTRY_JSON));
                argDate = new Date(getArguments().getLong(ARG_DATE_JSON));
                entry = new Entry(argEntryJson);
            } catch (Exception e) {
                argEntryJson = new JSONObject();
                e.printStackTrace();
            }
            argDate = new Date(getArguments().getLong(ARG_DATE_JSON, new Date().getTime()));
        }
    }

    private void initUI() {
        ll_fragmentHolder = (LinearLayout) root.findViewById(R.id.ll_fragmentholder);
        tv_checkinSuccess = (TextView) root.findViewById(R.id.tv_checkinSuccess);
        tv_checkinSuccess.setText(Locales.read(getActivity(), "summary_title").createAT());
        assembleFragments();
    }



    private void assembleFragments() {
        try {
            fragments = CheckinActivity.createFragments(entry);
            int i = 0;
            for (ViewPagerFragmentBase fragment : fragments) {
                getChildFragmentManager().beginTransaction().add(ll_fragmentHolder.getId(), fragment, "summaryfrag" + i).commit();
                fragment.addOnUpdateListener(new ViewPagerFragmentBase.OnResposneUpdate() {
                    @Override
                    public void onUpdate(CatalogDefinition catalogDefinition) {
                        try {
                            final API.OnApiResponse responseListener = new API.OnApiResponse<JSONObject>() {
                                @Override
                                public void onFailure(API_Error error) {
                                    try {
                                        new DefaultErrors(getActivity(), error.setDebugString("Checkin_summary_fragment:assembleFragments:submission"));
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onSuccess(JSONObject result) {
                                    try {
                                        if (result.optBoolean("success", false))
                                            Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                        else
                                            new DefaultErrors(getActivity(), new API_Error().setStatusCode(500).setDebugString("Checkin_summary_fragment:assembleFragments:returnFalse"));
                                    } catch (NullPointerException e) {
                                        // If the activity closes too early getActivity returns null and crashes the app.
                                    }
                                }
                            };
                            flaredownAPI.submitEntry(argDate, entry.getResponses(), responseListener);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (JSONException e) {
            new DefaultErrors(getActivity(), new API_Error().setDebugString("CheckInSummaryFragment:assembleFragments.JSONException").setStatusCode(500));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.checkin_fragment_summary, container, false);
        if(getActivity() instanceof CheckinActivity) {
            flaredownAPI = ((CheckinActivity) getActivity()).flareDownAPI;
        } else
            flaredownAPI = new API(getActivity());

        initUI();
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putString(ARG_ENTRY_JSON, entry.toJSONObject().toString());
            outState.putLong(ARG_DATE_JSON, argDate.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        for (ViewPagerFragmentBase fragment : fragments) {
            fragment.onPageExit();
        }
    }
}
