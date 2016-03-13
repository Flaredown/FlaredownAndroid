package com.flaredown.flaredownApp.Helpers.Volley;

import android.util.ArrayMap;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thunter on 10/03/16.
 */
public class JsonObjectExtraRequest extends JsonObjectRequest {
    private WebAttributes headers = new WebAttributes();
    private WebAttributes params = new WebAttributes();

    public JsonObjectExtraRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    /**
     * Set the extra headers for the Json Object request.
     * @param headers Map of the extra headers.
     */
    public void setHeaders(WebAttributes headers) {
        this.headers = headers;
    }

    /**
     * Set the extra parameters for the Json Object request.
     * @param params Map of the extra params.
     */
    public void setParams(WebAttributes params) {
        this.params = params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        headers.putAll(this.headers);
        return headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = super.getParams();
        params.putAll(this.params);
        return params;
    }
}
