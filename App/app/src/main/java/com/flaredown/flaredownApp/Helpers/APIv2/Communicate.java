package com.flaredown.flaredownApp.Helpers.APIv2;

import android.content.Context;

import com.flaredown.flaredownApp.BuildConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Contains methods used for communicating with the API.
 */
public class Communicate {
    private static final String API_URL = BuildConfig.API_BASE_URI;

    private Context context;

    /**
     * Create a communication's class.
     * @param context The context for the activity.
     */
    public Communicate(Context context) {
        this.context = context;
    }

    /**
     * Get the full API url.
     * @param endpoint The endpoint for the api.
     * @return The full API url.
     */
    protected static String getApiUrl(String endpoint) {
        return getApiUrl(endpoint, null);
    }

    /**
     * Get the full API url.
     * @param endpoint The endpoint for the api.
     * @param params The GET params to pass to the API.
     * @return The full API url.
     */
    protected static String getApiUrl(String endpoint, Map<String, String> params) {
        // Ensure the endpoint begins with a /
        if(!endpoint.startsWith("/"))
            endpoint = "/" + endpoint;

        String returnS = API_URL + endpoint;

        if(params != null && params.size() > 0) {
            returnS += "?";
            for (String key : params.keySet()) {
                try {
                    returnS += URLEncoder.encode(key, "utf-8") + "=" + URLEncoder.encode(params.get(key), "utf-8") + "&";
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
