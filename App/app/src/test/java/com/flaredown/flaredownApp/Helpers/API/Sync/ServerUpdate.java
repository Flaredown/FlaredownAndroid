package com.flaredown.flaredownApp.Helpers.API.Sync;

import com.flaredown.flaredownApp.API.Endpoints.Session.SessionLogin;
import com.flaredown.flaredownApp.Helpers.Volley.WebAttributes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by thunter on 30/03/2017.
 */

public class ServerUpdate {

    /**
     * Check if the SessionUpdate method correctly creates a WebAttributes object using annotated
     * fields.
     * @throws Exception
     */
    @Test
    public void getWebAttributes() throws Exception {

        String email = "test@example.com";
        String password = "pass";

        SessionLogin sl = new SessionLogin(email, password);


        WebAttributes wa = sl.getWebAttributes();

        assertEquals("Returning incorrect number of webattributes", 2, wa.size());

        assertEquals("Returning incorrect value for the email param", email, wa.get("user[email]"));

        assertEquals("Returning incorrect value for the password param", password, wa.get("user[password]"));
    }
}
