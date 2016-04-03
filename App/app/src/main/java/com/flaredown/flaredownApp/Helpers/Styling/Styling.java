package com.flaredown.flaredownApp.Helpers.Styling;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;

import com.flaredown.flaredownApp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

        /*
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


    public static String capitalise1char(String string) {
        if(string.length() == 1) {
            string = string.toUpperCase();
        } else if(string.length() > 1) {
            string = string.substring(0, 1).toUpperCase() + string.substring(1);
        }
        return string;
    }

    public static String displayDateLong(Calendar date) {
        SimpleDateFormat sdf;
        if(date.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) // If current year don't display the year.
            sdf = new SimpleDateFormat("MMMM d");
        else
            sdf = new SimpleDateFormat("MMMM d, yyyy");
        return sdf.format(date.getTime());
    }


    public static boolean isLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) > Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static void forcePortraitOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static void forcePortraitOnSmallDevices(Activity activity) {
        if(!isLargeScreen(activity))
            forcePortraitOrientation(activity);
    }
}
