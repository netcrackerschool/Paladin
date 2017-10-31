package com.netcracker.paladin.swing.tabs;

import com.netcracker.paladin.domain.MessageEntry;
import com.netcracker.paladin.infrastructure.repositories.exceptions.NoPublicKeyForEmailException;
import com.netcracker.paladin.infrastructure.services.email.EmailService;
import com.netcracker.paladin.infrastructure.services.encryption.EncryptionService;
import com.netcracker.paladin.infrastructure.services.encryption.asymmetric.exceptions.NoCorrectPrivateKeyException;
import com.netcracker.paladin.infrastructure.services.encryption.exceptions.NoPrivateKeyException;
import com.netcracker.paladin.swing.exceptions.NoMessagesException;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 29.10.14.
 */
public class TabRead extends JPanel {
    private final EmailService emailService;
    private final EncryptionService encryptionService;

    private final List<MessageEntry> loadedEmailEntries;
    private int currentIndex;

    private static final int MIN_DATE_LENGTH = 40;

    private final JLabel labelFrom = new JLabel("From: ");
    private final JLabel labelSubject = new JLabel("Subject: ");
    private final JLabel labelDate = new JLabel("Date: ");
    private final JLabel labelSignature = new JLabel("Signature: ");
    private final JLabel labelNumber = new JLabel("Number: ");

    private final JLabel labelValueFrom = new JLabel();
    private final JLabel labelValueSubject = new JLabel();
    private final JLabel labelValueDate = new JLabel();
    private final JLabel labelValueSignature = new JLabel();
    private final JLabel labelValueNumber = new JLabel();

    private final JButton buttonNewer = new JButton("NEWER");
    private final JButton buttonOlder = new JButton("OLDER");
    private final JButton buttonReset = new JButton("RESET");
    private final DefaultButtonModel buttonModelNewer = new DefaultButtonModel();
    private final DefaultButtonModel buttonModelOlder = new DefaultButtonModel();

    private final JTextArea textAreaMessage = new JTextArea(10, 30);

    private final GridBagConstraints constraints = new GridBagConstraints();

    public TabRead(EmailService emailService, EncryptionService encryptionService) {
        this.emailService = emailService;
        this.encryptionService = encryptionService;

        setLayout(new GridBagLayout());
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(labelFrom, constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(labelValueFrom, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        add(labelSubject, constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(labelValueSubject, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        add(labelDate, constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        labelValueDate.setText(StringUtils.rightPad("", MIN_DATE_LENGTH));
        add(labelValueDate, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        add(labelSignature, constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(labelValueSignature, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        add(labelNumber, constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(labelValueNumber, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        constraints.fill = GridBagConstraints.BOTH;
        buttonNewer.setModel(buttonModelNewer);
        buttonNewer.setFont(new Font("Arial", Font.BOLD, 16));
        add(buttonNewer, constraints);
        buttonNewer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonNewerActionPerformed(event);
            }
        });

        constraints.gridx = 2;
        constraints.gridy = 2;
        constraints.gridheight = 2;
        constraints.fill = GridBagConstraints.BOTH;
        buttonOlder.setModel(buttonModelOlder);
        buttonOlder.setFont(new Font("Arial", Font.BOLD, 16));
        buttonOlder.setEnabled(false);
        add(buttonOlder, constraints);
        buttonOlder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonOlderActionPerformed(event);
            }
        });

        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.gridheight = 1;
        constraints.fill = GridBagConstraints.BOTH;
        buttonReset.setFont(new Font("Arial", Font.BOLD, 16));
        add(buttonReset, constraints);
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonResetActionPerformed(event);
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridheight = 1;
        constraints.gridwidth = 3;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        textAreaMessage.setEditable(false);
        add(new JScrollPane(textAreaMessage), constraints);

        loadedEmailEntries = new ArrayList<>();
        currentIndex = -1;
    }

    private void buttonNewerActionPerformed(ActionEvent event) {
        try {
            if(currentIndex >= loadedEmailEntries.size()-1){
                List<MessageEntry> newLoadedEmailEntries = emailService.readEmails();

                for(MessageEntry entry : newLoadedEmailEntries){
                    if(loadedEmailEntries.contains(entry) == false){
                        decryptEntry(entry);
                        loadedEmailEntries.add(entry);
                    }
                }

                if(currentIndex < loadedEmailEntries.size()-1){
                    currentIndex++;
                    displayEntry(loadedEmailEntries.get(currentIndex));
                }else{
                    JOptionPane.showMessageDialog(this,
                            "No new emails :(",
                            "No new emails", JOptionPane.INFORMATION_MESSAGE);
                }
            }else{
                currentIndex++;
                displayEntry(loadedEmailEntries.get(currentIndex));
            }

            if(currentIndex > 0){
                buttonModelOlder.setEnabled(true);
            }
//            else if(currentIndex == loadedEmailEntries.size()-1){
//                buttonNewer.setBackground(new Color(255, 0, 0));
//            }
        } catch (NoMessagesException nme){
            JOptionPane.showMessageDialog(this,
                    "Mailbox is empty :(",
                    "No mail", JOptionPane.ERROR_MESSAGE);
        } catch (NoPrivateKeyException npke){
            JOptionPane.showMessageDialog(this,
                    "Please, set your private key first",
                    "No private key", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error while reading e-mails: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buttonOlderActionPerformed(ActionEvent event) {
        try {
            if(currentIndex != 0) {
                currentIndex--;
                displayEntry(loadedEmailEntries.get(currentIndex));
            }
            if(currentIndex == 0){
                buttonModelOlder.setEnabled(false);
            }
        } catch (NoMessagesException nme){
            JOptionPane.showMessageDialog(this,
                    "Mailbox is empty :(",
                    "No mail", JOptionPane.ERROR_MESSAGE);
        } catch (NoPrivateKeyException npke){
            JOptionPane.showMessageDialog(this,
                    "Please, set your private key first",
                    "No private key", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error while reading e-mails: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buttonResetActionPerformed(ActionEvent event) {
        try {
            List<MessageEntry> newLoadedEmailEntries = emailService.readEmails();

            loadedEmailEntries.clear();

            for(MessageEntry entry : newLoadedEmailEntries){
                decryptEntry(entry);
                loadedEmailEntries.add(entry);
            }

            currentIndex = 0;
        } catch (NoMessagesException nme){
            JOptionPane.showMessageDialog(this,
                    "Mailbox is empty :(",
                    "No mail", JOptionPane.ERROR_MESSAGE);
        } catch (NoPrivateKeyException npke){
            JOptionPane.showMessageDialog(this,
                    "Please, set your private key first",
                    "No private key", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error while reading e-mails: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void decryptEntry(MessageEntry entry){
        if(entry.getCipherBlob() != null){
            try {
                entry.setDisplayedMessage(encryptionService.decryptEmail(entry.getCipherBlob()));
            } catch (NoCorrectPrivateKeyException e){
                entry.setDisplayedMessage("No correct private key for this message was found");
            } catch (Exception e){
                entry.setDisplayedMessage("Unable to decrypt");
            }
            if(entry.getSignature() == null){
                entry.setSignatureStatus("no signature");
            }else{
                try {
                    if (encryptionService.verifySignature(
                            encryptionService.getPublicKey(entry.getFrom()),
                            entry.getSignature(),
                            entry.getCipherBlob())) {
                        entry.setSignatureStatus("correct");
                    } else {
                        entry.setSignatureStatus("incorrect");
                    }
                } catch (NoPublicKeyForEmailException e){
                    entry.setSignatureStatus("unknown sender");
                }
            }
        }else{
            entry.setDisplayedMessage(entry.getPlainMessage());
            entry.setSignatureStatus("not encrypted");
        }

        if(entry.getDisplayedMessage() == null){
            throw new Error("Improper email entry decryption!");
        }
    }

    private void displayEntry(MessageEntry entry){
        labelValueFrom.setText(entry.getFrom());
        labelValueSubject.setText(entry.getSubject());
        labelValueDate.setText(StringUtils.rightPad(entry.getSentDate().toString(), MIN_DATE_LENGTH));
        labelValueSignature.setText(entry.getSignatureStatus());
        labelValueNumber.setText((currentIndex+1)+" out of "+loadedEmailEntries.size());
        textAreaMessage.setText(entry.getDisplayedMessage());
        this.validate();
        this.repaint();
    }
}
