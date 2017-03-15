package com.flaredown.flaredownApp.API.Endpoints.Session;

import com.flaredown.flaredownApp.API.Sync.ServerUpdate;
import com.flaredown.flaredownApp.API.Sync.WebParameter;

/**
 * Processes the user sign in
 */

public class SessionUpdate extends ServerUpdate {

    @WebParameter(keyValue = "user[email]")
    private String email;
    @WebParameter(keyValue = "user[password]")
    private String password;


    @Override
    protected boolean allowStorage() {
        // Login should only work if the device is connected, also would not be wise to store the
        // password in plain text.
        return false;
    }
}
