package com.netcracker.paladin.swing.menus.otherskeys;

import com.netcracker.paladin.infrastructure.services.encryption.EncryptionService;
import com.netcracker.paladin.swing.SwingPaladinEmail;
import com.netcracker.paladin.swing.auxillary.FilePicker;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class AddPublicKeyDialog extends JDialog {
    private JFrame parent;

    private EncryptionService encryptionService;

    private DefaultComboBoxModel<String> comboBoxModelEmails;
    private DefaultButtonModel buttonModelSend;

    private JLabel labelEmail = new JLabel("Email: ");
    private JTextField textEmail = new JTextField(20);

    private FilePicker filePicker = new FilePicker("Public key file", "Select");

    private JButton buttonAdd = new JButton("Add");

    public AddPublicKeyDialog(JFrame parent, EncryptionService encryptionService) {
        super(parent, "Adding new public key", true);
        this.parent = parent;
        this.encryptionService = encryptionService;

        this.comboBoxModelEmails = ((SwingPaladinEmail) parent).getComboBoxModelEmails();
        this.buttonModelSend = ((SwingPaladinEmail) this.parent).getButtonModelSend();

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

        add(labelEmail, constraints);

        constraints.gridx = 1;
        add(textEmail, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridheight = 1;
        constraints.gridwidth = 3;
        filePicker.setMode(FilePicker.MODE_OPEN);
        add(filePicker, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        add(buttonAdd, constraints);

        buttonAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonAddActionPerformed(event);
            }
        });
    }

    private void buttonAddActionPerformed(ActionEvent event) {
        try {
            if (!validateFields()) {
                return;
            }

            String publicKeyFilePath = filePicker.getSelectedFilePath();
            byte[] publicKey = FileUtils.readFileToByteArray(new File(publicKeyFilePath));

            String email = textEmail.getText();

            encryptionService.addPublicKey(email, publicKey);

            if(comboBoxModelEmails.getSize() == 1){
                comboBoxModelEmails.removeElement(((SwingPaladinEmail) parent).getPlaceholderEmail());
                buttonModelSend.setEnabled(true);
            }
            comboBoxModelEmails.addElement(email);

            JOptionPane.showMessageDialog(AddPublicKeyDialog.this,
                    "New public key was added successfully!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error while adding new public key: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateFields() {
        if (textEmail.getText().equals("")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter To address!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            textEmail.requestFocus();
            return false;
        }

        return true;
    }
}
