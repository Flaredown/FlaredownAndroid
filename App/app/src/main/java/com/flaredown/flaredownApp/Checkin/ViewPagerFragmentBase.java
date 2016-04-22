package com.flaredown.flaredownApp.Checkin;

import android.content.Context;
import android.support.v4.app.Fragment;

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
