package com.flaredown.flaredownApp.Checkin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private JSONObject argResponseJson;
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
    public static Checkin_summary_fragment newInstance(JSONObject entryJson, JSONObject responseJson, Date date) {
        Checkin_summary_fragment fragment = new Checkin_summary_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_ENTRY_JSON, entryJson.toString());
        args.putString(ARG_RESPONSE_JSON, responseJson.toString());
        args.putLong(ARG_DATE_JSON, date.getTime());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                argEntryJson = new JSONObject(getArguments().getString(ARG_ENTRY_JSON));
                argResponseJson = new JSONObject(getArguments().getString(ARG_RESPONSE_JSON));
                argEntryJson.getJSONObject("entry").put("responses", argResponseJson.getJSONArray("responses"));
            } catch (JSONException e) {
                argEntryJson = new JSONObject();
                argResponseJson = new JSONObject();
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
            //fragments = CheckinActivity.createFragments(argEntryJson.getJSONObject("entry"));
            fragments = CheckinActivity.createFragments(EntryParsers.getCatalogDefinitions(argEntryJson.getJSONObject("entry"), new JSONArray())); //TODO pass responses
            int i = 0;
            for (ViewPagerFragmentBase fragment : fragments) {
                getChildFragmentManager().beginTransaction().add(ll_fragmentHolder.getId(), fragment, "summaryfrag"+i).commit();
                fragment.addOnUpdateListener(new ViewPagerFragmentBase.OnResposneUpdate() {
                    @Override
                    public void onUpdate(EntryParsers.CatalogDefinition answer) { //todo fix... use the catalog definitions
                        /*try {
                            JSONArray responseArray = argResponseJson.getJSONArray("responses");
                            for(int i = 0; i < responseArray.length(); i++) {
                                JSONObject responseItem = responseArray.getJSONObject(i);
                                if(responseItem.getString("name").equals(answer.getString("name")) && responseItem.getString("catalog").equals(answer.getString("catalog"))) {
                                    responseArray.getJSONObject(i).put("value", answer.get("value"));
                                    break;
                                }
                            }


                            flaredownAPI.submitEntry(argDate, argResponseJson, new API.OnApiResponse<JSONObject>() {
                                @Override
                                public void onFailure(API_Error error) {
                                    new DefaultErrors(getActivity(), error);
                                }

                                @Override
                                public void onSuccess(JSONObject result) {
                                    Toast.makeText(getActivity(), (result.optBoolean("success", false))? "DONE" : "FAILED", Toast.LENGTH_LONG).show(); // TODO A better confirmation

                                }
                            });
                        } catch (JSONException e) {e.printStackTrace();}*/
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public void onStop() {
        super.onStop();
        JSONArray responseArray = argResponseJson.optJSONArray("responses");
        if(responseArray == null) responseArray = new JSONArray();

        for (ViewPagerFragmentBase fragment : fragments) {
            JSONArray fragmentArray = fragment.activityClosing();
            for(int i = 0; i < fragmentArray.length(); i++) {
                JSONObject fragmentObject = fragmentArray.optJSONObject(i);
                if(fragmentObject != null)
                    responseArray.put(fragmentObject);
            }
        }

        JSONObject responseObject = new JSONObject();
        try {
            responseObject.put("responses", responseArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        flaredownAPI.submitEntry(argDate, responseObject, new API.OnApiResponse<JSONObject>() {
            @Override
            public void onFailure(API_Error error) {
                new DefaultErrors(getActivity(), error);
            }

            @Override
            public void onSuccess(JSONObject result) {
                try {
                    Toast.makeText(getActivity(), (result.optBoolean("success", false))? "DONE" : "FAILED", Toast.LENGTH_LONG).show(); // TODO A better confirmation
                } catch (NullPointerException e) { // If the application has been closed a null pointer is thrown
                }
            }
        });
    }
}
