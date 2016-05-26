package com.flaredown.flaredownApp.Helpers.APIv2.Helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Helps processing the date for the api.
 */
public class Date {
    public static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Get the Calendar for a standard api date string.
     * @param date The date to parse.
     * @return The calendar for the date... Note if null date or incorrect date then null will be returned.
     */
    @Nullable
    public static Calendar stringToCalendar(@Nullable String date) {
        return stringToCalendar(date, API_DATE_FORMAT);
    }

    /**
     * Get the Calendar for a date string.
     * @param date The date to parse.
     * @param format The format the date is in.
     * @return The calendar for the date... Note if null date or incorrect date then null will be returned.
     */
    @Nullable
    public static Calendar stringToCalendar(@Nullable String date, @NonNull SimpleDateFormat format) {
        if(date == null || date.equals(""))
            return null;
        
        Calendar output = Calendar.getInstance();
        try {
            output.setTime(format.parse(date));
            return output;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Get the time in millis from a string representation of a date.
     * @param date The string representation of a date.
     * @param format The format of the date given.
     * @return The time in milliseconds.... Note returns null if error occurs.
     */
    @Nullable
    public static Long stringToMillis(@Nullable String date, @NonNull SimpleDateFormat format) {
        Calendar calendar = stringToCalendar(date, format);
        if(calendar != null)
            return calendar.getTimeInMillis();
        return null;
    }

    /**
     * Get the time in millis from a string representation of a date (In the API_DATE_FORMAT).
     * @param date The string representation of a date (In the API_DATE_FORMAT).
     * @return The time in milliseconds.... Note returns null if error occurs.
     */
    @Nullable
    public static Long stringToMillis(@Nullable String date) {
        return stringToMillis(date, API_DATE_FORMAT);
    }

    /**
     * Creates a calendar instance from a millisecond time stamp.
     * @param millis The time in millisecond values.
     * @return New calendar object set to the millis given.
     */
    public static Calendar millisToCalendar(@Nullable Long millis) {
        if(millis == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    /**
     * Formats the Calendar object passed into a string in the API date format.
     * @param date The date to be parsed.
     * @return The string fro the date... Note if null date then null will be returned.
     */
    @Nullable
    public static String calendarToString(@Nullable Calendar date) {
        return calendarToString(date, API_DATE_FORMAT);
    }

    /**
     * Formats the Calendar object passed into a string, determined by the SimpleDateFormat passed.
     * @param date The date to be parsed.
     * @param format The format to parse to.
     * @return The string fro the date... Note if null date then null will be returned.
     */
    @Nullable
    public static String calendarToString(@Nullable Calendar date, @NonNull SimpleDateFormat format) {
        if(date == null)
            return null;
        return format.format(date.getTime());
    }
}
