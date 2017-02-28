package com.flaredown.flaredownApp.API_old.ResponseModel;

import android.os.Parcelable;

import com.flaredown.flaredownApp.API_old.ServerParsingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by thunter on 15/02/2017.
 */

abstract class ParsingHelper {

    static Gson getGson() {
        return new GsonBuilder().create();
    }

    static <I extends Parcelable> I parseJson(Class<I> clss, String json) throws ServerParsingException{
        try {
            return getGson().fromJson(json, clss);
        } catch (Throwable e) {
            throw new ServerParsingException("Failed to parse json", e);
        }
    }
}
