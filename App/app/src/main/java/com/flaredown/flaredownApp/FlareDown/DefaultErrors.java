package com.flaredown.flaredownApp.FlareDown;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Spannable;
import android.text.Spanned;

import com.flaredown.flaredownApp.PreferenceKeys;
import com.flaredown.flaredownApp.Styling;

import org.json.JSONObject;
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
        boolean popupShown = false;

        int statusCode = this.apiError.statusCode;


        if(apiError.volleyError != null && apiError.volleyError.networkResponse.data != null) {
            try {
                String response = new String(apiError.volleyError.networkResponse.data, "UTF-8");
                PreferenceKeys.log(PreferenceKeys.LOG_E, DEBUG_KEY, response);

                if (response.length() > 0) {
                    JSONObject jError = new JSONObject(response).getJSONObject("errors");

                    if (jError.has("title") && jError.has("description")) {
                        String title = Locales.read(context, "nice_errors." + jError.getString("title")).create();
                        String description = Locales.read(context, "nice_errors." + jError.get("description")).create();


                        showPopup(title, description);
                        popupShown = true;
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if(statusCode != 200 && !popupShown) {

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
