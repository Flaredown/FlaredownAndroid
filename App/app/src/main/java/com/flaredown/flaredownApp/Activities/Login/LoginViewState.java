package com.flaredown.flaredownApp.Activities.Login;

import android.os.Parcel;
import android.os.Parcelable;

import com.flaredown.flaredownApp.Helpers.Wrappers.Mosby.ViewStateWrapper;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.hannesdorfmann.parcelableplease.annotation.ParcelableThisPlease;

/**
 * Created by thunter on 12/01/2017.
 */

@ParcelablePlease(allFields = false)
public class LoginViewState extends ViewStateWrapper<LoginModel, LoginView> implements Parcelable {

    @ParcelableThisPlease
    public boolean isLoading = false;

    @ParcelableThisPlease
    public String errorEmailField = null;

    @ParcelableThisPlease
    public String errorPasswordField = null;


    final int STATE_SPLASH_SCREEN = 50;


    @Override
    public void apply(LoginView view, boolean retained) {
        apply(view, retained, true);
    }

    public void apply(LoginView view, boolean retained, boolean loading) {
        if(loading) {
            if (isLoading) {
                view.showLoading(true);
            } else {
                view.hideLoading();
            }
        }

        view.showError(exception, retained);

        switch (currentViewState) {
            case STATE_SPLASH_SCREEN:
                view.showSplashScreen();
                break;
            case STATE_SHOW_CONTENT:
                view.setFieldErrorMessages(errorEmailField, errorPasswordField);
                super.apply(view, retained);
            default:
                super.apply(view, retained);
        }
    }

    @Deprecated
    @Override
    public void setStateShowLoading(boolean pullToRefresh) {
        this.setStateShowLoading();
    }

    @Override
    public void setStateShowError(Throwable e, boolean pullToRefresh) {
        // Not setting current state as other items can be visible when displaying an error.
        exception = e;
    }

    public void setStateShowLoading() {
        this.isLoading = true;
    }

    public void setStateHideLoading() {
        this.isLoading = false;
    }

    public void setStateSplashScreen() {
        this.currentViewState = STATE_SPLASH_SCREEN;
    }

    /**
     * Set state: for the email and password fields.
     * @param email Email field error message.
     * @param password Password field error message.
     */
    public void setStateErrorFieldMessages(String email, String password) {
        this.errorEmailField = email;
        this.errorPasswordField = password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        LoginViewStateParcelablePlease.writeToParcel(this, dest, flags);
        super.writeToParcel(dest, flags);
    }

    public static final Creator<LoginViewState> CREATOR = new Creator<LoginViewState>() {
        public LoginViewState createFromParcel(Parcel source) {
            LoginViewState target = new LoginViewState();
            LoginViewStateParcelablePlease.readFromParcel(target, source);
            target.readFromParcel(source);
            return target;
        }

        public LoginViewState[] newArray(int size) {
            return new LoginViewState[size];
        }
    };
}
