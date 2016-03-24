package com.flaredown.flaredownApp.Helpers.API.EntryParser;

/**
 * Created by thunter on 21/02/16.
 */

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Default object for a catalog definition with no restrictions to it's value.
 * For example a check box or select box.
 */
public class RestrictionsBlank {

    /**
     * Tests if a value is valid, and returns a error message.
     * @param value The value to check.
     * @return Object describing if the value is valid and why.
     */
    public Valid isValidInput(Object value) {
        return new Valid();
    }

    /**
     * Gets a json object with all the restrictions for a catalog definition.
     * @return
     */
    public JSONObject getRestrictions() throws JSONException{
        return new JSONObject();
    }

    /**
     * Returns null, as no value given...
     * @return null
     */
    @Nullable
    public Object getDefaultValue() {
        return null;
    }

    /**
     * Return object for the isValidInput method.
     */
    protected class Valid {
        private boolean isValid = false;
        private String errorMessage = "UNKNOWN";

        /**
         * Create an Invalid return object with an error message
         * @param errorMessage Why the validation failed.
         */
        Valid(String errorMessage) {
            this.isValid = false;
            this.errorMessage = errorMessage;
        }

        Valid() {
            this.isValid = true;
        }

    }
}
