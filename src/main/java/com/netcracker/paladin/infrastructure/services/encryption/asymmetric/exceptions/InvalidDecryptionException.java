package com.netcracker.paladin.infrastructure.services.encryption.asymmetric.exceptions;

/**
 * Created on 10.12.14.
 */
public class InvalidDecryptionException extends IllegalStateException {
    public InvalidDecryptionException() {
    }

    public InvalidDecryptionException(String message) {
        super(message);
    }

    public InvalidDecryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDecryptionException(Throwable cause) {
        super(cause);
    }
}
