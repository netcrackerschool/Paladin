package com.netcracker.paladin.infrastructure.services.encryption.symmetric;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created on 27.10.14.
 */
public class Aes implements SymmetricEncryption {

    private  SecretKeySpec secretKey;
    private  byte[] key;

//    public int getKeySize(){
//        return KEYSIZE;
//    }

    @Override
    public void setKey(byte[] key)
    {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] encrypt(byte[] sequenceToEncrypt, byte[] sessionKey)
    {
        try
        {
            setKey(sessionKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(sequenceToEncrypt);
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    @Override
    public byte[] decrypt(byte[] sequenceToDecrypt, byte[] sessionKey)
    {
        try
        {
            setKey(sessionKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(sequenceToDecrypt);
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
