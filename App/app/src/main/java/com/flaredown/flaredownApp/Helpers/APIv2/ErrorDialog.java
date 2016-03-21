package com.flaredown.flaredownApp.Helpers.APIv2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import com.flaredown.flaredownApp.Helpers.PreferenceKeys;
import com.flaredown.flaredownApp.Helpers.Styling;
import com.flaredown.flaredownApp.R;

/**
 * Displays a error dialog to the user. Created from the Error object past at construction.
 */
public class ErrorDialog {
    private static final String DEBUG_KEY = "ErrorDialog";
    Error apiError;
    Context context;

    public ErrorDialog(Context context, Error apiError) {
        this.apiError = apiError;
        this.context = context;
        constructDialog();
    }


    private void constructDialog() {
        // TODO Display errors with the correct error message.
        // TODO Give option the restrict closing the dialog.
        String title = "", description = "";

        if(context == null) {
            PreferenceKeys.log(PreferenceKeys.LOG_E, DEBUG_KEY, getExtraDebugginString());
        } else {
            title = context.getString(R.string.locales_nice_errors_general_error);
            description = context.getString(R.string.locales_nice_errors_general_error_description);

            if(PreferenceKeys.DEBUGGING) {
                description += getExtraDebugginString();
            }

            // Display the pop up.
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(title);
            builder.setMessage(Html.fromHtml(description));

            // TODO offer retry option.

            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            Dialog dialog = builder.create();
            Styling.styleDialog(dialog);
            dialog.show();
        }
    }

    /**
     * Gets the html string for displaying extra debugging information.
     * @return
     */
    private String getExtraDebugginString() {
        String output = "";
        output += "<br/><br/>---App is in debug mode extra details below ---<br/>";
        output += "<b>Debug String:</b> " + apiError.getDebugString() + "<br/>";
        output += "<b>Error code:</b> " + apiError.getStatusCode() + "<br/>";
        output += "<b>Response: </b> ";

        String response = "<b>No Response</b>";

        try {
            response = new String(apiError.getVolleyError().networkResponse.data);
        } catch (NullPointerException e) {}

        output += response;

        return output;
    }
}
