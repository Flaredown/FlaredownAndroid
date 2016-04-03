package com.flaredown.flaredownApp.Helpers.APIv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.CheckIn;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.CheckIns;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.MetaTrackable;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns.TrackableType;
import com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.Session.Session;
import com.flaredown.flaredownApp.Helpers.APIv2.Helper.Date;
import com.flaredown.flaredownApp.Helpers.PreferenceKeys;
import com.flaredown.flaredownApp.Helpers.Volley.JsonObjectExtraRequest;
import com.flaredown.flaredownApp.Helpers.Volley.QueueProvider;
import com.flaredown.flaredownApp.Helpers.Volley.WebAttributes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public void checkIn(Calendar date, final APIResponse<CheckIn, Error> apiResponse) {
        WebAttributes getParams = new WebAttributes();
        getParams.put("date", Date.calendarToString(date));
        JsonObjectExtraRequest jsonObjectExtraRequest = JsonObjectExtraRequest.createRequest(context, Request.Method.GET, EndPointUrl.getAPIUrl("checkins", getParams), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    CheckIns checkIns = new CheckIns(response);
                    if (checkIns.size() <= 0) {
                        // No check ins found
                        apiResponse.onFailure(new Error().setDebugString("APIv2.Communicate.checkInDate::NoCheckIns"));
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
                apiResponse.onFailure(new Error(error).setDebugString("APIv2.Communicate.getTrackable:VolleyError"));
            }
        });
        QueueProvider.getQueue(context).add(jsonObjectExtraRequest);
    }
}
