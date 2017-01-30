package com.flaredown.flaredownApp.Helpers.Wrappers.Mosby;

import android.os.Parcelable;

import com.flaredown.flaredownApp.Helpers.Wrappers.Android.ActivityWrapper;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

/**
 * Mosby View wrapper, provide extra functionality to the android application and should be used in
 * place of the Mosby MvpLceView class.
 * @param <M> The model type.
 */

public interface ViewWrapper<M extends Parcelable> extends MvpLceView<M> {
    public ActivityWrapper getActivityw();

    public FragmentWrapper getFragment();
}
