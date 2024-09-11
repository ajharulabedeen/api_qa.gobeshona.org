package org.gobeshona.api.exception;

public class NoEmailAddressException extends RuntimeException {
    public NoEmailAddressException(String message) {
        super(message);
    }
}
