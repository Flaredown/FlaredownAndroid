package com.flaredown.flaredownApp.FlareDown;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;

import com.flaredown.flaredownApp.PreferenceKeys;
import com.flaredown.flaredownApp.Styling;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by thunter on 22/09/2015.
 */
public class DefaultErrors {
    API_Error apiError;
    Context context;

    private static String DEBUG_KEY = "FlareDown.DefaultErrors";


    public DefaultErrors(Context context, API_Error apiError) {
        this.apiError = apiError;
        this.context = context;

        String response = "<b>No Response</b>";
        String title = Locales.read(context, "nice_errors.general_error").resultIfUnsuccessful("Opps!").create();
        String description = Locales.read(context, "nice_errors.general_error_description").resultIfUnsuccessful("Something went wrong here, please check all the usually suspects and try again.").create();
        try {
            response = new String(apiError.volleyError.networkResponse.data, "UTF-8");
        } catch (Exception e) {}

        try {
            JSONObject jError = new JSONObject(response).getJSONObject("errors");
            title = Locales.read(context, "nice_errors." + jError.getString("title")).resultIfUnsuccessful(title).create();
            description = Locales.read(context, "nice_errors." + jError.getString("description")).resultIfUnsuccessful(description).create();

        } catch (JSONException e) {
            description = Locales.read(context, "nice_errors." + String.valueOf(apiError.statusCode)).resultIfUnsuccessful(description).create();
        }

        if(PreferenceKeys.DEBUGGING) {
            description += "<br/><br/>---App is in debug mode extra detail below---<br/>";
            description += "<b>Error code: </b>" + apiError.statusCode + "<br/><b>Response:</b><br/>";
            description += response;
        }

        showPopup(title, Html.fromHtml(description));
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
