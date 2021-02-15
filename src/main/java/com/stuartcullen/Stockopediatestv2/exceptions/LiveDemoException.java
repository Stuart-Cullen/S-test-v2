package com.stuartcullen.Stockopediatestv2.exceptions;

/**
 * Stuart Cullen - 2021-02-14
 *
 * An exception that a user is permitted to see on the UI
 */
public class LiveDemoException extends Exception {

    public LiveDemoException() { }

    public LiveDemoException(String message) {
        super(message);
    }

    public LiveDemoException(String message, Throwable cause) {
        super(message, cause);
    }

    public LiveDemoException(Throwable cause) {
        super(cause);
    }

    public LiveDemoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @return A UI safe message
     */
    public String getMessageForWebUI() {
        return getLocalizedMessage();
    }

}
