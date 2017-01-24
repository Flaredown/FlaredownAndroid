package com.flaredown.flaredownApp.Activities.Login;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * Model for the {@link LoginFragment}
 */
@ParcelablePlease
public class LoginModel implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        LoginModelParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<LoginModel> CREATOR = new Creator<LoginModel>() {
        public LoginModel createFromParcel(Parcel source) {
            LoginModel target = new LoginModel();
            LoginModelParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public LoginModel[] newArray(int size) {
            return new LoginModel[size];
        }
    };
}
