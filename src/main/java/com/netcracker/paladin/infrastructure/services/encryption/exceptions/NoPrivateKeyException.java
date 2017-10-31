package com.netcracker.paladin.infrastructure.services.encryption.exceptions;

/**
 * Created on 29.10.14.
 */
public class NoPrivateKeyException extends IllegalStateException {
    public NoPrivateKeyException() {
    }

    public NoPrivateKeyException(String s) {
        super(s);
    }

    public NoPrivateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPrivateKeyException(Throwable cause) {
        super(cause);
    }
}
