package com.flaredown.flaredownApp.Helpers.Wrappers.Mosby;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.flaredown.flaredownApp.Helpers.Wrappers.Android.ActivityWrapper;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;

/**
 * Mosby Fragment wrapper, provide extra functionality to the android application and should be used in
 * place of the Mosby MvpLceViewStateFragment class.
 *
 *
 * @param <CV> The view type for the view with the id = R.id.contentView.
 * @param <M> The underlying data model that will be used to display the view.
 * @param <V> The View interface that must be implemented by this view.
 * @param <P> The type of Presenter.
 * @param <VS> The type for the ViewState.
 */
public abstract class FragmentWrapper<CV extends View, M extends Parcelable, V extends ViewWrapper<M>, P extends MvpPresenter<V>, VS extends ViewStateWrapper<M, V>>
        extends MvpLceViewStateFragment<CV, M, V, P> implements ViewWrapper<M>{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // Handle rotations etc better.
    }

    @Override
    public ActivityWrapper getActivityw() {
        return (ActivityWrapper) getActivity();
    }

    @Override
    public FragmentWrapper getFragment() {
        return this;
    }

    public VS getViewState() {
        return (VS) super.getViewState();
    }


}
