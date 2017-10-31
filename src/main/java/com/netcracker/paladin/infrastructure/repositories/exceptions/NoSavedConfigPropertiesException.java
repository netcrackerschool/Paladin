package com.netcracker.paladin.infrastructure.repositories.exceptions;

/**
 * Created on 30.10.14.
 */
public class NoSavedConfigPropertiesException extends Exception {
    public NoSavedConfigPropertiesException() {
    }

    public NoSavedConfigPropertiesException(String s) {
        super(s);
    }

    public NoSavedConfigPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSavedConfigPropertiesException(Throwable cause) {
        super(cause);
    }
}
