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
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Profile.Country;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Profile.Profile;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Searches.Search;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Session.Session;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings.Tracking;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Trackings.Trackings;
import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;
import com.flaredown.flaredownApp.Helpers.PreferenceKeys;
import com.flaredown.flaredownApp.Helpers.Volley.JsonObjectExtraRequest;
import com.flaredown.flaredownApp.Helpers.Volley.QueueProvider;
import com.flaredown.flaredownApp.Helpers.Volley.WebAttributes;
<<<<<<< HEAD
=======
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Profile.Country;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Profile.Profile;
import com.flaredown.flaredownApp.Models.Alarm;
>>>>>>> development
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

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Contains methods used for communicating with the API.
 */
public class Communicate {
    private static final String DEBUG_KEY = "Communi";
    private static final long DEFAULT_DB_CACHE_EXPIRE_TIME = 1209600000;    // 14 days.. If items are
                                                                            // older than this they will be
                                                                            // removed from the db and
                                                                            // refreshed from the api.

    private Context context;
    private Realm mRealm;

    /**
     * Create a communication's class.
     * @param context The context for the activity.
     */
    public Communicate(Context context) {
        this.context = context;
        this.mRealm = Realm.getInstance(context);
    }

    /**
     * Login, this will sign in a user and store there credentials.
     * @param email The email of the user.
     * @param password The password for the user.
     */
    public void userSignIn(final String email, final String password, final APIResponse<Session, Error> apiResponse){
        final Runnable retryRunnable = new Runnable() {
            @Override
            public void run() {
                userSignIn(email, password, apiResponse);
            }
        };

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
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.userSignIn::VolleyError").setRetryRunnable(retryRunnable));
            }
        }).setParams(parameters);
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }

    public void userSignOut() {
        SharedPreferences sp = PreferenceKeys.getSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        spe.clear();
        spe.commit();

        Realm realm = Realm.getInstance(context);
        realm.beginTransaction();
        realm.clear(MetaTrackable.class);
        realm.commitTransaction();
    }

    /**
<<<<<<< HEAD
     * Get the check in object for a specific identification. // TODO needs testing.
     * @param id id of the checkin
=======
     * Get the check in object for a specific identification.
     * @param id The id of the check in to fetch.
     * @param apiResponse Response or error callback.
>>>>>>> development
     */
    public void checkIn(final String id, final APIResponse<CheckIn, Error> apiResponse) {
        final Runnable retryRunnable = new Runnable() {
            @Override
            public void run() {
                checkIn(id, apiResponse);
            }
        };
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
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.chackIn::ParseError").setRetryRunnable(retryRunnable));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.checkIn::VolleyError").setRetryRunnable(retryRunnable));
            }
        });
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }

    /**
     * Get the check in object for a specific date.
     * @param date The date for the check in.
     * @param apiResponse Response or error callback.
     */
    public void checkIn(final Calendar date, final APIResponse<CheckIn, Error> apiResponse) {
        final Runnable retryRunnable = new Runnable() {
            @Override
            public void run() {
                checkIn(date, apiResponse);
            }
        };

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
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.checkInDate::Exception").setRetryRunnable(retryRunnable));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.checkInDate::VolleyError").setRetryRunnable(retryRunnable));
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
    public void createCheckIn(final Calendar date, final APIResponse<CheckIn, Error> apiResponse) {
        final Runnable retryRunnable = new Runnable() {
            @Override
            public void run() {
                createCheckIn(date, apiResponse);
            }
        };

        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.POST, EndPointUrl.getAPIUrl("checkins"), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    apiResponse.onSuccess(new CheckIn(response));
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.createCheckIn::JSONException").setRetryRunnable(retryRunnable));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.createCheckIn::Volley").setRetryRunnable(retryRunnable));
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
            apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.createCheckIn::JSONException2").setRetryRunnable(retryRunnable));
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

    /**
     * Submit the check in to the api.
     * @param checkIn The check in to submit.
     * @param apiResponse Getting the response from the api.
     */
    public void submitCheckin(final CheckIn checkIn, final APIResponse<CheckIn, Error> apiResponse) {
        final Runnable retryRunnable = new Runnable() {
            @Override
            public void run() {
                submitCheckin(checkIn, apiResponse);
            }
        };

        try {
            JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.PUT, EndPointUrl.getAPIUrl("checkins/" + checkIn.getId()), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        apiResponse.onSuccess(new CheckIn(response));
                    } catch (JSONException e) {
                        apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.submitCheckin::JSONException").setRetryRunnable(retryRunnable));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.submitCheckin::volley").setRetryRunnable(retryRunnable));
                }
            });
            jsonObjectExtraRequest.setRequestBody(checkIn.getResponseJson().toString());
            WebAttributes headers = new WebAttributes();
            headers.put("Content-Type", "application/json");
            jsonObjectExtraRequest.setHeaders(headers);
            QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
        } catch (JSONException e) {
            apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.submitCheckin::JSONException2").setRetryRunnable(retryRunnable));
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
    public void getTrackings(final TrackableType type, final Calendar date, final APIResponse<Trackings, Error> apiResponse) {
        final Runnable retryRunnable = new Runnable() {
            @Override
            public void run() {
                getTrackings(type, date, apiResponse);
            }
        };

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
<<<<<<< HEAD
                        apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.getTrackings::NoTrackings"));
=======
                        apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.checkInDate::NoTrackings").setRetryRunnable(retryRunnable));
>>>>>>> development
                    } else {
                        apiResponse.onSuccess(trackings);
                    }
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.getTrackings::Exception").setRetryRunnable(retryRunnable));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.checkInDate::VolleyError").setRetryRunnable(retryRunnable));
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
     * Post the trackings object for a specific trackable type.
     * @param tracking The tracking object
     */
    public void submitTracking(Tracking tracking, final APIResponse<Tracking, Error> apiResponse) {
        try{
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.POST, EndPointUrl.getAPIUrl("trackings"), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Tracking tracking = new Tracking(response);
                    apiResponse.onSuccess(tracking);
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.submitTracking::Exception"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.submitTracking::VolleyError"));
            }
        });

        jsonObjectExtraRequest.setRequestBody(tracking.toJson().toString());
        WebAttributes headers = new WebAttributes();
        headers.put("Content-Type", "application/json");
        jsonObjectExtraRequest.setHeaders(headers);
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
        } catch (JSONException e) {
            apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.submitTracking::JSONException"));
        }

    }

    /**
     * Get the treatment from a tracking
     * @param ids List of treatment ids
     */
    public void getTreatments(final List<Integer> ids, final APIResponse<List<Treatment>, Error> apiResponse){
        final Runnable retryRunnable = new Runnable() {
            @Override
            public void run() {
                getTreatments(ids, apiResponse);
            }
        };

        String params = "";
        for (Integer id : ids){
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
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.treatment::Exception").setRetryRunnable(retryRunnable));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.checkInDate::VolleyError").setRetryRunnable(retryRunnable));
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
        for (Integer id : ids){
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
    public void getProfile(final String id, final APIResponse<Profile, Error> apiResponse) {
        final Runnable retryRunnable = new Runnable() {
            @Override
            public void run() {
                getProfile(id, apiResponse);
            }
        };

        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("profiles") + "/" + id, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    apiResponse.onSuccess(new Profile(response));
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.getProfile::Exception").setRetryRunnable(retryRunnable));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.getProfile::VolleyError").setRetryRunnable(retryRunnable));
            }
        });
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }

    /**
     * Submits the user's profile to the api with changes
     * @param profile Profile of user
     * @param apiResponse Response object
     */
    public void putProfile(final Profile profile, final APIResponse<JSONObject, Error> apiResponse) {
        final Runnable retryRunnable = new Runnable() {
            @Override
            public void run() {
                putProfile(profile, apiResponse);
            }
        };
        try{
            JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.PUT, EndPointUrl.getAPIUrl("profiles") + "/" + profile.getId(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    apiResponse.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.getProfile::VolleyError").setRetryRunnable(retryRunnable));
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
            apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.putProfile::JSONException").setRetryRunnable(retryRunnable));
        } catch (AuthFailureError authFailureError) {
            apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.putProfile::AuthFailure").setRetryRunnable(retryRunnable));
        }
    }

    /**
     * Get the list of available countries
     * @param apiResponse response or error callback
     */
    public void getCountries(final APIResponse<List<Country>, Error> apiResponse) {
        final Runnable retryRunnable = new Runnable() {
            @Override
            public void run() {
                getCountries(apiResponse);
            }
        };

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
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.getCountries:JSONException").setRetryRunnable(retryRunnable));
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
<<<<<<< HEAD
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
=======
     * Get a collection of MetaTrackables from the collection of id's provided. MetaTrackables are
     * cached and if ALL ids are stored in the DB then the cache will be used, otherwise a new query
     * will be made.
     * @param type The trackable types for all ID's given.
     * @param ids The ids for the MetaTrackables.
     * @param apiResponse Response or error callback.
     */
    public void getTrackable(final TrackableType type, final List<Integer> ids, final APIResponse<ArrayList<MetaTrackable>, Error> apiResponse) {
        final Runnable retryRunnable = new Runnable() {
            @Override
            public void run() {
                getTrackable(type, ids, apiResponse);
            }
        };

        MetaTrackable.clearExpiredItems(mRealm, DEFAULT_DB_CACHE_EXPIRE_TIME);

        RealmQuery<MetaTrackable> inDBQuery = mRealm.where(MetaTrackable.class);
        boolean first = true;
        for(Integer id : ids) {
            if(!first)
                inDBQuery.or();
            else first = false;

            inDBQuery.equalTo("id", id);
>>>>>>> development
        }

        inDBQuery = inDBQuery.findAll().where().equalTo("typeRaw", type.name());

        if(inDBQuery.count() == ids.size()) { // No need to do query.
            PreferenceKeys.log(PreferenceKeys.LOG_I, DEBUG_KEY, "Fetching Cached Meta Trackables");
            RealmResults<MetaTrackable> inDBResults = inDBQuery.findAll();
            apiResponse.onSuccess(new ArrayList<>(inDBResults.subList(0, inDBResults.size())));
        } else {

            String url = EndPointUrl.getAPIUrl(type.name().toLowerCase() + "s");
            url += "?";
            for (Integer id : ids) {
                url += "ids[]=" + id + "&";
            }
            JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    PreferenceKeys.log(PreferenceKeys.LOG_I, DEBUG_KEY, "Fetching Meta Trackables");
                    try {
                        ArrayList<MetaTrackable> result = new ArrayList<>();
                        JSONArray jArray = response.getJSONArray(type.name().toLowerCase() + "s");
                        for (int i = 0; i < jArray.length(); i++) {
                            MetaTrackable mt = new MetaTrackable(jArray.getJSONObject(i));
                            mt.setCachedAt(Calendar.getInstance()); // Set the cached time, enabling the db to stay fresh.
                            result.add(mt);
                        }
                        mRealm.beginTransaction();
                        mRealm.copyToRealmOrUpdate(result);
                        mRealm.commitTransaction();
                        apiResponse.onSuccess(result);
                    } catch (JSONException e) {
                        apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.getTrackable:JSONException").setRetryRunnable(retryRunnable));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
        }
    }

    /**
     *
     * @param id id of Tracking to remove
     * @param apiResponse response or error callback
     */
    public void removeTrackings(int id, final APIResponse<String, Error> apiResponse ){
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

    public void search(String queryname, String resource, final APIResponse<Search, Error> apiResponse){
        WebAttributes params = new WebAttributes();
        params.put("query[name]",queryname);
        params.put("resource",resource);
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

    public void submitNewTrackable(final TrackableType type, String name, final APIResponse<MetaTrackable, Error> apiResponse ){
        String template = "{\"%s\":{\"name\":%s,\"color_id\":null,\"users_count\":null}}\n";
        String newTrackable = String.format(template,type.toString().toLowerCase(),name);
        String url = EndPointUrl.getAPIUrl(type.name().toLowerCase() + "s");

        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    MetaTrackable meta = new MetaTrackable(response.getJSONObject(type.toString().toLowerCase()));
                    apiResponse.onSuccess(meta);
                } catch (JSONException e) {
                    apiResponse.onFailure(new Error().setExceptionThrown(e).setDebugString("APIv2.Communicate.submitNewTrackable::Exception"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.submitNewTrackable::VolleyError"));
            }
        });
        WebAttributes params = new WebAttributes();
        jsonObjectExtraRequest.setRequestBody(newTrackable);
        params.put("Content-Type", "application/json");
        jsonObjectExtraRequest.setHeaders(params);
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }
}
