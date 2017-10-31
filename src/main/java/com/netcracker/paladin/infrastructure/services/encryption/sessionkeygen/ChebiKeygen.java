package com.netcracker.paladin.infrastructure.services.encryption.sessionkeygen;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created on 27.10.14.
 */
public class ChebiKeygen implements SessionKeygen {
    private final KeyGenerator keyGenerator;

    public ChebiKeygen() {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128, new SecureRandom());
        }catch (NoSuchAlgorithmException e){
//            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] generateKey(){
        return keyGenerator.generateKey().getEncoded();
    }
}
