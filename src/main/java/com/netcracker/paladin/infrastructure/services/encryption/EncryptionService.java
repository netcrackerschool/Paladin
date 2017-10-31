package com.netcracker.paladin.infrastructure.services.encryption;

import com.netcracker.paladin.domain.PublicKeyEntry;
import com.netcracker.paladin.domain.SignedPublicKeyEntry;

import java.util.Collection;
import java.util.List;

/**
 * Created on 27.10.14.
 */
public interface EncryptionService {
    byte[] encryptEmail(String plainText, String recipient);

    String decryptEmail(byte[] cipherTextAndEncryptedSessionKey);

    byte[] generatePrivateKey();

    void setMainPrivateKey(byte[] mainPrivateKey);

    void addPrivateKey(byte[] newPrivateKey);

//    void deletePrivateKey(byte[] privateKey);

    Collection<byte[]> getAllPrivateKeys();

    byte[] getOwnPublicKey();

    void addPublicKey(PublicKeyEntry publicKeyEntry);

    void addPublicKey(String email, byte[] publicKey);

    byte[] getPublicKey(String email);

    void deletePublicKey(String email);

    List<PublicKeyEntry> getAllPublicKeyEntries();

    List<String> getAllEmailsWithPublicKey();

    byte[] getSignature(byte[] data);

    SignedPublicKeyEntry getSignedPublicKeyEntry(String email);

    SignedPublicKeyEntry getSignedPublicKeyEntry(PublicKeyEntry publicKeyEntry);

    PublicKeyEntry getPublicKeyEntry(SignedPublicKeyEntry signedPublicKeyEntry);

    boolean verifySignature(byte[] publicKey, byte[] signature, byte[] data);

    boolean verifySignature(String senderEmail, byte[] signature, byte[] data);

    boolean verifySignature(SignedPublicKeyEntry signedPublicKeyEntry);

    byte[] findPublicKey(String email);

    byte[] getMainPrivateKey();
}
