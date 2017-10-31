package com.netcracker.paladin.infrastructure.services.encryption.asymmetric.exceptions;

/**
 * Created on 10.12.14.
 */
public class NoCorrectPrivateKeyException extends IllegalStateException {
    public NoCorrectPrivateKeyException() {
    }

    public NoCorrectPrivateKeyException(String s) {
        super(s);
    }

    public NoCorrectPrivateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoCorrectPrivateKeyException(Throwable cause) {
        super(cause);
    }
}
