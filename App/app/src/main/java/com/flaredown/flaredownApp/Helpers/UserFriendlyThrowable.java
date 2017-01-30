package com.flaredown.flaredownApp.Helpers;

/**
 * A throwable which message is user friendly
 */
public class UserFriendlyThrowable extends Throwable {
    public UserFriendlyThrowable(String message) {
        super(message);
    }

    public UserFriendlyThrowable(String message, Throwable cause) {
        super(message, cause);
    }
}
