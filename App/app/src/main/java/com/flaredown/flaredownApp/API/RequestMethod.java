package com.flaredown.flaredownApp.API;

import com.android.volley.Request;

/**
 * Created by thunter on 09/02/2017.
 */

public enum RequestMethod {
    GET(Request.Method.GET),
    POST(Request.Method.POST);

    private int volleyMethod;

    RequestMethod(int volleyMethod) {
        this.volleyMethod = volleyMethod;
    }

    public int getVolleyMethod() {
        return volleyMethod;
    }
}
