package com.netcracker.paladin.infrastructure.services.encryption;

import com.netcracker.paladin.domain.PublicKeyEntry;
import com.netcracker.paladin.domain.SignedPublicKeyEntry;
import com.netcracker.paladin.infrastructure.repositories.PublicKeyEntryRepository;
import com.netcracker.paladin.infrastructure.services.encryption.asymmetric.AsymmetricEncryption;
import com.netcracker.paladin.infrastructure.services.encryption.asymmetric.exceptions.InvalidDecryptionException;
import com.netcracker.paladin.infrastructure.services.encryption.asymmetric.exceptions.NoCorrectPrivateKeyException;
import com.netcracker.paladin.infrastructure.services.encryption.exceptions.NoPrivateKeyException;
import com.netcracker.paladin.infrastructure.services.encryption.sessionkeygen.SessionKeygen;
import com.netcracker.paladin.infrastructure.services.encryption.symmetric.SymmetricEncryption;
import org.apache.commons.lang3.ArrayUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created on 27.10.14.
 */
public class EncryptionServiceImpl implements EncryptionService {
    private final PublicKeyEntryRepository publicKeyEntryRepository;
    private final AsymmetricEncryption asymmetricEncryption;
    private final SymmetricEncryption symmetricEncryption;
    private final SessionKeygen sessionKeygen;

    private byte[] mainPrivateKey;
    private Map<byte[], byte[]> allKeyPairs;
//    private Set<byte[]> allPrivateKeys;

    public EncryptionServiceImpl(
            PublicKeyEntryRepository publicKeyEntryRepository,
            AsymmetricEncryption asymmetricEncryption,
            SymmetricEncryption symmetricEncryption,
            SessionKeygen sessionKeygen) {
        this.publicKeyEntryRepository = publicKeyEntryRepository;
        this.asymmetricEncryption = asymmetricEncryption;
        this.symmetricEncryption = symmetricEncryption;
        this.sessionKeygen = sessionKeygen;
//        this.allPrivateKeys = new HashSet<>();
        this.allKeyPairs = new HashMap<>();
    }

    @Override
    public byte[] encryptEmail(String plainText, String recipient){
        try {
            byte[] sessionKey = sessionKeygen.generateKey();
            byte[] cipherText = symmetricEncryption.encrypt(plainText.getBytes("UTF-8"), sessionKey);
            byte[] recipientPublicKey = findPublicKey(recipient);
            System.out.println("Sending length: "+recipientPublicKey.length);
            byte[] encryptedSessionKey = asymmetricEncryption.encrypt(sessionKey, recipientPublicKey);
            byte[] cipherTextAndEncryptedSessionKey = ArrayUtils.addAll(encryptedSessionKey, cipherText);
//            byte[] cipherBlob = ArrayUtils.addAll(recipientPublicKey, cipherTextAndEncryptedSessionKey);
//            return cipherBlob;
            return cipherTextAndEncryptedSessionKey;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String decryptEmail(byte[] cipherTextAndEncryptedSessionKey){
        try {
//            byte[] usedPublicKey = ArrayUtils.subarray(cipherBlob, 0, asymmetricEncryption.getPublicKeySize());
//            byte[] cipherTextAndEncryptedSessionKey = ArrayUtils.subarray(cipherBlob, asymmetricEncryption.getPublicKeySize(), cipherBlob.length);
//
//            byte[] selectedPrivateKey = null;
//            for(Map.Entry<byte[], byte[]> keyPair : allKeyPairs.entrySet()){
//                System.out.println("Another public key comparison");
//                System.out.println("Lengths: "+keyPair.getKey().length+" "+usedPublicKey.length);
//                for(int i = 0; i < usedPublicKey.length; i++){
//                    System.out.println(keyPair.getKey()[i]+" "+usedPublicKey[i]+" "+i);
//                }
//                if(Arrays.equals(keyPair.getKey(), usedPublicKey)){
//                    selectedPrivateKey = keyPair.getValue();
//                    break;
//                }
//            }
//            if(selectedPrivateKey == null){
//                throw new NoCorrectPrivateKeyException();
//            }
//
//            System.out.println("Hop");

            byte[] encryptedSessionKey = ArrayUtils.subarray(cipherTextAndEncryptedSessionKey, 0, 128);
            byte[] cipherText = ArrayUtils.subarray(cipherTextAndEncryptedSessionKey, 128, cipherTextAndEncryptedSessionKey.length);

            try{
                byte[] sessionKey = asymmetricEncryption.decrypt(encryptedSessionKey, getMainPrivateKey());
                byte[] plainText = symmetricEncryption.decrypt(cipherText, sessionKey);
                return new String(plainText, "UTF-8");
            }catch (Exception e){
            }

            for(byte[] currentPrivateKey : allKeyPairs.values()){
                try{
                    byte[] sessionKey = asymmetricEncryption.decrypt(encryptedSessionKey, currentPrivateKey);
                    byte[] plainText = symmetricEncryption.decrypt(cipherText, sessionKey);
                    return new String(plainText, "UTF-8");
                }catch (Exception e){
                }
            }

            throw new NoCorrectPrivateKeyException();
//            byte[] sessionKey = asymmetricEncryption.decrypt(encryptedSessionKey, getMainPrivateKey());
//            byte[] sessionKey = asymmetricEncryption.decrypt(encryptedSessionKey, selectedPrivateKey);
//            byte[] plainText = symmetricEncryption.decrypt(cipherText, sessionKey);
//            return new String(plainText, "UTF-8");
        } catch (NullPointerException e){
            e.printStackTrace();
            throw new IllegalStateException(e);
        } catch (InvalidDecryptionException e){
            System.err.println("We spotted it");
            throw new NoCorrectPrivateKeyException();
        } catch (NoCorrectPrivateKeyException e){
            throw new NoCorrectPrivateKeyException();
        } catch (Exception e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] generatePrivateKey(){
        byte[] generatedKey = asymmetricEncryption.generatePrivateKey();
        this.allKeyPairs.put(asymmetricEncryption.generatePublicKey(generatedKey), generatedKey);
//        this.allPrivateKeys.add(generatedKey);
        return generatedKey;
    }

    public void setMainPrivateKey(byte[] mainPrivateKey){
        this.mainPrivateKey = mainPrivateKey;
        this.allKeyPairs.put(asymmetricEncryption.generatePublicKey(mainPrivateKey), mainPrivateKey);
//        this.allPrivateKeys.add(mainPrivateKey);
    }

    @Override
    public void addPrivateKey(byte[] newPrivateKey){
        this.allKeyPairs.put(asymmetricEncryption.generatePublicKey(newPrivateKey), newPrivateKey);
//        this.allPrivateKeys.add(newPrivateKey);
        if(mainPrivateKey == null){
            mainPrivateKey = newPrivateKey;
        }
    }

//    @Override
//    public void deletePrivateKey(byte[] privateKey){
//        this.allPrivateKeys.remove(privateKey);
//    }

    @Override
    public Collection<byte[]> getAllPrivateKeys(){
        return this.allKeyPairs.values();
//        return this.allPrivateKeys;
    }

    @Override
    public byte[] getOwnPublicKey(){
        return asymmetricEncryption.generatePublicKey(getMainPrivateKey());
    }

    @Override
    public void addPublicKey(PublicKeyEntry publicKeyEntry){
        publicKeyEntryRepository.insert(publicKeyEntry);
    }

    @Override
    public void addPublicKey(String email, byte[] publicKey){
        addPublicKey(new PublicKeyEntry(email, publicKey));
    }

    @Override
    public byte[] getPublicKey(String email){
        return publicKeyEntryRepository.findByEmail(email).getPublicKey();
    }

    @Override
    public void deletePublicKey(String email){
        publicKeyEntryRepository.deleteByEmail(email);
    }

    @Override
    public List<PublicKeyEntry> getAllPublicKeyEntries(){
        return publicKeyEntryRepository.findAll();
    }

    @Override
    public List<String> getAllEmailsWithPublicKey(){
        List<PublicKeyEntry> allPublicKeyEntries = getAllPublicKeyEntries();
        List<String> allEmailsWithPublicKey = new ArrayList<>(allPublicKeyEntries.size());
        for(PublicKeyEntry publicKeyEntry : allPublicKeyEntries){
            allEmailsWithPublicKey.add(publicKeyEntry.getEmail());
        }
        return allEmailsWithPublicKey;
    }

    @Override
    public byte[] getSignature(byte[] data){
        return asymmetricEncryption.createSignature(data, mainPrivateKey);
    }

    @Override
    public SignedPublicKeyEntry getSignedPublicKeyEntry(String email){
        byte[] publicKey = publicKeyEntryRepository.findByEmail(email).getPublicKey();
        byte[] emailBytes = email.getBytes();
        byte[] publicKeyWithEmail = ArrayUtils.addAll(publicKey, emailBytes);
        byte[] signature = asymmetricEncryption.createSignature(publicKeyWithEmail, mainPrivateKey);
        return new SignedPublicKeyEntry(null, signature, publicKeyWithEmail);
    }

    @Override
    public SignedPublicKeyEntry getSignedPublicKeyEntry(PublicKeyEntry publicKeyEntry){
        String email = publicKeyEntry.getEmail();
        byte[] publicKey = publicKeyEntry.getPublicKey();
        byte[] emailBytes = publicKeyEntry.getEmail().getBytes();
        byte[] publicKeyWithEmail = ArrayUtils.addAll(publicKey, emailBytes);
        byte[] signature = asymmetricEncryption.createSignature(publicKeyWithEmail, mainPrivateKey);
        return new SignedPublicKeyEntry(null, signature, publicKeyWithEmail);
    }

    @Override
    public PublicKeyEntry getPublicKeyEntry(SignedPublicKeyEntry signedPublicKeyEntry){
        try {
            byte[] publicKeyWithEmail = signedPublicKeyEntry.getPublicKeyWithEmail();
            byte[] publicKey = ArrayUtils.subarray(publicKeyWithEmail, 0, 256);
            String email = new String(ArrayUtils.subarray(publicKeyWithEmail, 256, publicKeyWithEmail.length), "UTF-8");
            return new PublicKeyEntry(email, publicKey);
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean verifySignature(byte[] publicKey, byte[] signature, byte[] data){
        return asymmetricEncryption.verifySignature(data, signature, publicKey);
    }

    @Override
    public boolean verifySignature(String signerEmail, byte[] signature, byte[] data){
        byte[] publicKey = publicKeyEntryRepository.findByEmail(signerEmail).getPublicKey();
        return verifySignature(publicKey, signature, data);
    }

    @Override
    public boolean verifySignature(SignedPublicKeyEntry signedPublicKeyEntry){
        return verifySignature(
                signedPublicKeyEntry.getSignerEmail(),
                signedPublicKeyEntry.getSignature(),
                signedPublicKeyEntry.getPublicKeyWithEmail());
    }

    @Override
    public byte[] findPublicKey(String email){
        return publicKeyEntryRepository.findByEmail(email).getPublicKey();
    }

    @Override
    public byte[] getMainPrivateKey(){
        if(mainPrivateKey == null){
            if(allKeyPairs.values().size() == 0){
                throw new NoPrivateKeyException();
            }
            for(byte[] privateKey : allKeyPairs.values()) {
                mainPrivateKey = privateKey;
                break;
            }
        }
        return mainPrivateKey;
    }
}
