package com.netcracker.paladin.domain;

import java.util.Arrays;
import java.util.Date;

/**
 * Created on 26.10.14.
 */
public class MessageEntry implements Comparable {

    private String from;
    private String subject;
    private String displayedMessage;
    private Date sentDate;
    private String plainMessage;
    private byte[] cipherBlob;
    private byte[] signature;

    private String signatureStatus;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDisplayedMessage() {
        return displayedMessage;
    }

    public void setDisplayedMessage(String displayedMessage) {
        this.displayedMessage = displayedMessage;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getPlainMessage() {
        return plainMessage;
    }

    public void setPlainMessage(String plainMessage) {
        this.plainMessage = plainMessage;
    }

    public byte[] getCipherBlob() {
        return cipherBlob;
    }

    public void setCipherBlob(byte[] cipherBlob) {
        this.cipherBlob = cipherBlob;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getSignatureStatus() {
        return signatureStatus;
    }

    public void setSignatureStatus(String signatureStatus) {
        this.signatureStatus = signatureStatus;
    }

    @Override
    public int compareTo(Object o) {
        if(o == null){
            throw new NullPointerException();
        }
        if(o instanceof MessageEntry == false){
            throw new ClassCastException();
        }
        return(this.getSentDate().compareTo(((MessageEntry) o).getSentDate()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageEntry)) return false;

        MessageEntry that = (MessageEntry) o;

        if (!getFrom().equals(that.getFrom())) return false;
        if (getSubject() != null ? !getSubject().equals(that.getSubject()) : that.getSubject() != null) return false;
        if (!getSentDate().equals(that.getSentDate())) return false;
        if (getPlainMessage() != null ? !getPlainMessage().equals(that.getPlainMessage()) : that.getPlainMessage() != null)
            return false;
        return Arrays.equals(getCipherBlob(), that.getCipherBlob());

    }

    @Override
    public int hashCode() {
        int result = getFrom().hashCode();
        result = 31 * result + (getSubject() != null ? getSubject().hashCode() : 0);
        result = 31 * result + getSentDate().hashCode();
        result = 31 * result + (getPlainMessage() != null ? getPlainMessage().hashCode() : 0);
        result = 31 * result + Arrays.hashCode(getCipherBlob());
        return result;
    }
}
