package com.flaredown.flaredownApp.FlareDown;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flaredown.flaredownApp.PreferenceKeys;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thunter on 03/09/15.
 */
public class API {
    private Context mContext;
    private static final String DEBUG_TAG = "FlareDownAPI";
    private static final String SP_USER_AUTHTOKEN = "FlareDownAPI_userauthtoken"; // String
    private static final String SP_USER_EMAIL = "FlareDownAPI_useremail"; // String
    private static final String SP_USER_SIGNED_IN = "FlareDownAPI_signedin"; // Boolean
    public static final String API_BASE_URL = "https://api-staging.flaredown.com/v1";
    public static final SimpleDateFormat API_DATE_FORMAT= new SimpleDateFormat("MMM dd yyyy");
    private static final String LOCALE_CACHE_FNAME = "localeCache";
    private SharedPreferences sharedPreferences;
    public String getEndpointUrl(String endpoint) {
        return getEndpointUrl(endpoint, new HashMap<String, String>());
    }
    public String getEndpointUrl(String endpoint, Map<String, String> params) {
        params.putAll(addAuthenticationParams());
        String url = API_BASE_URL + endpoint + "?";
        for(String key: params.keySet()) {
            url += "&" + key + "=" + params.get(key);
        }
        return url;
    }

    public API(Context context) {
        mContext = context;
        sharedPreferences = PreferenceKeys.getSharedPreferences(context);
    }
    public interface OnApiResponseArray extends OnApiResponse{
        void onSuccess(JSONArray jsonArray);
    }
    public interface OnApiResponseObject extends OnApiResponse{
        void onSuccess(JSONObject jsonObject);
    }
    public interface OnApiResponse {
        void onFailure(API_Error error);
    }


    public void users_sign_in(final String email, final String password, final OnApiResponseObject onApiResponseObject) {
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

                    getLocales(new OnApiResponseObject() {
                        @Override
                        public void onSuccess(JSONObject locales) {
                            onApiResponseObject.onSuccess(jsonResponse);
                        }

                        @Override
                        public void onFailure(API_Error error) {
                            users_sign_out_force();
                            onApiResponseObject.onFailure(error);
                        }
                    });

                    //TODO: UPDATE LOCALES ON SIGN IN

                    //onApiResponse.onSuccess(jsonResponse);
                } catch (JSONException e) {
                    onApiResponseObject.onFailure(new API_Error().setInternetConnection(true));
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    PreferenceKeys.log(PreferenceKeys.LOG_E, DEBUG_TAG, String.valueOf(error.networkResponse.statusCode));
                } catch (NullPointerException e) {
                    PreferenceKeys.log(PreferenceKeys.LOG_E, DEBUG_TAG, "503");
                }
                onApiResponseObject.onFailure(new API_Error().setVolleyError(error).setInternetConnection(true));
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

    public void users_sign_out(final OnApiResponseObject onApiResponseObject) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getEndpointUrl("/users/sign_out"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                users_sign_out_force();
                onApiResponseObject.onSuccess(new JSONObject());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onApiResponseObject.onFailure(new API_Error().setVolleyError(error).setInternetConnection(true));
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

    public void users_sign_out_force() {
        SharedPreferences.Editor sp = sharedPreferences.edit();
        sp.remove(SP_USER_AUTHTOKEN);
        sp.remove(SP_USER_EMAIL);
        sp.putBoolean(SP_USER_SIGNED_IN, false);
        sp.commit();
    }

    public void get_json_array(String endpoint, final OnApiResponseArray onApiResponseObject) {
        JsonRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, getEndpointUrl(endpoint), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                onApiResponseObject.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onApiResponseObject.onFailure(new API_Error().setVolleyError(error));
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonRequest);
    }



    public void entries(final Date date, final OnApiResponseObject onApiResponseObject) {
        Map<String, String> params = addAuthenticationParams();
        params.put("date", API_DATE_FORMAT.format(date));

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, getEndpointUrl("/entries"), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onApiResponseObject.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onApiResponseObject.onFailure(new API_Error().setVolleyError(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //return super.getParams();
                Map<String, String> postParams = addAuthenticationParams();

                postParams.put("date", API_DATE_FORMAT.format(date));

                return postParams;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonRequest);
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

    public void getLocales(OnApiResponseObject onCacheLocales) { getLocales("en", onCacheLocales);}
    public void getLocales(final String language, final OnApiResponseObject onCacheLocales) {
        PreferenceKeys.log(PreferenceKeys.LOG_I, DEBUG_TAG, "Refreshing locale file");


        StringRequest stringRequest = new StringRequest(Request.Method.GET, getEndpointUrl("/locales/" + language), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    // NEW LOCALE SAVE
                    if(Locales.updateSharedPreferences(mContext, jsonResponse.getJSONObject(language)))
                        onCacheLocales.onSuccess(jsonResponse.getJSONObject(language));
                    else
                        onCacheLocales.onFailure(new API_Error());
                } catch (Exception e) {
                    e.printStackTrace();
                    onCacheLocales.onFailure(new API_Error());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onCacheLocales.onFailure(new API_Error().setVolleyError(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return addAuthenticationParams();
            }
        };

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(stringRequest);
    }


    public boolean checkInternet() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected())
            return true;
        return false;
    }

    public class API_Error {
        public VolleyError volleyError;
        public Boolean internetConnection;
        public int statusCode = 500;

        public API_Error setVolleyError(VolleyError volleyError) {
            this.volleyError = volleyError;
            //if(volleyError.networkResponse.statusCode != null)
            try {
                this.statusCode = volleyError.networkResponse.statusCode;
                if(this.statusCode == 503) {
                    this.internetConnection = false;
                }
            } catch (NullPointerException e) {
                this.statusCode = 503;
                this.internetConnection = false;
            }
            return this;
        }
        public API_Error setInternetConnection(boolean internetConnection) {
            if(!internetConnection)
                statusCode = 503;
            if(statusCode == 503)
                this.internetConnection = false;
            else
                this.internetConnection = internetConnection;
            return this;
        }
        public String toString() {
            return "Internet Connection: " + (internetConnection ? "true" : "false") + " Status Code: " + String.valueOf(statusCode);
        }
    }
}
