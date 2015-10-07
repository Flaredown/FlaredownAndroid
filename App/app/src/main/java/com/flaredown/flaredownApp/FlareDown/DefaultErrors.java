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
            showPopup("Error", errorText);

        }

    }

    private void showPopup (String title, String text) {
        showPopup(title, Spannable.Factory.getInstance().newSpannable(text));
    }
    private void showPopup (String title, Spanned text) {
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

    }
}
