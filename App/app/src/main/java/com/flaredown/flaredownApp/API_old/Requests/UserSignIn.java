package com.flaredown.flaredownApp.API_old.Requests;

import com.android.volley.VolleyError;
import com.flaredown.flaredownApp.API_old.RequestMethod;
import com.flaredown.flaredownApp.API_old.ResponseModel.Sessions;
import com.flaredown.flaredownApp.API_old.SuperRequest;
import com.flaredown.flaredownApp.Helpers.Volley.WebAttributes;

/**
 * Sign in user, sends to endpoint /api/sessions
 */

public class UserSignIn extends SuperRequest<Sessions>{

    private String username;
    private String password;

    public UserSignIn(String username, String password) {
        super(RequestMethod.POST, "sessions");
        this.username = username;
        this.password = password;
    }

    @Override
    protected WebAttributes getPostParams() {
        WebAttributes attrs = super.getPostParams();
        attrs.put("user[email]", username);
        attrs.put("user[password]", password);
        return attrs;
    }

    @Override
    protected Sessions onRequestSuccess(String data) throws Throwable {
        return Sessions.createFromJson(data);
    }

    @Override
    protected Throwable onRequestError(Throwable object) {
        if(object != null && object instanceof VolleyError && ((VolleyError) object).networkResponse.statusCode == 401)
            return new InvalidCredentialsException();
        return super.onRequestError(object);
    }

    public static final class InvalidCredentialsException extends IllegalArgumentException {
        public InvalidCredentialsException() {
            super("Invalid user credentials");
        }
    }
}
