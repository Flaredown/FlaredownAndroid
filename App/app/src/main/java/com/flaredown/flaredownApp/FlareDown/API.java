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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private static final String SP_ENTRIES_CACHE = "FlareDownAPI_entries_cache";
    public static final String API_BASE_URL = "https://api-staging.flaredown.com/v1";
    public static final SimpleDateFormat API_DATE_FORMAT= new SimpleDateFormat("MMM-dd-yyyy");
    private static final String LOCALE_CACHE_FNAME = "localeCache";
    public static final String CHAR_SET = "UTF-8";
    //public static final Date currentDate = new Date(new Date().getTime() + (1000*60*60*24));
    public static final Date currentDate = new Date();
    private SharedPreferences sharedPreferences;
    public String getEndpointUrl(String endpoint) {
        return getEndpointUrl(endpoint, new HashMap<String, String>());
    }
    public String getEndpointUrl(String endpoint, Boolean authparams) {
        return getEndpointUrl(endpoint, new HashMap<String, String>(), authparams);
    }
    public String getEndpointUrl(String endpoint, Map<String, String> params) {
        return getEndpointUrl(endpoint, params, true);
    }
    public String getEndpointUrl(String endpoint, Map<String, String> params, Boolean authprams) {
        try {
            //endpoint = URLEncoder.encode(endpoint, CHAR_SET);
            if(authprams)
                params.putAll(addAuthenticationParams());
            String url = API_BASE_URL + endpoint + "?";
            for (String key : params.keySet()) {
                key = URLEncoder.encode(key, CHAR_SET);
                String value = URLEncoder.encode(params.get(key), CHAR_SET);
                url += "&" + key + "=" + value;
            }
            return url;
        } catch (UnsupportedEncodingException e) { e.printStackTrace(); return "";}
    }

    /**
     * Initiates the API library which is used to communicate with the FlareDownAPI
     * @param context
     */
    public API(Context context) {
        mContext = context;
        sharedPreferences = PreferenceKeys.getSharedPreferences(context);
    }
    public interface OnApiResponse<T> {
        void onFailure(API_Error error);
        void onSuccess(T result);
    }

    /**
     * Sends API request to server to log the user in, auth token is automatically stored and for
     * future api requests the user will be logged in.
     * @param email Users email address.
     * @param password Users password.
     * @param onApiResponse Callback with the response from the endpoint /users/sign_in.
     */
    public void users_sign_in(final String email, final String password, final OnApiResponse<JSONObject> onApiResponse) {
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

                    getLocales(new OnApiResponse<JSONObject>() {
                        @Override
                        public void onSuccess(JSONObject locales) {
                            onApiResponse.onSuccess(jsonResponse);
                        }

                        @Override
                        public void onFailure(API_Error error) {
                            users_sign_out_force();
                            onApiResponse.onFailure(error);
                        }
                    });

                    //TODO: UPDATE LOCALES ON SIGN IN

                    //onApiResponse.onSuccess(jsonResponse);
                } catch (JSONException e) {
                    onApiResponse.onFailure(new API_Error().setInternetConnection(true));
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
                onApiResponse.onFailure(new API_Error().setVolleyError(error).setInternetConnection(true));
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

    /**
     * Logs off the current user, removing only the usertoken from shareprefs.
     * @param onApiResponse Callback with the response from the API.
     */
    public void users_sign_out(final OnApiResponse<JSONObject> onApiResponse) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getEndpointUrl("/users/sign_out"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                users_sign_out_force();
                onApiResponse.onSuccess(new JSONObject());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onApiResponse.onFailure(new API_Error().setVolleyError(error).setInternetConnection(true));
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

    /**
     * Retrieves the end point /current_user, provides details regarding the current user. Requires
     * user to be signed in.
     * @param onApiResponse Callback with the response from the API.
     */
    public void current_user(final OnApiResponse<JSONObject> onApiResponse) {
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, getEndpointUrl("/current_user"), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onApiResponse.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onApiResponse.onFailure(new API_Error().setVolleyError(error));
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonRequest);
    }

    /**
     * Gets a list of trackables/editables from the API
     * @param catalog which catalog do you wish to filter down to.
     * @param onApiResponse Callback with the response from the API
     */
    public void getEditables(final String catalog, final OnApiResponse<List<String>> onApiResponse) {
        Date currentDate = API.currentDate; // Getting entries endpoint for today.
        entries(currentDate, new OnApiResponse<JSONObject>() {
            @Override
            public void onFailure(API_Error error) {
                onApiResponse.onFailure(error);
            }

            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject json_catalogsDefinitions = result.getJSONObject("entry").getJSONObject("catalog_definitions");
                    JSONArray json_catalogDefinitions = json_catalogsDefinitions.getJSONArray(catalog);

                    List<String> returnList = new ArrayList<String>();

                    for (int i = 0; i < json_catalogDefinitions.length(); i++) {
                        JSONArray editableArray = json_catalogDefinitions.getJSONArray(i);
                        for (int j = 0; j < editableArray.length(); j++) {
                            JSONObject editable = editableArray.getJSONObject(j);
                            returnList.add(editable.getString("name"));
                        }
                    }
                    onApiResponse.onSuccess(returnList);

                } catch (JSONException e) {
                    e.printStackTrace();
                    PreferenceKeys.log(PreferenceKeys.LOG_E, DEBUG_TAG, result.toString());
                    onApiResponse.onFailure(new API_Error().setStatusCode(500));
                }
            }
        });
    }

    /**
     * Forcefully logs the user out. No communication to the server occurs, only the
     * sharedpreference key for the token is removed.
     */
    public void users_sign_out_force() {
        SharedPreferences.Editor sp = sharedPreferences.edit();
        sp.remove(SP_USER_AUTHTOKEN);
        sp.remove(SP_USER_EMAIL);
        sp.putBoolean(SP_USER_SIGNED_IN, false);
        sp.commit();
    }

    /**
     * Retrieves a JSONArray from the API using the GET method.
     * @param endpoint The endpoint you wish to receive the JSONArray from.
     * @param onApiResponse Callback with the response from the API server.
     * @return Returns the volley request queue it is using.
     */
    public RequestQueue get_json_array(String endpoint, final OnApiResponse<JSONArray> onApiResponse) {
        JsonRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, getEndpointUrl(endpoint), null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                onApiResponse.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onApiResponse.onFailure(new API_Error().setVolleyError(error));
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonRequest);
        return requestQueue;
    }


    /**
     * Retrieves the endpoint /entries
     * @param date The entry date to fetch.
     * @param onApiResponse Callback with the response from the API server.
     */
    public void entries(final Date date, final OnApiResponse<JSONObject> onApiResponse) {
        Map<String, String> params = addAuthenticationParams();
        params.put("date", API_DATE_FORMAT.format(date));

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, getEndpointUrl("/entries"), new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onApiResponse.onSuccess(response);
                SharedPreferences.Editor sp = PreferenceKeys.getSharedPreferences(mContext).edit();

                sp.putString(SP_ENTRIES_CACHE, response.toString());
                sp.commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onApiResponse.onFailure(new API_Error().setVolleyError(error));
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

    public void submitEntry(final Date date, final JSONObject response, final OnApiResponse<JSONObject> onApiResponse) {
        HashMap<String, String> params = new HashMap<>();
        params.put("entry", response.toString());

        JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, getEndpointUrl("/entries/" + API_DATE_FORMAT.format(date) + ".json", params), response, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("success") && response.getBoolean("success") == true) {
                        onApiResponse.onSuccess(response);
                    } else {
                        onApiResponse.onFailure(new API_Error().setStatusCode(500));
                    }
                } catch (JSONException e) {
                    onApiResponse.onFailure(new API_Error().setStatusCode(500));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onApiResponse.onFailure(new API_Error().setVolleyError(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //return super.getParams();
                Map<String, String> postParams = addAuthenticationParams();
                return postParams;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(putRequest);
    }

    /**
     * Removes trackable from user, only for the current day.
     * @param catalog The catalog the trackable is from.
     * @param id The id of the trackable.
     * @param onApiResponse Callback with the response from the API server.
     */
    public void delete_trackable(String catalog, int id, final OnApiResponse<String> onApiResponse) {
        if(catalog == null || catalog.equals("")) {
            onApiResponse.onFailure(new API_Error().setStatusCode(500));
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, getEndpointUrl("/" + catalog + "/" + id), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onApiResponse.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onApiResponse.onFailure(new API_Error().setVolleyError(error));
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    /**
     * Removes trackable from user, only for the current day.
     * @param catalog The catalog the trackable is from.
     * @param name The name of the trackable.
     * @param onApiResponse Callback with the response from the API server
     */
    public void delete_trackableByName(final String catalog, final String name, final OnApiResponse<String> onApiResponse) {
        if(catalog == null || catalog.equals("") || name == null || name.equals("")) {
            onApiResponse.onFailure(new API_Error().setStatusCode(500));
            return;
        }

        current_user(new OnApiResponse<JSONObject>() {
            @Override
            public void onFailure(API_Error error) {
                onApiResponse.onFailure(error);
            }

            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray catalogItems = result.getJSONArray(catalog);
                    boolean found = false;
                    for (int i = 0; i < catalogItems.length(); i++) {
                        JSONObject catalogItem = catalogItems.getJSONObject(i);
                        String name2 = catalogItem.getString("name");
                        Integer id = catalogItem.getInt("id");
                        if (name.equals(name2)) {
                            delete_trackable(catalog, id, onApiResponse);
                            found = true;
                        }
                    }
                    if (!found) {
                        onApiResponse.onSuccess("");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    onApiResponse.onFailure(new API_Error().setStatusCode(500));
                }
            }
        });

    }

    /**
     * Creates a new trackable for the current user.
     * @param catalog The catalog you wish to add to.
     * @param name The new name of the trackable.
     * @param onApiResponse Callback with the response from the API server.
     */
    public void create_trackable(String catalog, String name, final OnApiResponse<JSONObject> onApiResponse) {
        switch (catalog) {
            case "symptoms":
            case "treatments":
            case "conditions":
                break;
            default:
                onApiResponse.onFailure(new API_Error().setStatusCode(500));
                return;
        }
        if(name == null || name.equals("")) {
            onApiResponse.onFailure(new API_Error().setStatusCode(500));
            return;
        }
        final Map<String, String> params = addAuthenticationParams();
        params.put("name", name);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getEndpointUrl("/" + catalog + "/"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    onApiResponse.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                    onApiResponse.onFailure(new API_Error().setStatusCode(500));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
               return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

    /**
     * Checks if the user is logged in, it does not check with the server if this is true.
     * @return if true the user is signed in.
     */
    public boolean isLoggedIn() {
        SharedPreferences sp = PreferenceKeys.getSharedPreferences(mContext);


        PreferenceKeys.log(PreferenceKeys.LOG_V, DEBUG_TAG, "Checking if logged in....");
        PreferenceKeys.log(PreferenceKeys.LOG_V, DEBUG_TAG, sp.getString(SP_USER_EMAIL, "<NOTHING>"));
        PreferenceKeys.log(PreferenceKeys.LOG_V, DEBUG_TAG, sp.getString(SP_USER_AUTHTOKEN, "<NOTHING>"));
        PreferenceKeys.log(PreferenceKeys.LOG_V, DEBUG_TAG, sp.getBoolean(SP_USER_SIGNED_IN, false) ? "true" : "false");



        if(!sp.getString(SP_USER_AUTHTOKEN, "").equals("") && !sp.getString(SP_USER_EMAIL, "").equals("") && sp.getBoolean(SP_USER_SIGNED_IN, false)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates a map with the authentication params which is sent with each api request to
     * authenticate the user for each transaction. Which maybe added onto to add extra parameters.
     * @return Map<String, String> of the user details.
     */
    public Map<String, String> addAuthenticationParams() {
        Map<String, String> params = new HashMap<>();
        if(isLoggedIn()) {
            SharedPreferences sp = PreferenceKeys.getSharedPreferences(mContext);
            params.put("user_email", sp.getString(SP_USER_EMAIL, ""));
            params.put("user_token", sp.getString(SP_USER_AUTHTOKEN, ""));
        }
        return params;
    }

    /**
     * Retrieves the locals from endpoint /locals/en (default is english) if another language use
     * getLocals(language, onApiResponse).
     * @param onApiResponse Callback with the response from the API server.
     */
    public void getLocales(OnApiResponse<JSONObject> onApiResponse) { getLocales("en", onApiResponse);}

    /**
     * Retrieves the locals from endpoint /locals.
     * @param language The language prefix. Currently only en is available.
     * @param onApiResponse Callback with the response from the API server.
     */
    public void getLocales(final String language, final OnApiResponse<JSONObject> onApiResponse) {
        PreferenceKeys.log(PreferenceKeys.LOG_I, DEBUG_TAG, "Refreshing locale file");


        StringRequest stringRequest = new StringRequest(Request.Method.GET, getEndpointUrl("/locales/" + language), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    // NEW LOCALE SAVE
                    if(Locales.updateSharedPreferences(mContext, jsonResponse.getJSONObject(language)))
                        onApiResponse.onSuccess(jsonResponse.getJSONObject(language));
                    else
                        onApiResponse.onFailure(new API_Error());
                } catch (Exception e) {
                    e.printStackTrace();
                    onApiResponse.onFailure(new API_Error());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onApiResponse.onFailure(new API_Error().setVolleyError(error));
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

    /**
     * Checks if there is an internet connection.
     * @return returns true if the device is connected to the internet.
     */
    public boolean checkInternet() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected())
            return true;
        return false;
    }

}
