package com.flaredown.flaredownApp.Helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;

/**
 * Helper class for Google's GSON helper.
 */

public class GsonHelper {
    private static Gson gson;

    /**
     * Get the default instance of Gson for the applicaiton, with custom type adaptors.
     * @return Gson object for converting strings to objects.
     */
    public static final Gson getGson() {
        if(gson == null) {
            // Create instance.
            gson = new GsonBuilder()
                    .registerTypeAdapter(Boolean.class, booleanAsIntAdapter)
                    .create();
        }
        return gson;
    }

    /**
     * Convert a JSON string to an object.
     * @param json The json object to convert to an object.
     * @param cls The class for the new object to be based upon.
     * @param <T> The type of the object to be created.
     * @return The newly created object from the JSON string.
     * @throws JsonSyntaxException Thrown when difficulty parsing the json.
     */
    public static final <T> T getFromJson(String json, Class<T> cls) throws JsonSyntaxException{
        return getGson().fromJson(json, cls);
    }

    /**
     * Convert an object to a JSON string.
     * @param object The object to convert to a JSON stirng.
     * @param <T> The type of the object to be converted.
     * @return JSON string which represents the object passed as a parameter.
     */
    public static final <T> String toJson(T object) {
        return getGson().toJson(object);
    }

    /**
     * Removes the first generation from a JSON string.
     * @param json The string in which to remove a first generation.
     * @return JSON string with the first generation removed.
     */
    public static final String removeFirstGeneration(String json) {
        try {
            Map<String, Object> jsonRoot = getFromJson(json, Map.class);
            return toJson(jsonRoot.get(jsonRoot.keySet().iterator().next()));
        } catch(NullPointerException e) {
        }
        return null;
    }

    private static final TypeAdapter<Boolean> booleanAsIntAdapter = new TypeAdapter<Boolean>() {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if(value == null)
                out.nullValue();
            else
                out.value(value);
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            switch (peek) {
                case BOOLEAN:
                    return in.nextBoolean();
                case NULL:
                    in.nextNull();
                    return null;
                case NUMBER:
                    return in.nextInt() != 0; // Allow numbers to be converted to ints.
                case STRING:
                    String value = in.nextString();
                    return Boolean.parseBoolean(value) || "1".equals(value);
                default:
                    throw new IllegalStateException("Expected Boolean but was " + peek); // Error could not parse the boolean.
            }
        }
    };

}
