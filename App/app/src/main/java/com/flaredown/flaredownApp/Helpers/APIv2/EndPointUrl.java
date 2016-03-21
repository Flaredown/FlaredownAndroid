package com.flaredown.flaredownApp.Helpers.APIv2;

import android.support.annotation.Nullable;

import com.flaredown.flaredownApp.BuildConfig;
import com.flaredown.flaredownApp.Helpers.Volley.WebAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Retrieve the URL for a specific endpoint.
 */
public class EndPointUrl {
    public static final String API_URL = BuildConfig.API_BASE_URI;

    /**
     * Get a url address for a specific API endpoint.
     * @param endpoint The endpoint for the URL.
     * @return The web address for the endpoint given.
     */
    public static String getAPIUrl(String endpoint) {
        return getAPIUrl(endpoint, null);
    }

    /**
     * Get a url address for a specific API endpoint.
     * @param endpoint The endpoint for the URL.
     * @param getParams Get params to attach at the end of the url.
     * @return The web address for the endpoint given.
     */
    public static String getAPIUrl(String endpoint, @Nullable WebAttributes getParams) {
        // Ensure the endpoint begins with a /
        if(!endpoint.startsWith("/"))
            endpoint = "/" + endpoint;

        String returnS = API_URL + endpoint;

        if(getParams != null && getParams.size() > 0) {
            returnS += "?";
            for (String key : getParams.keySet()) {
                try {
                    returnS += URLEncoder.encode(key, "utf-8") + "=" + URLEncoder.encode(getParams.get(key), "utf-8") + "&";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if(returnS.endsWith("&"))
                returnS = returnS.substring(0, returnS.length()-1);
        }
        return returnS;
    }
}
