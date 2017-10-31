package com.netcracker.paladin.infrastructure.services.encryption.asymmetric;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created on 27.10.14.
 */
public interface AsymmetricEncryption {

    int getPublicKeySize();

    byte[] generatePrivateKey();

    byte[] generatePublicKey(byte[] privateKeyBytes);

    byte[] encrypt(byte[] sequenceToEncrypt, PublicKey publicKey);

    byte[] encrypt(byte[] sequenceToEncrypt, byte[] publicKeyBytes);

    byte[] decrypt(byte[] sequenceToDecrypt, PrivateKey privateKey);

    byte[] decrypt(byte[] sequenceToDecrypt, byte[] privateKeyBytes);

    byte[] createSignature(byte[] data, byte[] privateKeyBytes);

    boolean verifySignature(byte[] data, byte[] signatureBytes, byte[] publicKeyBytes);
}
