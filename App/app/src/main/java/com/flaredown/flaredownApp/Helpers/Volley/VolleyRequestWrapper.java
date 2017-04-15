package com.flaredown.flaredownApp.Helpers.Volley;

import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Wrapper class for the Volley library for sending JSON
 */

public class VolleyRequestWrapper extends StringRequest {
    protected static final String PROTOCOL_CHARSET = "utf-8";

    private WebAttributes headers = new WebAttributes();
    private WebAttributes authHeaders = new WebAttributes();
    private WebAttributes postParams = new WebAttributes();

    private String requestBody;


    public VolleyRequestWrapper(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        WebAttributes output = new WebAttributes();
        Map<String, String> superHeaders = super.getHeaders();

        // Merge the super headers, auth headers and standard headers into one web attribute.

        if(superHeaders != null)
            output.putAll(superHeaders);
        if(this.headers.size() > 0)
            output.putAll(this.headers);
        if(this.authHeaders.size() > 0)
            output.putAll(authHeaders);
        return output;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        WebAttributes output = new WebAttributes();
        Map<String, String> superParams = super.getParams();
        if(superParams != null)
            output.putAll(superParams);
        if(postParams != null)
            output.putAll(postParams);
        return output;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        try {
            return requestBody == null ? super.getBody() : requestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException e) {
            VolleyLog.wtf(e, e.getLocalizedMessage());
        }
        return super.getBody();
    }

    // *** Setters and getters ***

    /**
     * Set the request headers.
     * @param headers The request headers.
     */
    public void setHeaders(WebAttributes headers) {
        this.headers = headers;
    }

    /**
     * Set the authorisation headers.
     * @param authHeaders The authorisation headers.
     */
    public void setAuthHeaders(WebAttributes authHeaders) {
        this.authHeaders = authHeaders;
    }

    /**
     * Set the post parameters (if it is a post request).
     * @param postParams The post params.
     */
    public void setPostParams(WebAttributes postParams) {
        this.postParams = postParams;
    }

    /**
     * Set the request body, put request.
     * @param requestBody The request body.
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
}
