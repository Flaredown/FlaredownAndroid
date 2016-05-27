package com.flaredown.flaredownApp.Checkin.tags;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.widget.TextView;

import com.flaredown.flaredownApp.Checkin.ViewPagerFragmentBase;
import com.flaredown.flaredownApp.Helpers.APIv2.APIResponse;
import com.flaredown.flaredownApp.Helpers.APIv2.Communicate;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Tag;
import com.flaredown.flaredownApp.Helpers.APIv2.Error;
import com.flaredown.flaredownApp.R;

import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

/**
 * Created by thunter on 26/05/16.
 */
public class TagFragment extends ViewPagerFragmentBase {

    private Communicate api;

    private FrameLayout fl_root;
    private FlowLayout fl_popular_tags;

    /**
     * Create a new instance for the fragment.
     * @return A new fragment instance.
     */
    public static TagFragment newInstance() {
        Bundle args = new Bundle();
        TagFragment fragment = new TagFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        api = new Communicate(getActivity());
        assignViews(inflater, container);
        displayPopularTags();

        return fl_root;
    }

    /**
     * Get and display the list of popular tags.
     */
    private void displayPopularTags() {
        fl_popular_tags.removeAllViews();
        api.getPopularTags(new APIResponse<List<Tag>, Error>() {
            @Override
            public void onSuccess(List<Tag> result) {
                for (Tag tag : result) {
                    TextView tv = addPopularTag(tag.getName());
                }
            }

            @Override
            public void onFailure(Error result) {
                TextView tv = addPopularTag("Error Loading Retry?");
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayPopularTags();
                    }
                });
            }
        });
    }

    private TextView addPopularTag(String tag) {
        TextView tv = new TextView(getActivity());
        tv.setTextAppearance(getActivity(), R.style.AppTheme_TextView_Link);
        tv.setText(tag);
        fl_popular_tags.addView(tv);
        return tv;
    }

    /**
     * Inflates and assigns views.
     * @param inflater
     * @param container
     */
    private void assignViews(LayoutInflater inflater, ViewGroup container) {
        fl_root = (FrameLayout) inflater.inflate(R.layout.checkin_fragment_tags, container, false);
        fl_popular_tags = (FlowLayout) fl_root.findViewById(R.id.fl_popular_tags);
    }
}
