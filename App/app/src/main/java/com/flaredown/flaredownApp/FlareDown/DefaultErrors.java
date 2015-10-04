package com.flaredown.flaredownApp.FlareDown;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Spannable;
import android.text.Spanned;

import com.flaredown.flaredownApp.Styling;

/**
 * Created by thunter on 22/09/2015.
 */
public class DefaultErrors {
    API.API_Error apiError;
    Context context;

    private static String DEBUG_KEY = "FlareDown.DefaultErrors";


    public DefaultErrors(Context context, API.API_Error apiError) {
        this.apiError = apiError;
        this.context = context;

        int statusCode = this.apiError.statusCode;

        if(statusCode != 200) {

            Spanned errorText = Locales.read(context, "nice_errors." + String.valueOf(statusCode)).resultIfUnsuccessful("_no_message").capitalize1Char().createAT();
            if(errorText.toString().equals("_no_message")) {
                errorText = Locales.read(context, "nice_errors.500").resultIfUnsuccessful("Something went wrong, perhaps try again").capitalize1Char().createAT();
            }
            showPopup(context, "Error", errorText);

        }

    }

    private static Dialog showPopup (Context context, String title, String text) {
        return showPopup(context, title, Spannable.Factory.getInstance().newSpannable(text));
    }
    private static Dialog showPopup (Context context, String title, Spanned text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(text)
            .setTitle(title)
            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });


        Dialog dialog = builder.create();
        Styling.styleDialog(dialog);
        dialog.show();
        return dialog;
    }


    public static Dialog showNoInternetDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("No internet connection.");
        builder.setMessage("Your phone has lost internet connection, please reconnect to continue.");
        builder.setCancelable(false);

        Dialog dialog = builder.create();
        Styling.styleDialog(dialog);
        dialog.show();
        return dialog;


        /*Dialog dialog = showPopup(context, "No internet connection", "You phone has lost internet connection, please reconnect to continue.");
        dialog.setCancelable(false);
        return dialog;*/
    }
}
