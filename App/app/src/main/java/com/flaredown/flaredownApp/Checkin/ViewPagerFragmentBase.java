package com.flaredown.flaredownApp.Checkin;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.flaredown.flaredownApp.Helpers.API.EntryParser.*;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.CheckIn;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The base class for each page of the check in
 */
public abstract class ViewPagerFragmentBase extends Fragment {

    private CheckinActivity activity;

    @Override
    public void onAttach(Context context) {
        this.activity = (CheckinActivity) getActivity();
        super.onAttach(context);
    }

    public CheckinActivity getCheckInActivity() {
        return activity;
    }

    /**
     * Called when the page becomes visible.
     */
    public void onPageEnter() {

    }

    /**
     * Called on page exit.
     */
    public void onPageExit() {

    }

    public void addUpdateListener() {

    }
}
