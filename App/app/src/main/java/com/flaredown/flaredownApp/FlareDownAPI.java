package com.flaredown.flaredownApp;

import android.content.Context;
import android.os.Environment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by thunter on 03/09/15.
 */
public class FlareDownAPI {
    private Context mContext;
    private static final String DEBUG_TAG = "FlareDownAPI";
    public static final String API_BASE_URL = "https://api-staging.flaredown.com/v1";
    private static final String LOCALE_CACHE_FNAME = "localeCache";
    public static final String getEndpointUrl(String endpoint) {
        return API_BASE_URL + endpoint;
    }
    public JSONObject locales = null;

    public FlareDownAPI(Context context) {
        mContext = context;
        locales = readCachedLocales();
    }




    public JSONObject readCachedLocales() {
        File data = new File(mContext.getCacheDir().getPath() + LOCALE_CACHE_FNAME);
        try {
            if(data.exists()) {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(data));
                JSONObject jsonObject = new JSONObject((String) objectInputStream.readObject());
                objectInputStream.close();
                return jsonObject;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface OnCacheLocales {
        void onSuccess(JSONObject locales);
        void onError();
    }
    public void cacheLocales(OnCacheLocales onCacheLocales) { cacheLocales("en", onCacheLocales);}
    public void cacheLocales(final String language, final OnCacheLocales onCacheLocales) {



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getEndpointUrl("/locales/" + language), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    File dir = mContext.getCacheDir();
                    if (!dir.exists())
                        dir.mkdirs();
                    String path = mContext.getCacheDir().getPath() + LOCALE_CACHE_FNAME;
                    File data = new File(path);
                    if (!data.createNewFile()) {
                        data.delete();
                        data.createNewFile();
                    }

                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(data));
                    objectOutputStream.writeObject(response.getJSONObject(language).toString());
                    objectOutputStream.close();
                    onCacheLocales.onSuccess(response.getJSONObject(language));
                } catch (Exception e) {
                    e.printStackTrace();
                    onCacheLocales.onError();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onCacheLocales.onError();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(jsonObjectRequest);
    }

}
