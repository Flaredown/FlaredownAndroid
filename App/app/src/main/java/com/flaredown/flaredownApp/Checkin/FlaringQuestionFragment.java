package com.flaredown.flaredownApp.Checkin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.flaredown.flaredownApp.R;

import org.json.JSONException;

/**
 * For displaying the "You have not check in yet page, also for implementing a future feature asking
 * the user if they are flaring today.
 */
public class FlaringQuestionFragment extends ViewPagerFragmentBase {
    private static String ARG_SUMMARY_VIEW = "Summary View";
    private boolean isSummaryView;

    private FrameLayout fl_root;
    private LinearLayout ll_not_checked_in;
    private Button bt_not_checked_in_checkin;

    /**
     * Create a new instance for the fragment.
     * @return A new fragment instance.
     * @throws JSONException
     */
    public static FlaringQuestionFragment newInstance(boolean isSummaryView) {
        FlaringQuestionFragment fragment = new FlaringQuestionFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SUMMARY_VIEW, isSummaryView);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().containsKey(ARG_SUMMARY_VIEW))
            isSummaryView = getArguments().getBoolean(ARG_SUMMARY_VIEW);
        else
            isSummaryView = false; // Setting default value if not set.
        assignViews(inflater, container);
        bt_not_checked_in_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCheckinFragment().nextPage(true);
            }
        });
        return fl_root;
    }

    private void assignViews(LayoutInflater inflater, ViewGroup container) {
        fl_root = (FrameLayout) inflater.inflate(R.layout.checkin_flaring_question_fragment, container, false);
        ll_not_checked_in = (LinearLayout) fl_root.findViewById(R.id.ll_not_checked_in);

        if(isSummaryView) {
            ll_not_checked_in.setVisibility(View.GONE);
            // Remove padding, this is only need to be done until the is flaring input is added.
            // TODO when flaring input is added.
            ScrollView scrollView = (ScrollView) fl_root.findViewById(R.id.sv_flaring_question);
            scrollView.setPadding(0,0,0,0);
        }
        bt_not_checked_in_checkin = (Button) fl_root.findViewById(R.id.bt_not_checked_in_checkin);
    }
}
