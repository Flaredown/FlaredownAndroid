package com.flaredown.flaredownApp.API.Sync;

import com.android.volley.Request;

/**
 * Created by thunter on 05/03/2017.
 */

public enum RequestMethod {
    GET(Request.Method.GET),
    PUT(Request.Method.PUT),
    DELETE(Request.Method.DELETE),
    POST(Request.Method.POST);

    private int volleyInt;

    RequestMethod(int volleyInt) {
        this.volleyInt = volleyInt;
    }

    public int getVolleyInt() {
        return volleyInt;
    }
}
