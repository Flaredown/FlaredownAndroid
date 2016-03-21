package com.flaredown.flaredownApp.Helpers.Volley;

import com.android.volley.AuthFailureError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.flaredown.flaredownApp.Helpers.PreferenceKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Injects authentication information into a Volley Request.
 */
public class JsonObjectExtraRequest extends StringRequest {
    private WebAttributes headers = new WebAttributes();
    private WebAttributes params = new WebAttributes();
    /**
     * Creates a new request.
     * @param method the HTTP method to use.
     * @param url URL to fetch the JSON from.
     * @param listener Listener to receive the JSON response.
     * @param errorListener Error listener, or null to ignore errors.
     * @return itself.
     */
    public static JsonObjectExtraRequest createRequest(int method, String url, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {
        // It is done this way because the JSONObjectRequest provided by the volley language has
        // difficulty with attaching parameters with post requests.
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    listener.onResponse(new JSONObject(response));
                } catch (JSONException e) {
                    PreferenceKeys.log(PreferenceKeys.LOG_E, "GH", "EXCE{TO");
                    errorListener.onErrorResponse(new ParseError(e));
                }
            }
        };

        return new JsonObjectExtraRequest(method, url, stringListener, errorListener);
    }

    private JsonObjectExtraRequest(int method, String url, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    /**
     * Set the extra headers for the Json Object request.
     * @param headers Map of the extra headers.
     */
    public JsonObjectExtraRequest setHeaders(WebAttributes headers) {
        this.headers = headers;
        return this;
    }

    /**
     * Set the parameters for the Json Object request.
     * @param params Map of the params for the HTTP request.
     */
    public JsonObjectExtraRequest setParams(WebAttributes params) {
        this.params = params;
        return this;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        headers.putAll(this.headers);
        return headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        WebAttributes getParames = new WebAttributes();
        Map<String, String> superParams = super.getParams();
        if(superParams != null)
            getParames.putAll(superParams);
        getParames.putAll(params);
        return getParames;
    }
}
