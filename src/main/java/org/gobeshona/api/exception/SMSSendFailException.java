package org.gobeshona.api.exception;

public class SMSSendFailException extends Exception {
    public SMSSendFailException(String message) {
        super(message);
    }

    public SMSSendFailException(String message, Throwable cause) {
        super(message, cause);
    }
}
