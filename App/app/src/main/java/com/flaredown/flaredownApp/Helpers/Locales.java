package com.flaredown.flaredownApp.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;

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
            else
                sp_editor.putString(prefix + key, object.getString(key));
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


    /**
     * Read the shared preferences including string replacement.
     */

    public static class Reader{
        private Context context;
        private String result;
        private String key;
        private boolean successful = false;
        public Reader(Context context, String result, boolean successful, String key) {
            this.context = context;
            this.result = result;
            this.successful = successful;
            this.key = key;
        }
        public Reader replace(String key, String value) {
            result = result.replaceAll("\\{\\{" + key + "\\}\\}", value);
            return this;
        }
        public String create() {
            return result;
        }
        public android.text.Spanned createAT() {
            return Html.fromHtml(result);
        }
        public Reader capitalize1Char() {
            if(result.length() == 1) {
                result = result.toUpperCase();
            } else if(result.length() > 1) {
                result = result.substring(0, 1).toUpperCase() + result.substring(1);
            }

            if(!result.substring(result.length() - 1).equals(".")) {
                result += ".";
            }
            return this;
        }
        public Reader resultIfUnsuccessful(String failedResult) {
            if(!successful)
                result = failedResult;
            return this;
        }
        public Reader useKeyIfUnsuccessful() {
            if(!successful)
                result = key;
            return this;
        }
    }
    public static Reader read(Context context, String key) {
        SharedPreferences sp = getSharedPreferences(context);
        boolean successful = false;
        if(sp.contains(key)) {
            successful = true;
        }
        String result = sp.getString(key, "_LOCALE_ERROR_");

        return new Reader(context, result, successful, key);
    }
}