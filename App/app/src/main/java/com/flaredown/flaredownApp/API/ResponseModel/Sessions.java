package com.flaredown.flaredownApp.API.ResponseModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.flaredown.flaredownApp.API.ServerParsingException;
import com.flaredown.flaredownApp.Helpers.PreferenceKeys;
import com.google.gson.annotations.SerializedName;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * <p>Session object returned from [POST] api/sessions</p>
 * <p><b>Sample request:</b></p>
 * <pre>
 *     {
 *          "id": 1,
 *          "created_at": "2015-08-10T22:07:44.466Z",
 *          "updated_at": "2017-02-15T20:59:53.547Z",
 *          "user_id": 936,
 *          "email": "th@thomashunter.co.uk",
 *          "token": "21fdf2e74f20207007af459ea56da225",
 *          "settings": {
 *              "base_url": "https://app.flaredown.com",
 *              "notification_channel": "private-936",
 *              "facebook_app_id": "340751662715210",
 *              "discourse_url": "https://talk.flaredown.com/",
 *              "discourse_enabled": true
 *          }
 *      }
 * </pre>
 */

@ParcelablePlease
public class Sessions implements Parcelable {

    /**
     * Create a Session object from a json string.
     * @param json The json values for the object.
     * @return The object created from the json.
     * @throws ServerParsingException
     */
    public static Sessions createFromJson(String json) throws ServerParsingException {
        return ParsingHelper.parseJson(Sessions.class, json);
    }

    public static Sessions getFromSharedPreferences(Context context) throws ServerParsingException {
        SharedPreferences sp = PreferenceKeys.getSharedPreferences(context);
        return Sessions.createFromJson(sp.getString(PreferenceKeys.USER_SESSION, ""));
    }

    public void storeSession(Context context, boolean blocking) {
        SharedPreferences.Editor spe = PreferenceKeys.getSharedPreferences(context).edit();
        spe.putString(PreferenceKeys.USER_SESSION, toJson());
        if(blocking)
            spe.commit();
        else
            spe.apply();
    }

    public String toJson(){
        return ParsingHelper.getGson().toJson(this);
    }

    public int id;
    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("updated_at")
    public String updatedAt;

    public String email;

    public String password;

    //region Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    //endregion

    //region ParcelablePlease.
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        SessionsParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<Sessions> CREATOR = new Creator<Sessions>() {
        public Sessions createFromParcel(Parcel source) {
            Sessions target = new Sessions();
            SessionsParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public Sessions[] newArray(int size) {
            return new Sessions[size];
        }
    };
    //endregion
}
