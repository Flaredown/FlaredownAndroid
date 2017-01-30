package com.flaredown.flaredownApp.Helpers.Wrappers.Mosby;

import android.os.Parcelable;

import com.flaredown.flaredownApp.Helpers.Wrappers.Android.ActivityWrapper;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

/**
 * Mosby Fragment wrapper, provide extra functionality to the android application and should be used in
 * place of the Mosby MvpBasePresenter class.
 *
 * @param <V> The View interface.
 * @param <M> The underlying data model.
 */

public abstract class PresenterWrapper<V extends ViewWrapper<M>, M extends Parcelable>
        extends MvpBasePresenter<V>
        implements MvpPresenter<V>{

    public FragmentWrapper getFragment() {
        if(isViewAttached())
            return getView().getFragment();
        else
            return null;
    }

    public ActivityWrapper getActivity() {
        if(isViewAttached())
            return getView().getActivityw();
        return null;
    }

    public void runOnUiThread(Runnable runnable) {
        ActivityWrapper activity = getActivity();
        if(activity != null) {
            activity.runOnUiThread(runnable);
        }
    }
}
