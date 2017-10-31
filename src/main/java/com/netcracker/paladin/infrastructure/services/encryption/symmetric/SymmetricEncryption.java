package com.netcracker.paladin.infrastructure.services.encryption.symmetric;

/**
 * Created on 27.10.14.
 */
public interface SymmetricEncryption {

    void setKey(byte[] key);

    byte[] encrypt(byte[] sequenceToEncrypt, byte[] sessionKey);

    byte[] decrypt(byte[] sequenceToDecrypt, byte[] sessionKey);
}
