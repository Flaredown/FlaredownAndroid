package com.flaredown.flaredownApp.Helpers.APIv2;

import com.flaredown.flaredownApp.BuildConfig;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by thunter on 10/03/16.
 */
public class CommunicateTest {


    /**
     * Tests if the getApiUrl method returns the correct url when only passed an entry point.
     * @throws Exception
     */
    @Test
    public void getApiUrl1Test() throws Exception {
        String output = Communicate.getApiUrl("test");
        assertEquals("Returning incorrect URL", BuildConfig.API_BASE_URI + "/test", output);
    }

    @Test
    public void getApiUrl2Test() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("test", "testing");
        map.put("test2", "testing2");
        map.put("tj%@", "test%ing3");

        String output = Communicate.getApiUrl("test", map);

        assertEquals("Returning incorrect URL", BuildConfig.API_BASE_URI + "/test?test=testing&tj%25%40=test%25ing3&test2=testing2", output);

    }
}