package com.flaredown.flaredownApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Iterator;

/**
 * Created by thunter on 03/09/15.
 */
public class FlareDownLocales {
    private static final String DEBUG_TAG = "FlareDownLocales";
    private static final String SHARED_PREFERENCES_KEY = "com.flaredown.flaredownApp.locales";
    private static final int SHARED_PREFERENCES_MODE = Context.MODE_PRIVATE;
    private static final String LOCALE_URL = "https://api-staging.flaredown.com/v1/locales/en";


    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_KEY, SHARED_PREFERENCES_MODE);
    }

    public static void updateSharedPreferences(final Context context) {
        PreferenceKeys.log(PreferenceKeys.LOG_V, DEBUG_TAG, "Updating locales");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, LOCALE_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    PreferenceKeys.log(PreferenceKeys.LOG_I, DEBUG_TAG, "Finished loading locales");
                    JSONObject root = response.getJSONObject(response.keys().next());
                    getSharedPreferences(context).edit().clear();
                    addToPreferences(context, root);

                    Toast.makeText(context, getSharedPreferences(context).getString("occupation_options.1", "NOTHING"), Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PreferenceKeys.log(PreferenceKeys.LOG_E, DEBUG_TAG, "Error loading locales...." + error.getLocalizedMessage());
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(jsonObjectRequest);
    }
    private static void addToPreferences(Context context, JSONObject object) throws JSONException{ addToPreferences(context, "", object); }
    private static void addToPreferences(Context context, String prefix, JSONObject object) throws JSONException{
        SharedPreferences.Editor prefs = getSharedPreferences(context).edit();

        Iterator<?> keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if(object.get(key) instanceof JSONObject) addToPreferences(context, prefix + key + ".", object.getJSONObject(key));
            if((object.get(key) instanceof JSONArray)) {
                addToPreferences(context, prefix + key + ".", object.getJSONArray(key));
            } else {
                prefs.putString(prefix + key, object.getString(key));
            }
        }
        prefs.commit();
    }
    private static void addToPreferences(Context context, String prefix, JSONArray jsonArray) throws JSONException {
        SharedPreferences.Editor prefs = getSharedPreferences(context).edit();

        for(int i = 0; i < jsonArray.length(); i++) {
            if(jsonArray.get(i) instanceof JSONObject)
                addToPreferences(context, prefix + String.valueOf(i) + ".", jsonArray.getJSONObject(i));
            else if(jsonArray.get(i) instanceof JSONArray)
                addToPreferences(context, prefix + String.valueOf(i) + ".", jsonArray.getJSONArray(i));
            else
                prefs.putString(prefix + String.valueOf(i), jsonArray.getString(i));
        }
        prefs.commit();
    }
}