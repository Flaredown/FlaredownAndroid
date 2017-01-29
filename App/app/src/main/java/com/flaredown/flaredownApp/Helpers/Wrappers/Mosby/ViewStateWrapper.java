package com.flaredown.flaredownApp.Helpers.Wrappers.Mosby;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.AbsParcelableLceViewState;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

/**
 * Mosby ViewState wrapper, provide extra functionality to the android application and should be used in
 * place of the Mosby AbsParcelableLceViewState class.
 *
 * Things to note when setting up.
 * ParcelablePlease does not work! You need to include each item manually do this by using this
 * annotation: @ParcelablePlease(allFields = false). To select fields to parcel use the following
 * annotation: @ParcelableThisPlease.
 *
 * @param <D> The underlying data model.
 * @param <V> The View interface.
 */

/*
ParcelablePlease library does not work because the Generic Type param. (it thinks it does not
 implement the parcelable so has to be manually done). Side note using the @Bag annotation does not
 work b/c you cannot edit the class relating to the issue (not with out copying each one then
 updating would be difficult).
 */
public abstract class ViewStateWrapper<D extends Parcelable, V extends ViewWrapper<D>> extends AbsParcelableLceViewState<D, V>{



    /**
     * Keep track if the view is loading something.
     */
    @ParcelableThisPlease
    public boolean isLoading = false;

    @Override
    public void apply(V view, boolean retained) {
        if(isLoading) {
            view.showLoading(false);
        } else {
            view.hideLoading();
        }

        super.apply(view, retained);
    }

    /**
     * Set the view's state to loading.
     * @param pullToRefresh doesn't matter
     */
    @Deprecated
    @Override
    public void setStateShowLoading(boolean pullToRefresh) {
        setStateShowLoading();
    }

    /**
     * Set the view's state to loading.
     */
    public void setStateShowLoading() {
        this.isLoading = true;
    }

    /**
     * Set the view's state to not loading.
     */
    public void setStateHideLoading() {
        this.isLoading = false;
    }

    public D getLoadedData() {
        return loadedData;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        // Manually parceling the loaded data object (as it is a generic)
        if(loadedData == null)
            dest.writeInt(0); // Set object null flag to false
        else {
            dest.writeInt(1); // Set object null flag to true
            dest.writeSerializable(loadedData.getClass()); // Store the class... enabling the reading of the object.
            dest.writeParcelable(loadedData, flags); // Storing the actual object.
        }


    }

    @Override
    protected void readFromParcel(Parcel in) {
        super.readFromParcel(in);

        if(in.readInt() == 1) {
            loadedData = in.readParcelable(((Class<?>)in.readSerializable()).getClassLoader()); // Read the object.
        } else {
            loadedData = null;
        }
    }
}
