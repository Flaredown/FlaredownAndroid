package com.flaredown.flaredownApp.Checkin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flaredown.flaredownApp.FlareDown.API;
import com.flaredown.flaredownApp.FlareDown.API_Error;
import com.flaredown.flaredownApp.FlareDown.DefaultErrors;
import com.flaredown.flaredownApp.FlareDown.Locales;
import com.flaredown.flaredownApp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Checkin_summary_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Checkin_summary_fragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ENTRY_JSON = "entryJson";
    private static final String ARG_RESPONSE_JSON = "responseJson";
    private static final String ARG_DATE_JSON = "date";

    private JSONObject argEntryJson;
    private JSONArray argResponseJson;
    private List<EntryParsers.CollectionCatalogDefinition> collectionCatalogDefinitions;
    private Date argDate;
    private View root;
    private LinearLayout ll_fragmentHolder;
    private List<ViewPagerFragmentBase> fragments;
    private TextView tv_checkinSuccess;
    private API flaredownAPI;

    public Checkin_summary_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param entryJson The entry json retrieved for checkin
     * @param responseJson The response json submitted for checkin.
     * @return A new instance of fragment Checkin_summary_fragment.
     */
    public static Checkin_summary_fragment newInstance(JSONObject entryJson, JSONArray responseJson, Date date) {
        Checkin_summary_fragment fragment = new Checkin_summary_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_ENTRY_JSON, entryJson.toString());
        args.putString(ARG_RESPONSE_JSON, responseJson.toString());
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
                argResponseJson = new JSONArray(getArguments().getString(ARG_RESPONSE_JSON));
                argDate = new Date(getArguments().getLong(ARG_DATE_JSON));
                collectionCatalogDefinitions = EntryParsers.getCatalogDefinitions(argEntryJson, argResponseJson);
            } catch (JSONException e) {
                argEntryJson = new JSONObject();
                argResponseJson = new JSONArray();
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
        //try {
            //fragments = CheckinActivity.createFragments(argEntryJson.getJSONObject("entry"));
            fragments = CheckinActivity.createFragments(collectionCatalogDefinitions); //TODO pass responses
            int i = 0;
            for (ViewPagerFragmentBase fragment : fragments) {
                getChildFragmentManager().beginTransaction().add(ll_fragmentHolder.getId(), fragment, "summaryfrag"+i).commit();
                fragment.addOnUpdateListener(new ViewPagerFragmentBase.OnResposneUpdate() {
                    @Override
                    public void onUpdate(EntryParsers.CatalogDefinition catalogDefinition) {
                        try {
                            if (catalogDefinition.getResponse() != null) {
                                EntryParsers.findCatalogDefinition(collectionCatalogDefinitions, catalogDefinition.getCatalog(), catalogDefinition.getName()).setResponse(catalogDefinition.getResponse());
                                argResponseJson = EntryParsers.getResponsesJSONCatalogDefinitionList(collectionCatalogDefinitions);

                                final API.OnApiResponse responseListener = new API.OnApiResponse<JSONObject>() {
                                    @Override
                                    public void onFailure(API_Error error) {
                                        try {
                                            new DefaultErrors(getActivity(), error.setDebugString("Checkin_summary_fragment:assembleFragments:submition"));
                                        } catch (NullPointerException e) {e.printStackTrace();}
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
                                flaredownAPI.submitEntry(argDate, argResponseJson, responseListener);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });
                fragment.onPageExit();
            }
       // } catch (JSONException e) {
        //    e.printStackTrace();
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_checkin_summary, container, false);
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
        outState.putString(ARG_ENTRY_JSON, EntryParsers.getCatalogDefinitionsJSON(collectionCatalogDefinitions).toString());
        outState.putString(ARG_RESPONSE_JSON, EntryParsers.getResponsesJSONCatalogDefinitionList(collectionCatalogDefinitions).toString());
        outState.putLong(ARG_DATE_JSON, argDate.getTime());
    }

    @Override
    public void onStop() {
        super.onStop();
        for (ViewPagerFragmentBase fragment : fragments) {
            fragment.onPageExit();
        }
    }
}
