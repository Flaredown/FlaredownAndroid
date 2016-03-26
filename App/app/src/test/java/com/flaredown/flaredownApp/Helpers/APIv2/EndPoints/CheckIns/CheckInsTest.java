package com.flaredown.flaredownApp.Helpers.APIv2.EndPoints.CheckIns;

import com.flaredown.flaredownApp.BuildConfig;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by thunter on 26/03/16.
 */
@Config(sdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricGradleTestRunner.class)
public class CheckInsTest {

    @Test
    public void testJsonParse() throws Exception {
        String jsonTestString = "{\"checkins\":[{\"id\":\"56f5a02fa28d27000302004e\",\"created_at\":null,\"updated_at\":null,\"date\":\"2016-03-18\",\"note\":\"\",\"tag_ids\":[],\"conditions\":[{\"id\":\"56f5a02fa28d27000302004d\",\"created_at\":null,\"updated_at\":null,\"checkin_id\":\"56f5a02fa28d27000302004e\",\"value\":0,\"color_id\":\"7\",\"condition_id\":269}],\"symptoms\":[{\"id\":\"56f5a02fa28d27000302004f\",\"created_at\":null,\"updated_at\":null,\"checkin_id\":\"56f5a02fa28d27000302004e\",\"value\":0,\"color_id\":\"26\",\"symptom_id\":607},{\"id\":\"56f5a02fa28d270003020050\",\"created_at\":null,\"updated_at\":null,\"checkin_id\":\"56f5a02fa28d27000302004e\",\"value\":0,\"color_id\":\"21\",\"symptom_id\":243},{\"id\":\"56f5a02fa28d270003020051\",\"created_at\":null,\"updated_at\":null,\"checkin_id\":\"56f5a02fa28d27000302004e\",\"value\":0,\"color_id\":\"9\",\"symptom_id\":536}],\"treatments\":[]}]}";
        CheckIns checkins = new CheckIns(new JSONObject(jsonTestString));

        System.out.printf(checkins.toJson().toString());
    }
}