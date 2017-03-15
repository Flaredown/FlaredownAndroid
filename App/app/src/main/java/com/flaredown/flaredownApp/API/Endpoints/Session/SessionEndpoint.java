package com.flaredown.flaredownApp.API.Endpoints.Session;

import com.flaredown.flaredownApp.API.Sync.Endpoint;
import com.flaredown.flaredownApp.API.Sync.RequestMethod;

/**
 * Created by thunter on 15/03/2017.
 */

public class SessionEndpoint extends Endpoint {
    @Override
    protected String getEndpoint() {
        return "sessions";
    }

    @Override
    protected RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
