package com.netcracker.paladin.swing.menus.otherskeys;

import com.netcracker.paladin.domain.PublicKeyEntry;
import com.netcracker.paladin.domain.SignedPublicKeyEntry;
import com.netcracker.paladin.infrastructure.repositories.exceptions.NoPublicKeyForEmailException;
import com.netcracker.paladin.infrastructure.services.encryption.EncryptionService;
import com.netcracker.paladin.swing.SwingPaladinEmail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AcceptPublicKeyDialog extends JDialog {

    private final JFrame parent;

    private final EncryptionService encryptionService;

    private DefaultComboBoxModel<String> comboBoxModelEmails;
    private DefaultButtonModel buttonModelSend;

    private final SignedPublicKeyEntry signedPublicKeyEntry;
    private final PublicKeyEntry publicKeyEntry;
    
    private final JLabel labelMessage = new JLabel("Public key found in email:");
    private final JLabel labelFrom = new JLabel("From: ");
    private final JLabel labelOf = new JLabel("Of: ");
    private final JLabel labelSignature = new JLabel("Signature: ");

    private final JLabel labelValueFrom = new JLabel("No sender");
    private final JLabel labelValueOf = new JLabel("No owner");
    private final JLabel labelValueSignature = new JLabel("No signature status");

    private final JButton buttonAccept = new JButton("Accept");
    private final JButton buttonDecline = new JButton("Decline");

    private final JFileChooser fileChooser = new JFileChooser();

    public AcceptPublicKeyDialog(JFrame parent, EncryptionService encryptionService, SignedPublicKeyEntry signedPublicKeyEntry) {
        super(parent, "Public key found", true);
        this.parent = parent;
        this.encryptionService = encryptionService;

        this.comboBoxModelEmails = ((SwingPaladinEmail) parent).getComboBoxModelEmails();
        this.buttonModelSend = ((SwingPaladinEmail) this.parent).getButtonModelSend();

        this.signedPublicKeyEntry = signedPublicKeyEntry;
        this.publicKeyEntry = encryptionService.getPublicKeyEntry(signedPublicKeyEntry);

        labelValueFrom.setText(signedPublicKeyEntry.getSignerEmail());
        labelValueOf.setText(publicKeyEntry.getEmail());

        try {
            if (encryptionService.verifySignature(signedPublicKeyEntry)) {
                labelValueSignature.setText("correct");
            } else {
                labelValueSignature.setText("incorrect");
            }
        } catch (NoPublicKeyForEmailException e){
            labelValueSignature.setText("unknown sender");
        }

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

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        add(labelMessage, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.EAST;
        add(labelFrom, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        add(labelValueFrom, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.EAST;
        add(labelOf, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        add(labelValueOf, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.EAST;
        add(labelSignature, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        add(labelValueSignature, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.EAST;
        add(buttonAccept, constraints);
        buttonAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonAcceptActionPerformed(event);
            }
        });

        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        add(buttonDecline, constraints);
        buttonDecline.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonDeclineActionPerformed(event);
            }
        });
    }

    private void buttonAcceptActionPerformed(ActionEvent event) {
        try {
            encryptionService.addPublicKey(publicKeyEntry);

            comboBoxModelEmails.addElement(publicKeyEntry.getEmail());
            if(comboBoxModelEmails.getSize() == 1){
                comboBoxModelEmails.removeElement(((SwingPaladinEmail) parent).getPlaceholderEmail());
                buttonModelSend.setEnabled(true);
            }

            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading private key: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buttonDeclineActionPerformed(ActionEvent event) {
        try {
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving properties file: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

