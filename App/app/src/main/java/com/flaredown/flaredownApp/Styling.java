package com.flaredown.flaredownApp;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by thunter on 13/09/15.
 */
public class Styling {
    public static void setFont() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/proximanova_regular.ttf")
                .build());

        /**
         * Don't forget to add the following
         * @Override
         * protected void attachBaseContext(Context newBase) {
         *      super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
         * }
         */
    }
}
