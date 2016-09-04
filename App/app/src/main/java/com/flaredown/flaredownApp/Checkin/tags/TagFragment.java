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
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.ObservableHashSet;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TagCollection;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.Tag;
import com.flaredown.flaredownApp.Helpers.APIv2.Error;
import com.flaredown.flaredownApp.Helpers.FlowLayoutHelper;
import com.flaredown.flaredownApp.R;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by thunter on 26/05/16.
 */
public class TagFragment extends ViewPagerFragmentBase {

    private static TagCollection<Tag> popularTags;

    private Communicate api;

    private FrameLayout fl_root;
    private FlowLayout fl_popular_tags;
    private FlowLayoutHelper<Tag> flh_popular_tags;
    private ProgressBar pb_popularTags;

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
        getCheckInActivity().getCheckIn().addTag(tag);
    }

    /**
     * Remove a tag from the selected tags for the check in and update the check in object and api.
     * @param tag The tag to remove from the check in.
     */
    public void removeTag(Tag tag) {
        getCheckInActivity().getCheckIn().removeTag(tag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        api = new Communicate(getActivity());
        assignViews(inflater, container);
        Subscriber<TagCollection.CollectionChange<Tag>> popularTagsSubscriber = new Subscriber<TagCollection.CollectionChange<Tag>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(TagCollection.CollectionChange<Tag> collectionChange) {
                switch (collectionChange.getChangeType()) {
                    case ADD:
                        flh_popular_tags.addItem((Tag) collectionChange.getObject());
                        break;
                    case REMOVE:
                        flh_popular_tags.removeItem((Tag) collectionChange.getObject());
                        break;
                }

                if(popularTags.size() == 0 && pb_popularTags.getVisibility() != View.VISIBLE) {
                    pb_popularTags.setVisibility(View.VISIBLE);
                } else if(popularTags.size() > 0 && pb_popularTags.getVisibility() != View.GONE) {
                    pb_popularTags.setVisibility(View.GONE);
                }
            }
        };

        Subscriber<TagCollection.CollectionChange<Tag>> selectedTagsSubscriber = new Subscriber<TagCollection.CollectionChange<Tag>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(TagCollection.CollectionChange<Tag> tagCollectionChange) {
                switch (tagCollectionChange.getChangeType()) {
                    case ADD:
                        flh_selected_tags.addItem(tagCollectionChange.getObject());
                        break;
                    case REMOVE:
                        flh_selected_tags.removeItem(tagCollectionChange.getObject());
                        break;
                }
            }
        };

        if(popularTags == null) {
            popularTags = new TagCollection<>();
            displayPopularTags();
        } else {
            for (Tag popularTag : popularTags) {
                popularTagsSubscriber.onNext(new TagCollection.CollectionChange(popularTag, TagCollection.ChangeType.ADD));
            }
        }

        tv_addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEditableActivity.startActivity(getActivity(), TrackableType.TAG, getCheckInActivity().getCheckIn());
            }
        });

        // Show already selected tags.
        flh_selected_tags.addItems(getCheckInActivity().getCheckIn().getTags());


        popularTags.subscribeCollectionObservable(popularTagsSubscriber);
        getCheckInActivity().getCheckIn().getTags().subscribeCollectionObservable(selectedTagsSubscriber);

        return fl_root;
    }

    /**
     * Get and display the list of popular tags.
     */
    private void displayPopularTags() {
        popularTags.clear();
        api.getPopularTags(new APIResponse<List<Tag>, Error>() {
            @Override
            public void onSuccess(List<Tag> result) {
                popularTags.clear();
                for (Tag tag : result) {
                    popularTags.add(tag);
                }
            }

            @Override
            public void onFailure(Error result) {
                popularTags.clear();
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
        pb_popularTags = new ProgressBar(getActivity());
        fl_popular_tags.addView(pb_popularTags);
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
