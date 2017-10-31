package com.netcracker.paladin.domain;

/**
 * Created on 02.12.14.
 */
public class SignedPublicKeyEntry {
    private final String signerEmail;
    private final byte[] signature;
    private final byte[] publicKeyWithEmail;

    public SignedPublicKeyEntry(String signerEmail, byte[] signature, byte[] publicKeyWithEmail) {
        this.signerEmail = signerEmail;
        this.signature = signature;
        this.publicKeyWithEmail = publicKeyWithEmail;
    }

    public String getSignerEmail() {
        return signerEmail;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] getPublicKeyWithEmail() {
        return publicKeyWithEmail;
    }
}
