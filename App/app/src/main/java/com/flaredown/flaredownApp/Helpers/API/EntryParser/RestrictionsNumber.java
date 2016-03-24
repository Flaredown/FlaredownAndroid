package com.flaredown.flaredownApp.Helpers.API.EntryParser;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thunter on 21/02/16.
 */
public class RestrictionsNumber extends RestrictionsBlank {
    private Double value;
    private Double step;
    private Double min;
    private Double max;
    
    RestrictionsNumber(JSONObject catalogDefinitionJObject) {
        this.value = catalogDefinitionJObject.optDouble("value");
        this.step = catalogDefinitionJObject.optDouble("step");
        this.min = catalogDefinitionJObject.optDouble("min");
        this.max = catalogDefinitionJObject.optDouble("max");
    }

    RestrictionsNumber(double value, double step, double min, double max) {
        this.value = value;
        this.step = step;
        this.min = min;
        this.max = max;
    }

    /**
     * Tests if a number input is valid.
     * @param value The value to check.
     * @return
     */
    @Override
    public Valid isValidInput(Object value) {
        if(!(value instanceof Number)) { // Check that the input is a number.
            return new Valid("Input requires a number.");
        }
        if(min != null && ((Number) value).doubleValue() < min) {
            return new Valid("Input is below the minimum value.");
        }
        if(max != null && ((Number) value).doubleValue() > max) {
            return new Valid("Input is above the maximum value.");
        }
        return new Valid();
    }

    /**
     * Get json object representing the restrictions.
     * @return JSONObject representing the restrictions.
     * @throws JSONException
     */
    @Override
    public JSONObject getRestrictions() throws JSONException{
        JSONObject returnJObject = super.getRestrictions();
        returnJObject.putOpt("value", value);
        returnJObject.putOpt("step", step);
        returnJObject.putOpt("min", min);
        returnJObject.putOpt("max", max);
        return returnJObject;
    }

    /**
     * Returns the default value for a number, may return null.
     * @return The default value for a number restriction.
     */
    @Nullable
    @Override
    public Object getDefaultValue() {
        return value;
    }

    /**
     * Get the steps between numbers. Note may return null.
     * @return The steps between numbers.
     */
    @Nullable
    public Double getStep() {
        return step;
    }

    /**
     * Get the minimum value. Note may return null.
     * @return The minimum allowed value.
     */
    @Nullable
    public Double getMin() {
        return min;
    }

    /**
     * Get the maximum allowed value. Note may return null.
     * @return The maximum allowed value.
     */
    @Nullable
    public Double getMax() {
        return max;
    }
}
