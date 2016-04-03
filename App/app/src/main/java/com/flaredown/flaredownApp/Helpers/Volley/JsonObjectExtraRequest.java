package com.flaredown.flaredownApp.Helpers.Volley;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.flaredown.flaredownApp.Helpers.APIv2.Communicate;
import com.flaredown.flaredownApp.Helpers.PreferenceKeys;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Injects authentication information into a Volley Request.
 */
public class JsonObjectExtraRequest extends StringRequest {
    /** Default charset for JSON request. */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    private Context mContext;
    private WebAttributes headers = new WebAttributes();
    private WebAttributes authHeaders = new WebAttributes();
    private WebAttributes params = new WebAttributes();
    private String requestBody;
    /**
     * Creates a new request.
     * @param method the HTTP method to use.
     * @param url URL to fetch the JSON from.
     * @param listener Listener to receive the JSON response.
     * @param errorListener Error listener, or null to ignore errors.
     * @return itself.
     */
    public static JsonObjectExtraRequest createRequest(Context context, int method, String url, final Response.Listener<JSONObject> listener, final Response.ErrorListener errorListener) {
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
        JsonObjectExtraRequest output = new JsonObjectExtraRequest(context, method, url, stringListener, errorListener);

        // Pass authentication parameters if available.
        if(new Communicate(context).isCredentialsSaved()) {
            SharedPreferences sp = PreferenceKeys.getSharedPreferences(context);
            WebAttributes headers = new WebAttributes();
            headers.put("Authorization", "Token token=\"" + sp.getString(PreferenceKeys.SP_Av2_USER_TOKEN, "") + "\", email=\"" + sp.getString(PreferenceKeys.SP_Av2_USER_EMAIL, "") + "\"");
            output.setAuthHeaders(headers);
        }

        return output;
    }

    private JsonObjectExtraRequest(Context context, int method, String url, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.mContext = context;
    }

    private void setAuthHeaders(WebAttributes authHeaders) {
        this.authHeaders = authHeaders;
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

    /**
     * Set the request body, used for PUT requests.
     * @param requestBody The request body.
     * @return itself.
     */
    public JsonObjectExtraRequest setRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        WebAttributes output = new WebAttributes();
        Map<String, String> superHeaders = super.getHeaders();
        if(superHeaders != null)
            output.putAll(this.headers);
        if(this.headers.size() > 0) {
            output.putAll(this.headers);
        }
        output.putAll(this.authHeaders);
        return output;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        WebAttributes getParames = new WebAttributes();
        Map<String, String> superParams = super.getParams();
        if(superParams != null)
            getParames.putAll(superParams);
        getParames.putAll(params);
        if(getParames.size() > 0)
            return getParames;
        return null;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return requestBody == null ? null : requestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    requestBody, PROTOCOL_CHARSET);
            return null;
        }
    }
}
