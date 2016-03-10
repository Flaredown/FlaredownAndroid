package com.flaredown.flaredownApp.Helpers;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Keeps a global copy of the volley request queue.
 */
public class VolleyProvider {
    private static RequestQueue queue = null;

    private VolleyProvider() {}

    private static synchronized RequestQueue getQueue(Context context) {
        if(queue == null)
            queue = Volley.newRequestQueue(context.getApplicationContext());
        return queue;
    }
}
