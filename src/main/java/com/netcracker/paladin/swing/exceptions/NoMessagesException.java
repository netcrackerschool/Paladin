package com.netcracker.paladin.swing.exceptions;

/**
 * Created on 29.10.14.
 */
public class NoMessagesException extends IllegalStateException {
    public NoMessagesException() {
    }

    public NoMessagesException(String s) {
        super(s);
    }

    public NoMessagesException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMessagesException(Throwable cause) {
        super(cause);
    }
}
