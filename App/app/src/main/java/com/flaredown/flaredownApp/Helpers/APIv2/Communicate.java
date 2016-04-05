package com.flaredown.flaredownApp.Helpers.APIv2;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.CheckIn;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.CheckIns;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
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
                spe.commit();

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
     * @param id
     */
    public void checkIn(String id, final APIResponse<CheckIn, Error> apiResponse) {
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("checkins/" + id), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    apiResponse.onSuccess(new CheckIn(response));
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
    public void checkIn(Calendar date, final APIResponse<CheckIn, Error> apiResponse) {
        WebAttributes getParams = new WebAttributes();
        getParams.put("date", Date.calendarToString(date));
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("checkins", getParams), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    CheckIns checkIns = new CheckIns(response);
                    if(checkIns.size() <= 0) {
                        // No check ins found
                        apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.checkInDate::NoCheckIns"));
                    } else {
                        apiResponse.onSuccess(checkIns.get(0));
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
                        // No getTrackings found
                        apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.checkInDate::NoTrackings"));
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
    public void getTreatments(List<Integer> ids, final APIResponse<List<Treatment>, Error> apiResponse){
        String params = "";
        for (int id : ids){
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
    public List<Treatment> getTreatmentsBlocking(List<Integer> ids){
        String params = "";
        for (int id : ids){
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
    public void getCountries(final APIResponse<List<>, Error> apiResponse){
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
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.getCountries::Exception"));
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
}
