package com.flaredown.flaredownApp;

import android.app.Dialog;
import android.content.Context;
import android.flaredown.com.flaredown.R;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by thunter on 13/09/15.
 */
public class Styling {
    private static int uniqueId = 1;
    public static int getUniqueId() {
        return uniqueId++;
    }
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
    public static float getInDP(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
    public static void setBackground(Context context, View view, int resId) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            view.setBackgroundDrawable(ContextCompat.getDrawable(context, resId));
        else
            view.setBackground(ContextCompat.getDrawable(context, resId));
    }

    public static void styleDialog(Dialog dialog) {
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.box_background);
    }
}
