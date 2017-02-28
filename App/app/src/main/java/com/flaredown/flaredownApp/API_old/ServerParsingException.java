package com.flaredown.flaredownApp.API_old;

/**
 * Object to be passed through the {@link OnRequestErrorListener}, providing details to why the
 * request failed.
 */
public class ServerParsingException extends Throwable {
    public ServerParsingException() {
    }

    public ServerParsingException(String message) {
        super(message);
    }

    public ServerParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerParsingException(Throwable cause) {
        super(cause);
    }
}
