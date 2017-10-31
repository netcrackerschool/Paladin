package com.netcracker.paladin.domain;

/**
 * Created on 27.10.14.
 */
public class PublicKeyEntry {
    private final String email;
    private final byte[] publicKey;

    public PublicKeyEntry(String email, byte[] publicKey) {
        this.email = email;
        this.publicKey = publicKey;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }
}
