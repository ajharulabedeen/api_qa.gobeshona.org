package org.gobeshona.api.exception;

public class EmailSendFailException extends Exception {
    public EmailSendFailException(String message) {
        super(message);
    }

    public EmailSendFailException(String message, Throwable cause) {
        super(message, cause);
    }
}

