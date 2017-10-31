package com.netcracker.paladin.infrastructure.services.email;

import com.netcracker.paladin.domain.MessageEntry;
import com.netcracker.paladin.domain.SignedPublicKeyEntry;
import com.netcracker.paladin.infrastructure.services.config.ConfigService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.MultiPartEmail;

import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EmailServiceImpl implements EmailService {

    private final ConfigService configService;

    private final String SIGNED_PUBKEY_SUBJECT = "SIGNATURE_PUBLIC_KEY";

    public EmailServiceImpl(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public void sendEmail(String toAddress,
                          String subject,
                          String message,
                          byte[] cipherBlob,
                          byte[] signature) throws AddressException, MessagingException, IOException {

        if(subject.equals(SIGNED_PUBKEY_SUBJECT)){
            subject += "_escaped";
        }

        Properties smtpProperties = configService.loadProperties();
        final String username = smtpProperties.getProperty("mail.user");
        final String password = smtpProperties.getProperty("mail.password");
        final String hostname = smtpProperties.getProperty("mail.smtp.host");
        final String port = smtpProperties.getProperty("mail.smtp.port");

        try {
            MultiPartEmail email = new MultiPartEmail();
            email.setHostName(hostname);
            email.setSmtpPort(Integer.parseInt(port));
            System.out.println(Integer.parseInt(port));
            email.setAuthenticator(new DefaultAuthenticator(username, password));
            email.setSSLOnConnect(true);
            email.setFrom(username);
            email.setSubject(subject);
            email.setMsg(message);
            email.addTo(toAddress);

            DataSource cipherBlobSource = new ByteArrayDataSource(cipherBlob, "application/python-pickle");
            email.attach(cipherBlobSource, "Chebi", "Naum molodec");

            DataSource signatureSource = new ByteArrayDataSource(signature, "application/python-pickle");
            email.attach(signatureSource, "ChebiSignature", "Naum molodec");

            email.send();
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<MessageEntry> readEmails() {
        try {
            Properties properties = configService.loadProperties();
            properties.setProperty("mail.store.protocol", "imaps");

            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("imaps");
            store.connect(
                    properties.getProperty("mail.smtp.host"),
                    properties.getProperty("mail.user"),
                    properties.getProperty("mail.password"));
            
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            Message[] messages = emailFolder.getMessages();
            List<MessageEntry> messageEntryList = new ArrayList<>(messages.length);
            
            for(Message message : messages){

                if(message.getSubject().equals(SIGNED_PUBKEY_SUBJECT)){
                    continue;
                }

                MessageEntry messageEntry = new MessageEntry();

                messageEntry.setFrom(message.getFrom()[0].toString());
                messageEntry.setSubject(message.getSubject());
                messageEntry.setPlainMessage(getText(message));
                messageEntry.setSentDate(message.getSentDate());

                String contentType = message.getContentType();
                if (contentType.contains("multipart")) {
                    Multipart multiPart = (Multipart) message.getContent();
                    for (int i = 0; i < multiPart.getCount(); i++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            if(part.getFileName().equals("Chebi")){
                                byte[] cipherBlob = IOUtils.toByteArray(part.getInputStream());
                                messageEntry.setCipherBlob(cipherBlob);
                            }
                            if(part.getFileName().equals("ChebiSignature")){
                                byte[] signature = IOUtils.toByteArray(part.getInputStream());
                                messageEntry.setSignature(signature);
                            }
                        }
                    }
                }

                messageEntryList.add(messageEntry);
            }

            emailFolder.close(true);
            store.close();
            
            return messageEntryList;
            
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    @Override
    public void sendSignedPublicKey(String toAddress, SignedPublicKeyEntry signedPublicKeyEntry){

        Properties smtpProperties = configService.loadProperties();
        final String username = smtpProperties.getProperty("mail.user");
        final String password = smtpProperties.getProperty("mail.password");
        final String hostname = smtpProperties.getProperty("mail.smtp.host");
        final String port = smtpProperties.getProperty("mail.smtp.port");

        try {
            MultiPartEmail email = new MultiPartEmail();
            email.setHostName(hostname);
            email.setSmtpPort(Integer.parseInt(port));
            email.setAuthenticator(new DefaultAuthenticator(username, password));
            email.setSSLOnConnect(true);
            email.setFrom(username);
            email.setSubject(SIGNED_PUBKEY_SUBJECT);
            email.setMsg("Signed public key");
            email.addTo(toAddress);

            DataSource data = new ByteArrayDataSource(signedPublicKeyEntry.getPublicKeyWithEmail(), "application/python-pickle");
            email.attach(data, "Data", "Naum molodec");

            DataSource signature = new ByteArrayDataSource(signedPublicKeyEntry.getSignature(), "application/python-pickle");
            email.attach(signature, "Signature", "Naum molodec");

            email.send();
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public List<SignedPublicKeyEntry> readSignedPublicKeys(){
        try {
            Properties properties = configService.loadProperties();
            properties.setProperty("mail.store.protocol", "imaps");

            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("imaps");
            store.connect(
                    properties.getProperty("mail.smtp.host"),
                    properties.getProperty("mail.user"),
                    properties.getProperty("mail.password"));

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            Message[] messages = emailFolder.getMessages();
            List<SignedPublicKeyEntry> signedPublicKeyEntryList = new ArrayList<>(messages.length);

            for(Message message : messages){

                if(message.getSubject().equals(SIGNED_PUBKEY_SUBJECT) == false){
                    continue;
                }

                String signerEmail = message.getFrom()[0].toString();

                byte[] publicKeyWithEmail = null;
                byte[] signature = null;

                String contentType = message.getContentType();
                if (contentType.contains("multipart")) {
                    Multipart multiPart = (Multipart) message.getContent();
                    for (int i = 0; i < multiPart.getCount(); i++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            if(part.getFileName().equals("Data")){
                                if(publicKeyWithEmail != null){
                                    throw new IllegalStateException("Double Data attachment in signed public key");
                                }
                                publicKeyWithEmail = IOUtils.toByteArray(part.getInputStream());
                            }
                            if(part.getFileName().equals("Signature")){
                                if(signature != null){
                                    throw new IllegalStateException("Double Signature attachment in signed public key");
                                }
                                signature = IOUtils.toByteArray(part.getInputStream());
                                System.out.println("Size of signature: "+signature.length);
                            }
                        }
                    }
                }

                if(signature == null || publicKeyWithEmail == null){
                    continue;
                }

                message.setFlag(Flags.Flag.DELETED, true);

                signedPublicKeyEntryList.add(new SignedPublicKeyEntry(signerEmail, signature, publicKeyWithEmail));
            }

            emailFolder.close(true);
            store.close();

            return signedPublicKeyEntryList;

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private String getText(Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
//            textIsHtml = p.isMimeType("text/html");
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }
}
