package com.flaredown.flaredownApp.Helpers;

import java.util.Calendar;

/**
 * Created by squiggie on 2/16/16.
 */
public class TimeHelper {

    public static int getCurrentTimezoneOffset(Calendar c) {
        return c.getTimeZone().getOffset(c.getTimeInMillis());
    }

}
