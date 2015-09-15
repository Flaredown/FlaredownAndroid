package com.flaredown.flaredownApp.FlareDown;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flaredown.flaredownApp.PreferenceKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by thunter on 03/09/15.
 */
public class Locales {
    private static final String DEBUG_TAG = "FlareDownLocales";
    private static final String SHARED_PREFERENCES_KEY = "com.flaredown.flaredownApp.locales";
    private static final int SHARED_PREFERENCES_MODE = Context.MODE_PRIVATE;
    private static final String LOCALE_URL = "https://api-staging.flaredown.com/v1/locales/en";


    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_KEY, SHARED_PREFERENCES_MODE);
    }

    /**
     * Loads the locales to a sharedpreferences xml file.
     */
    public static boolean updateSharedPreferences(final Context context, JSONObject locales) {
        PreferenceKeys.log(PreferenceKeys.LOG_V, DEBUG_TAG, "Updating locales");

        SharedPreferences.Editor sp_editor = Locales.getSharedPreferences(context).edit();
        sp_editor.clear();
        try {
            addToPreferences(context, locales, sp_editor);
            sp_editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void addToPreferences(Context context, JSONObject object, SharedPreferences.Editor sp_editor) throws JSONException{
        addToPreferences(context, "", object, sp_editor);
    }
    private static void addToPreferences(Context context, String prefix, JSONObject object, SharedPreferences.Editor sp_editor) throws JSONException{
        Iterator<?> keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if(object.get(key) instanceof JSONObject)
                addToPreferences(context, prefix + key + ".", object.getJSONObject(key), sp_editor);
            if((object.get(key) instanceof JSONArray))
                addToPreferences(context, prefix + key + ".", object.getJSONArray(key), sp_editor);
            else {
                sp_editor.putString(prefix + key, object.getString(key));
            }
        }
    }
    private static void addToPreferences(Context context, String prefix, JSONArray jsonArray, SharedPreferences.Editor sp_editor) throws JSONException {
        for(int i = 0; i < jsonArray.length(); i++) {
            if(jsonArray.get(i) instanceof JSONObject)
                addToPreferences(context, prefix + String.valueOf(i) + ".", jsonArray.getJSONObject(i), sp_editor);
            else if(jsonArray.get(i) instanceof JSONArray)
                addToPreferences(context, prefix + String.valueOf(i) + ".", jsonArray.getJSONArray(i), sp_editor);
            else
                sp_editor.putString(prefix + String.valueOf(i), jsonArray.getString(i));
        }
    }
}