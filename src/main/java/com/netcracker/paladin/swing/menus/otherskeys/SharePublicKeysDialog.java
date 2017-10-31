package com.netcracker.paladin.swing.menus.otherskeys;

import com.netcracker.paladin.domain.PublicKeyEntry;
import com.netcracker.paladin.domain.SignedPublicKeyEntry;
import com.netcracker.paladin.infrastructure.services.email.EmailService;
import com.netcracker.paladin.infrastructure.services.encryption.EncryptionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SharePublicKeysDialog extends JDialog {
    private final JFrame parent;

    private final EmailService emailService;
    private final EncryptionService encryptionService;

    private final List<PublicKeyEntry> allPublicKeyEntries;

    private JList listEmails;
    private ListSelectionModel listSelectionModelEmails;

    private final JLabel labelTo = new JLabel("Send to: ");
    private final JTextField textTo = new JTextField(20);

    private final JButton buttonShare = new JButton("Share");

    public SharePublicKeysDialog(JFrame parent, EmailService emailService, EncryptionService encryptionService) {
        super(parent, "Adding new public key", true);
        this.parent = parent;
        this.emailService = emailService;
        this.encryptionService = encryptionService;
        this.allPublicKeyEntries = encryptionService.getAllPublicKeyEntries();

        listEmails = new JList(getAllEmailsWithPublicKey(allPublicKeyEntries).toArray());
        listSelectionModelEmails = listEmails.getSelectionModel();

        setupForm();

        pack();
        setLocationRelativeTo(parent);
    }

    private void setupForm() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 5, 10);
        constraints.anchor = GridBagConstraints.WEST;

        JScrollPane listPane = new JScrollPane(listEmails);
        JPanel listContainer = new JPanel(new GridLayout(1,1));
        listContainer.setBorder(BorderFactory.createTitledBorder("List"));
        listContainer.add(listPane);
        add(listContainer);

        constraints.gridx = 0;
        constraints.gridy = 1;
        add(labelTo, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        add(textTo, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.CENTER;
        add(buttonShare, constraints);

        buttonShare.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonShareActionPerformed(event);
            }
        });
    }

    private void buttonShareActionPerformed(ActionEvent event) {
        try {
            if (!validateFields()) {
                return;
            }

            String toAdress = textTo.getText();

            int minIndex = listSelectionModelEmails.getMinSelectionIndex();
            int maxIndex = listSelectionModelEmails.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (listSelectionModelEmails.isSelectedIndex(i)) {
                    SignedPublicKeyEntry signedPublicKeyEntry = encryptionService.getSignedPublicKeyEntry(allPublicKeyEntries.get(i));
                    emailService.sendSignedPublicKey(toAdress, signedPublicKeyEntry);
                }
            }

            JOptionPane.showMessageDialog(SharePublicKeysDialog.this,
                    "Keys were shared successfully!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error while sharing public keys: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateFields() {
        if (textTo.getText().equals("")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter To address!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            textTo.requestFocus();
            return false;
        }

        return true;
    }

    private List<String> getAllEmailsWithPublicKey(List<PublicKeyEntry> allPublicKeyEntries){
        List<String> allEmailsWithPublicKey = new ArrayList<>(allPublicKeyEntries.size());
        for(PublicKeyEntry publicKeyEntry : allPublicKeyEntries){
            allEmailsWithPublicKey.add(publicKeyEntry.getEmail());
        }
        return allEmailsWithPublicKey;
    }
}
