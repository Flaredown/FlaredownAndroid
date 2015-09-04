package com.flaredown.flaredownApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thunter on 03/09/15.
 */
public class FlareDownAPI {
    private Context mContext;
    private static final String DEBUG_TAG = "FlareDownAPI";
    private static final String SP_USER_AUTHTOKEN = "FlareDownAPI_userauthtoken"; // String
    private static final String SP_USER_EMAIL = "FlareDownAPI_useremail"; // String
    private static final String SP_USER_SIGNED_IN = "FlareDownAPI_signedin"; // Boolean
    public static final String API_BASE_URL = "https://api-staging.flaredown.com/v1";
    private static final String LOCALE_CACHE_FNAME = "localeCache";
    public static final String getEndpointUrl(String endpoint) {
        return API_BASE_URL + endpoint;
    }
    public JSONObject locales = null;

    public FlareDownAPI(Context context) {
        mContext = context;
        locales = readCachedLocales();
    }
    public interface OnApiResponse{
        void onSuccess(JSONObject jsonObject);
        void onFailure(VolleyError error);
    }


    public void users_sign_in(final String email, final String password, final OnApiResponse onApiResponse) {
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, getEndpointUrl("/users/sign_in"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    final JSONObject jsonResponse = new JSONObject(response);
                    JSONObject jsonUser = jsonResponse.getJSONObject("user");

                    SharedPreferences.Editor sp = PreferenceKeys.getSharedPreferences(mContext).edit();

                    sp.putString(SP_USER_AUTHTOKEN, jsonUser.getString("authentication_token"));
                    sp.putString(SP_USER_EMAIL, jsonUser.getString("email"));
                    sp.putBoolean(SP_USER_SIGNED_IN, true);
                    sp.commit();

                    cacheLocales(new OnCacheLocales() {
                        @Override
                        public void onSuccess(JSONObject locales) {
                            onApiResponse.onSuccess(jsonResponse);
                        }

                        @Override
                        public void onError() {
                            onApiResponse.onFailure(null);
                        }
                    });


                    //onApiResponse.onSuccess(jsonResponse);
                } catch (JSONException e) { e.printStackTrace(); }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onApiResponse.onFailure(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("v1_user[email]", email);
                params.put("v1_user[password]", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }

    public void users_sign_out(final OnApiResponse onApiResponse) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getEndpointUrl("/users/sign_out"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SharedPreferences.Editor sp = PreferenceKeys.getSharedPreferences(mContext).edit();
                sp.remove(SP_USER_AUTHTOKEN);
                sp.remove(SP_USER_EMAIL);
                sp.putBoolean(SP_USER_SIGNED_IN, false);
                sp.commit();
                onApiResponse.onSuccess(new JSONObject());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onApiResponse.onFailure(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return addAuthenticationParams();
                //return super.getParams();
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    public boolean isLoggedIn(boolean doubleCheck) {
        SharedPreferences sp = PreferenceKeys.getSharedPreferences(mContext);


        PreferenceKeys.log(PreferenceKeys.LOG_V, DEBUG_TAG, "Checking if logged in....");
        PreferenceKeys.log(PreferenceKeys.LOG_V, DEBUG_TAG, sp.getString(SP_USER_EMAIL, "<NOTHING>"));
        PreferenceKeys.log(PreferenceKeys.LOG_V, DEBUG_TAG, sp.getString(SP_USER_AUTHTOKEN, "<NOTHING>"));
        PreferenceKeys.log(PreferenceKeys.LOG_V, DEBUG_TAG, sp.getBoolean(SP_USER_SIGNED_IN, false) ? "true" : "false");



        if(!sp.getString(SP_USER_AUTHTOKEN, "").equals("") && !sp.getString(SP_USER_EMAIL, "").equals("") && sp.getBoolean(SP_USER_SIGNED_IN, false)) {
            if(doubleCheck) {
                // TODO poll server to see if user logged in.
                return true;
            } else return true;
        } else {
            return false;
        }
    }

    public Map<String, String> addAuthenticationParams() {
        Map<String, String> params = new HashMap<>();
        if(isLoggedIn(false)) {
            SharedPreferences sp = PreferenceKeys.getSharedPreferences(mContext);
            params.put("user_email", sp.getString(SP_USER_EMAIL, ""));
            params.put("user_token", sp.getString(SP_USER_AUTHTOKEN, ""));
        }
        return params;
    }

    public JSONObject readCachedLocales() {
        File data = new File(mContext.getCacheDir().getPath() + LOCALE_CACHE_FNAME);
        try {
            if(data.exists()) {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(data));
                JSONObject jsonObject = new JSONObject((String) objectInputStream.readObject());
                objectInputStream.close();
                return jsonObject;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface OnCacheLocales {
        void onSuccess(JSONObject locales);
        void onError();
    }
    public void cacheLocales(OnCacheLocales onCacheLocales) { cacheLocales("en", onCacheLocales);}
    public void cacheLocales(final String language, final OnCacheLocales onCacheLocales) {
        PreferenceKeys.log(PreferenceKeys.LOG_I, DEBUG_TAG, "Refreshing locale file");


        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, getEndpointUrl("/locales/" + language), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    File dir = mContext.getCacheDir();
                    if (!dir.exists())
                        dir.mkdirs();
                    String path = mContext.getCacheDir().getPath() + LOCALE_CACHE_FNAME;
                    File data = new File(path);
                    if (!data.createNewFile()) {
                        data.delete();
                        data.createNewFile();
                    }

                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(data));
                    objectOutputStream.writeObject(jsonResponse.getJSONObject(language).toString());
                    objectOutputStream.close();
                    onCacheLocales.onSuccess(jsonResponse.getJSONObject(language));
                } catch (Exception e) {
                    e.printStackTrace();
                    onCacheLocales.onError();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onCacheLocales.onError();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return addAuthenticationParams();
                //
            }
        };

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(jsonObjectRequest);
    }

}
