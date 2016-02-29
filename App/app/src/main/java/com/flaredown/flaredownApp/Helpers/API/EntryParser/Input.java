package com.flaredown.flaredownApp.Helpers.API.EntryParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thunter on 21/02/16.
 */
public class Input {
    Object value;
    String label;
    String metaLabel;
    String helper;
    RestrictionsBlank restrictionsBlank = new RestrictionsBlank();

    /**
     * Default consturctor for input.
     */
    public Input() {

    }

    /**
     * Create Input object from JSON Object representation.
     * @param inputJObject Input object JSON Object representation.
     * @throws JSONException
     */
    public Input(JSONObject inputJObject) throws JSONException{
        this.value = inputJObject.get("value");
        this.label = inputJObject.optString("label", null);
        this.metaLabel = inputJObject.optString("meta_label", null);
        this.helper = inputJObject.optString("helper", null);

        //TODO implement restrictions.
    }

    /**
     * Create Input object from with predefined values.
     * @param value The value of the input.
     * @param helper The helper for the input.
     * @param metaLabel The meat label for the input.
     */
    public Input(Object value, String helper, String metaLabel) {
        this.value = value;
        this.metaLabel = metaLabel;
        this.helper = helper;
    }

    /**
     * Convert object to JSON object representation.
     * @return JSON object representing this Input.
     * @throws JSONException
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject returnJObject = new JSONObject();

        returnJObject.put("value", value);
        returnJObject.putOpt("label", label);
        returnJObject.putOpt("meta_label", metaLabel);
        returnJObject.putOpt("helper", helper);

        return returnJObject;
    }

    /**
     * Get the input value.
     * @return the input value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Get the label name.
     * @return The label name.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the meta label.
     * @return The meta label.
     */
    public String getMetaLabel() {
        return metaLabel;
    }

    /**
     * Get the helper.
     * @return The helper.
     */
    public String getHelper() {
        return helper;
    }

    /**
     * Get the restrictions for this input.
     * @return The restrictions for this input.
     */
    public RestrictionsBlank getRestrictiions() {
        return restrictionsBlank;
    }

    public static List<Input> createDefaultSmilySelect() {
        List<Input> returnInput = new ArrayList<>();

        returnInput.add(new Input(0, "basic_0", "smiley"));
        returnInput.add(new Input(1, "basic_1", null));
        returnInput.add(new Input(2, "basic_2", null));
        returnInput.add(new Input(3, "basic_3", null));
        returnInput.add(new Input(4, "basic_4", null));

        return returnInput;
    }
}
