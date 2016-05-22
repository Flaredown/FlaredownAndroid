package com.flaredown.flaredownApp.Helpers;

/**
 * Helper class for formatting strings.
 */
public class StringHelper {

    /**
     * Converts the first character of the string to a capital letter.
     * @param str The string for formatting.
     * @return String where first char is capitalised (Immutable).
     */
    public static String upperFirstChar(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Ensures a string ends with a full stop.
     * @param str The string.
     * @return A string with a guaranteed full stop at the end.
     */
    public static String forceFullStop(String str) {
        return str + (str.endsWith(".")? "" : ".");
    }
}
