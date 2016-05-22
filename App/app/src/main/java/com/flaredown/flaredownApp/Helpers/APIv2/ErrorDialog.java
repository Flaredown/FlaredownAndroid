package com.flaredown.flaredownApp.Helpers.APIv2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.Html;

import com.flaredown.flaredownApp.Helpers.InternetConnectivity;
import com.flaredown.flaredownApp.Helpers.PreferenceKeys;
import com.flaredown.flaredownApp.Helpers.StringHelper;
import com.flaredown.flaredownApp.R;
import com.flaredown.flaredownApp.Receivers.InternetStatusBroadcastReceiver;

import java.util.List;

/**
 * Displays a error dialog to the user. Created from the Error object past at construction.
 */
public class ErrorDialog {
    private static final String DEBUG_KEY = "ErrorDialog";
    private Error apiError;
    private Context context;
    private boolean cancelable = true;
    private InternetStatusBroadcastReceiver internetStatusBroadcastReceiver;

    public ErrorDialog(@NonNull Context context, @NonNull Error apiError) {
        this.apiError = apiError;
        this.context = context;
    }

    /**
     * Should the user be able to cancel the error dialog.
     * @param cancelable True if the user can cancel the error dialog.
     * @return Itself.
     */
    public ErrorDialog setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    /**
     * Show the error dialog.
     */
    public void show() {
        String title = "", description = "";
        final boolean internetConnected = InternetConnectivity.isConnected(context);

        // Set the title and description of the dialog.
        title = context.getString(R.string.locales_nice_errors_general_error); // Default value.
        description = context.getString(R.string.locales_nice_errors_general_error_description); // Default values.

        if(!internetConnected) {
            description = context.getString(R.string.locales_nice_errors_no_internet);
        } else if(!apiError.isResponseGiven()) {
            description = context.getString(R.string.locales_nice_errors_no_response);
        } else {
            String identifier = "locales_nice_errors_" + apiError.getStatusCode();
            int resId = context.getResources().getIdentifier(identifier, "string", context.getPackageName());
            if(resId != 0) { // Only if resId found.
                description = context.getString(resId);
            } else {
                List<String> errorMessages = apiError.getErrorList();
                boolean first = true;
                if(errorMessages.size() > 0) {
                    description = "";
                    for (String errorMessage : errorMessages) {
                        if(!first) description += "<br/>";
                        description += StringHelper.forceFullStop(StringHelper.upperFirstChar(errorMessage));
                        first = false;
                    }
                }
            }
        }

        if(PreferenceKeys.DEBUGGING) { // Append extra information if debugging
            description += getExtraDebugginString();
        }

        // Build the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(Html.fromHtml(description));

        // Set the buttons.
        // Ok button.
        if(cancelable) {
            builder.setPositiveButton(context.getString(R.string.locales_nav_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // When clicked the dialog will automatically close.
                }
            });
        }
        builder.setCancelable(cancelable); // If not cancelable prevent user from closing dialog.

        // Retry button.
        if(apiError.getRetryRunnable() != null) {
            builder.setNegativeButton(context.getString(R.string.locales_nav_retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    apiError.getRetryRunnable().run();
                }
            });
        }

        final Dialog dialog = builder.create();
        dialog.show();
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
        if(apiError.getExceptionThrown() != null)
            output += "<b>Exception Thrown</b>" + apiError.getExceptionThrown().getMessage() + "<br/>";
        output += "<b>Response: </b> ";

        String response = "<b>No Response</b>";

        try {
            response = new String(apiError.getVolleyError().networkResponse.data);
        } catch (NullPointerException e) {}

        output += response;

        return output;
    }
}
