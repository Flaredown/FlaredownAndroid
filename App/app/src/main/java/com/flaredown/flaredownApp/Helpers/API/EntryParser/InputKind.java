package com.flaredown.flaredownApp.Helpers.API.EntryParser;

/**
 * Created by thunter on 21/02/16.
 */
public enum InputKind {
    CHECKBOX ("checkbox"),
    SELECT ("select"),
    NUMBER ("number"),
    UNKNOWN ("unknown");

    private final String name;

    InputKind(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null && name.equals(otherName));
    }

    public static InputKind toEnum(String key) {
        InputKind inputKinds[] = InputKind.values();
        for (InputKind inputKind : inputKinds) {
            if(inputKind.toString().equals(key))
                return inputKind;
        }
        return InputKind.UNKNOWN;
    }

    public String toString() {
        return this.name;
    }
}
