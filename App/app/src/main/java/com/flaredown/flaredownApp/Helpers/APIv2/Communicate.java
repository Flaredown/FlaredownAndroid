package com.flaredown.flaredownApp.Helpers.APIv2;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.CheckIn;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.CheckIns;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.MetaTrackable;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Searches.Search;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Session.Session;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings.Trackings;
import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;
import com.flaredown.flaredownApp.Helpers.PreferenceKeys;
import com.flaredown.flaredownApp.Helpers.Volley.JsonObjectExtraRequest;
import com.flaredown.flaredownApp.Helpers.Volley.QueueProvider;
import com.flaredown.flaredownApp.Helpers.Volley.WebAttributes;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Profile.Country;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Profile.Profile;
import com.flaredown.flaredownApp.Models.Treatment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Contains methods used for communicating with the API.
 */
public class Communicate {


    private Context context;

    /**
     * Create a communication's class.
     * @param context The context for the activity.
     */
    public Communicate(Context context) {
        this.context = context;
    }

    /**
     * Login, this will sign in a user and store there credentials.
     * @param email The email of the user.
     * @param password The password for the user.
     */
    public void userSignIn(String email, String password, final APIResponse<Session, Error> apiResponse){
        final WebAttributes parameters = new WebAttributes();
        parameters.put("user[email]", email);
        parameters.put("user[password]", password);

        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.POST, EndPointUrl.getAPIUrl("sessions"), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Session session = new Session(response);
                // Save the user details to remain logged in.
                SharedPreferences sp = PreferenceKeys.getSharedPreferences(context);
                SharedPreferences.Editor spe = sp.edit();
                spe.putString(PreferenceKeys.SP_Av2_USER_EMAIL, session.getEmail());
                spe.putString(PreferenceKeys.SP_Av2_USER_TOKEN, session.getToken());
                spe.putString(PreferenceKeys.SP_Av2_USER_ID, session.getUserId());
                spe.apply();

                apiResponse.onSuccess(session);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.userSignIn::VolleyError"));
            }
        }).setParams(parameters);
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }

    /**
     * Get the check in object for a specific identification. // TODO needs testing.
     * @param id id of the checkin
     */
    public void checkIn(String id, final APIResponse<CheckIn, Error> apiResponse) {
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("checkins/" + id), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final CheckIn checkIn = new CheckIn(response);
                    final ArrayList<ArrayList<MetaTrackable>> completeCount = new ArrayList<>();

                    for (final TrackableType trackableType : TrackableType.values()) {
                        getTrackable(trackableType, checkIn.getTrackableIds(trackableType), new APIResponse<ArrayList<MetaTrackable>, Error>() {
                            @Override
                            public void onSuccess(ArrayList<MetaTrackable> result) {
                                for (MetaTrackable metaTrackable : result) {
                                    checkIn.attachMetaTrackables(trackableType, metaTrackable);
                                }
                                completeCount.add(result);

                                if (completeCount.size() >= TrackableType.values().length) {
                                    apiResponse.onSuccess(checkIn);
                                }
                            }

                            @Override
                            public void onFailure(Error result) {
                                apiResponse.onFailure(result);
                            }
                        });
                    }
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.chackIn::ParseError"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.checkIn::VolleyError"));
            }
        });
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }

    /**
     * Get the check in object for a specific date. // TODO needs testing.
     * @param date The date for the check in.
     */
    public void checkIn(final Calendar date, final APIResponse<CheckIn, Error> apiResponse) {
        WebAttributes getParams = new WebAttributes();
        getParams.put("date", Date.calendarToString(date));
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("checkins", getParams), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    CheckIns checkIns = new CheckIns(response);
                    if (checkIns.size() <= 0) {
                        // No check in found, so create one, then download information
//                        createCheckIn(date, apiResponse);
                        createCheckIn(date, new APIResponse<CheckIn, Error>() {
                            @Override
                            public void onSuccess(CheckIn result) {
                                checkIn(result.getId(), apiResponse);
                            }

                            @Override
                            public void onFailure(Error result) {
                                apiResponse.onFailure(result);
                            }
                        });
                    } else {
                        final CheckIn checkIn = checkIns.get(0);
                        final ArrayList<ArrayList<MetaTrackable>> completeCount = new ArrayList<>();

                        for (final TrackableType trackableType : TrackableType.values()) {
                            getTrackable(trackableType, checkIn.getTrackableIds(trackableType), new APIResponse<ArrayList<MetaTrackable>, Error>() {
                                @Override
                                public void onSuccess(ArrayList<MetaTrackable> result) {
                                    for (MetaTrackable metaTrackable : result) {
                                        checkIn.attachMetaTrackables(trackableType, metaTrackable);
                                    }
                                    completeCount.add(result);

                                    if (completeCount.size() >= TrackableType.values().length) {
                                        apiResponse.onSuccess(checkIn);
                                    }
                                }

                                @Override
                                public void onFailure(Error result) {
                                    apiResponse.onFailure(result);
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.checkInDate::Exception"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.checkInDate::VolleyError"));
            }
        });
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }

    /**
     * Tell the API to create a check in for a specific date, note if a check in already exists an
     * error is returned. (Check in object is returned via the api response listener).
     * @param date The date for the check in to be created on.
     * @param apiResponse Getting the response from the api, including the check in object for the
     *                    date.
     */
    public void createCheckIn(Calendar date, final APIResponse<CheckIn, Error> apiResponse) {
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.POST, EndPointUrl.getAPIUrl("checkins"), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    apiResponse.onSuccess(new CheckIn(response));
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.createCheckIn::JSONException"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.createCheckIn::Volley"));
            }
        });
        try {
            JSONObject rootJObject = new JSONObject();
            JSONObject checkin = new JSONObject();
            rootJObject.put("checkin", checkin);
            checkin.put("date", Date.calendarToString(date));
            jsonObjectExtraRequest.setRequestBody(rootJObject.toString());

            WebAttributes headers = new WebAttributes();
            headers.put("Content-Type", "application/json");
            jsonObjectExtraRequest.setHeaders(headers);

            QueueProvider.getQueue(context).add(jsonObjectExtraRequest);

        } catch (JSONException e) {
            apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.createCheckIn::JSONException2"));
        }
    }

    public CheckIn checkInBlocking(final Calendar date) {
        WebAttributes getParams = new WebAttributes();
        getParams.put("date", Date.calendarToString(date));
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("checkins", getParams), future, future);
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);

        try {
            JSONObject response =  future.get(15, TimeUnit.SECONDS);
            CheckIns checkIns = new CheckIns(response);
            if (checkIns.size() > 0){
                return checkIns.get(0);
            }
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        } catch (TimeoutException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
        return null;
    }

    public void submitCheckin(CheckIn checkIn, final APIResponse<CheckIn, Error> apiResponse) {
        try {
            JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.PUT, EndPointUrl.getAPIUrl("checkins/" + checkIn.getId()), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        apiResponse.onSuccess(new CheckIn(response));
                    } catch (JSONException e) {
                        apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.submitCheckin::JSONException"));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.submitCheckin::volley"));
                }
            });
            jsonObjectExtraRequest.setRequestBody(checkIn.getResponseJson().toString());
            WebAttributes headers = new WebAttributes();
            headers.put("Content-Type", "application/json");
            jsonObjectExtraRequest.setHeaders(headers);
            QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
        } catch (JSONException e) {
            apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.submitCheckin::JSONException2"));
        }
    }

    /**
     * Checks if user credentials are saved, you can assume the user is signed in, however the API is
     * not contacted.
     * @return Returns true if user credentials are stored.
     */
    public boolean isCredentialsSaved() {
        SharedPreferences sp = PreferenceKeys.getSharedPreferences(context);
        return sp.getString(PreferenceKeys.SP_Av2_USER_EMAIL, null) != null && sp.getString(PreferenceKeys.SP_Av2_USER_TOKEN, null) != null && sp.getString(PreferenceKeys.SP_Av2_USER_ID, null) != null;
    }

    /**
     * Get the trackings object for a specific trackable type.
     * @param type The trackable type i.e. Treatment, Symptom...
     * @param date The date for the trackable
     */
    public void getTrackings(TrackableType type, Calendar date, final APIResponse<Trackings, Error> apiResponse) {
        WebAttributes getParams = new WebAttributes();
        getParams.put("at", Date.calendarToString(date));
        getParams.put("trackable_type",type.getTrackingsFormattedType());

        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("trackings", getParams), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Trackings trackings = new Trackings(response);
                    if(trackings.size() <= 0) {
                        // No Trackings found
                        apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.getTrackings::NoTrackings"));
                    } else {
                        apiResponse.onSuccess(trackings);
                    }
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.getTrackings::Exception"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.checkInDate::VolleyError"));
            }
        });
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }

    /**
     * Get the trackings object for a specific trackable type in a blocking way. For use on background threads and services only.
     * @param type Type of tracking to get
     * @param date Date of trackings
     * @return JSONObject of trackings
     */
    public Trackings getTrackingsBlocking(TrackableType type, Calendar date){
        WebAttributes getParams = new WebAttributes();
        getParams.put("at", Date.calendarToString(date));
        getParams.put("trackable_type",type.getTrackingsFormattedType());

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("trackings", getParams), future, future);
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);

        try {
            JSONObject response =  future.get(15, TimeUnit.SECONDS);
            return new Trackings(response);
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        } catch (TimeoutException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Get the treatment from a tracking
     * @param ids List of treatment ids
     */
    public void getTreatments(List<String> ids, final APIResponse<List<Treatment>, Error> apiResponse){
        String params = "";
        for (String id : ids){
            params += "ids[]=" + id + "&";
        }

        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("treatments") + "?" + params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray treatmentsJArray = response.getJSONArray("treatments");
                    List<Treatment> treatments = new ArrayList<
                            >();
                    for (int i = 0; i < treatmentsJArray.length(); i++){
                        JSONObject treatmentJObject = treatmentsJArray.getJSONObject(i);
                        Treatment treatment = new Treatment(treatmentJObject);
                        treatments.add(treatment);
                    }
                    apiResponse.onSuccess(treatments);
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.treatment::Exception"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.checkInDate::VolleyError"));
            }
        });
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }

    /**
     * Get the treatment from a tracking in a blocking way. For use on backgrounds threads only.
     * @param ids List of treatment ids
     * @return JSONObject of treatments
     */
    public List<Treatment> getTreatmentsBlocking(List<String> ids){
        String params = "";
        for (String id : ids){
            params += "ids[]=" + id + "&";
        }
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("treatments") + "?" + params,future, future);
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);

        try {
            JSONObject response =  future.get(15, TimeUnit.SECONDS);
            JSONArray treatmentsJArray = response.getJSONArray("treatments");
            List<Treatment> treatments = new ArrayList<>();
            for (int i = 0; i < treatmentsJArray.length(); i++){
                JSONObject treatmentJObject = treatmentsJArray.getJSONObject(i);
                Treatment treatment = new Treatment(treatmentJObject);
                treatments.add(treatment);
            }
            return treatments;
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        } catch (TimeoutException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     *
     * @param id User id
     * @param apiResponse response or error callback
     */
    public void getProfile(String id, final APIResponse<Profile, Error> apiResponse){
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("profiles") + "/" + id, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    apiResponse.onSuccess(new Profile(response));
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.getProfile::Exception"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.getProfile::VolleyError"));
            }
        });
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }

    /**
     * Submits the user's profile to the api with changes
     * @param profile Profile of user
     * @param apiResponse Response object
     */
    public void putProfile(Profile profile, final APIResponse<JSONObject, Error> apiResponse){
        try{
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.PUT, EndPointUrl.getAPIUrl("profiles") + "/" + profile.getId(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                apiResponse.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.getProfile::VolleyError"));
            }
        });
            jsonObjectExtraRequest.setRequestBody(profile.toJSON().toString());
            Map<String,String> headers = jsonObjectExtraRequest.getHeaders();
            headers.put("Content-Type", "application/json");
            WebAttributes attr = new WebAttributes();
            attr.putAll(headers);
            jsonObjectExtraRequest.setHeaders(attr);
            QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
        } catch (JSONException e){
            apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.putProfile::JSONException"));
        } catch (AuthFailureError authFailureError) {
            apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.putProfile::AuthFailure"));
        }
    }

    /**
     * Get the list of available countries
     * @param apiResponse response or error callback
     */
    public void getCountries(final APIResponse<List<Country>, Error> apiResponse){
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("countries"), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray countryArray = response.getJSONArray("countries");
                    List<Country> countries = new ArrayList<>();
                    for (int i = 0; i < countryArray.length(); i++) {
                        countries.add(new Country(countryArray.getJSONObject(i)));
                    }
                    apiResponse.onSuccess(countries);
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.getCountries:JSONException"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error().setExceptionThrown(error).setDebugString("APIv2.Communicate.getCountries:VolleyError"));
            }
        });
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }

    /**
     *
     * @param type Trackable type
     * @param ids List of ids to retrieve
     * @param apiResponse response or error callback
     */
    public void getTrackable(final TrackableType type, List<Integer> ids, final APIResponse<ArrayList<MetaTrackable>, Error> apiResponse) {
        String url = EndPointUrl.getAPIUrl(type.name().toLowerCase() + "s");
        url += "?";
        for (Integer id : ids) {
            url += "ids[]=" + id + "&";
        }
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<MetaTrackable> result = new ArrayList<>();
                    JSONArray jArray = response.getJSONArray(type.name().toLowerCase() + "s");
                    for (int i = 0; i < jArray.length(); i++) {
                        result.add(new MetaTrackable(jArray.getJSONObject(i)));
                    }
                    apiResponse.onSuccess(result);
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.getTrackable:JSONException"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }

    /**
     *
     * @param id id of Tracking to remove
     * @param apiResponse response or error callback
     */
    public void removeTrackings(String id, final APIResponse<String, Error> apiResponse ){
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, EndPointUrl.getAPIUrl("trackings") + "/" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    apiResponse.onSuccess("");
                } catch (Exception e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.deleteTrackings:JSONException"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error().setExceptionThrown(error).setDebugString("APIv2.Communicate.deleteTrackings:VolleyError"));
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Pass authentication parameters if available.
                if(new Communicate(context).isCredentialsSaved()) {
                    SharedPreferences sp = PreferenceKeys.getSharedPreferences(context);
                    WebAttributes headers = new WebAttributes();
                    headers.put("Authorization", "Token token=\"" + sp.getString(PreferenceKeys.SP_Av2_USER_TOKEN, "") + "\", email=\"" + sp.getString(PreferenceKeys.SP_Av2_USER_EMAIL, "") + "\"");
                    return headers;
                }
                return super.getHeaders();
            }
        };
        QueueProvider.getQueue(context).add(stringRequest);
    }

    public void getSuggestedDoses(String id, final APIResponse<Search, Error> apiResponse){
        WebAttributes params = new WebAttributes();
        params.put("query[treatment_id]",id);
        params.put("resource","dose");
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("searches",params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Search search = new Search(response);
                    apiResponse.onSuccess(search);
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.getSuggestedDoses:JSONException"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error().setExceptionThrown(error).setDebugString("APIv2.Communicate.getSuggestedDoses:VolleyError"));
            }
        });
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }
}
