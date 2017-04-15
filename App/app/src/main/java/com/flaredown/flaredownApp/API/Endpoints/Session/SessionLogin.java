package com.flaredown.flaredownApp.API.Endpoints.Session;

import com.flaredown.flaredownApp.API.Sync.RequestMethod;
import com.flaredown.flaredownApp.API.Sync.ServerModel;
import com.flaredown.flaredownApp.API.Sync.ServerUpdate;
import com.flaredown.flaredownApp.API.Sync.WebParameter;
import com.flaredown.flaredownApp.Helpers.GsonHelper;

/**
 * Processes the user sign in
 */

public class SessionLogin extends ServerUpdate<SessionModel> {

    @WebParameter(keyValue = "user[email]")
    public String email;
    @WebParameter(keyValue = "user[password]")
    public String password;

    public SessionLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public SessionModel generateServerModel(String json) {
        return GsonHelper.getFromJson(json, SessionModel.class);
    }

    @Override
    public String getEndpoint() {
        return SessionEndpoint.endpointString;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }

    @Override
    protected boolean allowStorage() {
        // Login should only work if the device is connected, also would not be wise to store the
        // password in plain text.
        return false;
    }
}
