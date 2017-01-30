package com.flaredown.flaredownApp.Helpers;

import com.flaredown.flaredownApp.FlaredownApplication;
import com.flaredown.flaredownApp.R;

/**
 * Used for converting throwables to user friendly strings.
 */
public class ErrorMessageDeterminer {
    public static ErrorMessage getErrorMessage(Throwable throwable) {

        // User friendly throwables do not need modifying... as they are already friendly.
        if(UserFriendlyThrowable.class.equals(throwable.getClass()))
            return new ErrorMessage(throwable.getMessage());


        // Unknown message

        return new ErrorMessage(FlaredownApplication.getStringResource(R.string.locales_nice_errors_500));
    }

    public static class ErrorMessage {
        private String message;

        public ErrorMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
