package com.flaredown.flaredownApp.Checkin.tags;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flaredown.flaredownApp.Checkin.AddEditableActivity;
import com.flaredown.flaredownApp.Checkin.ViewPagerFragmentBase;
import com.flaredown.flaredownApp.Helpers.APIv2.APIResponse;
import com.flaredown.flaredownApp.Helpers.APIv2.Communicate;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.Tag;
import com.flaredown.flaredownApp.Helpers.APIv2.Error;
import com.flaredown.flaredownApp.Helpers.FlowLayoutHelper;
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
    private FlowLayoutHelper<Tag> flh_popular_tags;

    private FlowLayout fl_selected_tags;
    private FlowLayoutHelper<Tag> flh_selected_tags;

    private TextView tv_addTag;

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

    /**
     * Add a tag to the selected tags for the check in and update the check in object and api.
     * @param tag The tag to add to the check in.
     */
    public void addTag(Tag tag) {
        if(flh_selected_tags != null) {
            flh_selected_tags.addItem(tag);
            // Update the check in.
            getCheckInActivity().getCheckIn().addTag(tag);
            getCheckInActivity().checkInUpdate();
        }
    }

    /**
     * Remove a tag from the selected tags for the check in and update the check in object and api.
     * @param tag The tag to remove from the check in.
     */
    public void removeTag(Tag tag) {
        if(flh_selected_tags != null) {
            flh_selected_tags.removeItem(tag);
            // Update the check in.
            getCheckInActivity().getCheckIn().removeTag(tag);
            getCheckInActivity().checkInUpdate();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        api = new Communicate(getActivity());
        assignViews(inflater, container);
        displayPopularTags();

        tv_addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditableActivity.startActivity(getActivity(), TrackableType.TAG);
            }
        });

        // Show already selected tags.
        flh_selected_tags.addItems(getCheckInActivity().getCheckIn().getTags());

        return fl_root;
    }

    /**
     * Get and display the list of popular tags.
     */
    private void displayPopularTags() {
        flh_popular_tags.clear();
        fl_popular_tags.addView(new ProgressBar(getActivity()));
        api.getPopularTags(new APIResponse<List<Tag>, Error>() {
            @Override
            public void onSuccess(List<Tag> result) {
                flh_popular_tags.clear();
                for (Tag tag : result) {
                    flh_popular_tags.addItem(tag);
                }
            }

            @Override
            public void onFailure(Error result) {
                fl_popular_tags.removeAllViews();
                TextView tv = new TextView(getActivity());
                tv.setText("Error Loading Retry?");
                fl_popular_tags.addView(tv);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayPopularTags();
                    }
                });
            }
        });
    }

    /**
     * Inflates and assigns views.
     * @param inflater
     * @param container
     */
    private void assignViews(LayoutInflater inflater, ViewGroup container) {
        fl_root = (FrameLayout) inflater.inflate(R.layout.checkin_fragment_tags, container, false);
        tv_addTag = (TextView) fl_root.findViewById(R.id.tv_add_tag);
        fl_popular_tags = (FlowLayout) fl_root.findViewById(R.id.fl_popular_tags);
        flh_popular_tags = new FlowLayoutHelper<>(fl_popular_tags, new FlowLayoutHelper.Adapter<Tag>() {
            @Override
            public View viewCreation(final Tag item) {
                TextView tv = new TextView(getActivity());
                tv.setText(item.getMetaTrackable().getName());
                tv.setTextAppearance(getActivity(), R.style.AppTheme_TextView_Link);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addTag(item);
                    }
                });
                int padding = (int) getResources().getDimension(R.dimen.sep_margin_small);
                tv.setPadding(padding, padding, padding, padding);
                return tv;
            }
        });

        fl_selected_tags = (FlowLayout) fl_root.findViewById(R.id.fl_selected_tags);
        flh_selected_tags = new FlowLayoutHelper<>(fl_selected_tags, new FlowLayoutHelper.Adapter<Tag>() {
            @Override
            public View viewCreation(final Tag item) {
                LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.checkin_template_tag, null, false);
                TextView tv = (TextView) linearLayout.findViewById(R.id.tv_tag);
                tv.setText(item.getMetaTrackable().getName());
//                ((ViewGroup.MarginLayoutParams) tv.getLayoutParams()).setMargins(100, 100, 100, 100);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeTag(item);
                    }
                });
                return linearLayout;
            }
        });
    }
}