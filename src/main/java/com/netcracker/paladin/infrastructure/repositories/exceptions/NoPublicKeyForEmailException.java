package com.netcracker.paladin.infrastructure.repositories.exceptions;

/**
 * Created on 29.10.14.
 */
public class NoPublicKeyForEmailException extends IllegalStateException {
    public NoPublicKeyForEmailException() {
    }

    public NoPublicKeyForEmailException(String message) {
        super(message);
    }

    public NoPublicKeyForEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoPublicKeyForEmailException(Throwable cause) {
        super(cause);
    }
}
