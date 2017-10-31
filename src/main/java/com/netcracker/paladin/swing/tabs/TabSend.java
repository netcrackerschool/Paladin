package com.netcracker.paladin.swing.tabs;

import com.netcracker.paladin.infrastructure.repositories.exceptions.NoPublicKeyForEmailException;
import com.netcracker.paladin.infrastructure.services.email.EmailService;
import com.netcracker.paladin.infrastructure.services.encryption.EncryptionService;
import com.netcracker.paladin.infrastructure.services.encryption.exceptions.NoPrivateKeyException;
import com.netcracker.paladin.swing.SwingPaladinEmail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created on 29.10.14.
 */
public class TabSend extends JPanel {
    private final JFrame parent;

    private final EmailService emailService;
    private final EncryptionService encryptionService;

//    private final JTextField fieldTo = new JTextField(30);

    private final JLabel labelTo = new JLabel("To: ");
    private final JLabel labelSubject = new JLabel("Subject: ");

    private final JComboBox comboBoxTo;
    private final JButton buttonSend = new JButton("SEND");
    private final DefaultComboBoxModel comboBoxModelEmails;
    private final DefaultButtonModel buttonModelSend;

    private final JTextField fieldSubject = new JTextField(30);
    private final JTextArea textAreaMessage = new JTextArea(10, 30);

    private final GridBagConstraints constraints = new GridBagConstraints();

    public TabSend(JFrame parent, EmailService emailService, EncryptionService encryptionService) {
        this.parent = parent;

        this.emailService = emailService;
        this.encryptionService = encryptionService;

        this.comboBoxModelEmails = ((SwingPaladinEmail) this.parent).getComboBoxModelEmails();
        this.buttonModelSend = ((SwingPaladinEmail) this.parent).getButtonModelSend();

        setLayout(new GridBagLayout());
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(labelTo, constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
//        add(fieldTo, constraints);
        comboBoxTo = new JComboBox();
        comboBoxTo.setModel(comboBoxModelEmails);
        add(comboBoxTo, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        add(labelSubject, constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(fieldSubject, constraints);

        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        constraints.fill = GridBagConstraints.BOTH;
        buttonSend.setModel(buttonModelSend);
        buttonSend.setFont(new Font("Arial", Font.BOLD, 16));
        add(buttonSend, constraints);
        buttonSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonSendActionPerformed(event);
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridheight = 1;
        constraints.gridwidth = 3;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        add(new JScrollPane(textAreaMessage), constraints);
    }

    private void buttonSendActionPerformed(ActionEvent event) {
        if (!validateFields()) {
            return;
        }

//        String toAddress = fieldTo.getText();
        String toAddress = (String) comboBoxTo.getSelectedItem();
        String subject = fieldSubject.getText();
        String message = textAreaMessage.getText();

//        File[] attachFiles = null;
//        if (!filePicker.getSelectedFilePath().equals("")) {
//            File selectedFile = new File(filePicker.getSelectedFilePath());
//            attachFiles = new File[] {selectedFile};
//        }

        try {
            System.out.println("Sending...");
            byte[] cipherBlob = encryptionService.encryptEmail(message, toAddress);
            emailService.sendEmail(toAddress,
                    subject,
                    "Message is encrypted",
                    cipherBlob,
                    encryptionService.getSignature(cipherBlob));

            JOptionPane.showMessageDialog(this,
                    "The e-mail has been sent successfully!");
        } catch (NoPrivateKeyException npke){
            JOptionPane.showMessageDialog(this,
                    "Please, set your private key first",
                    "No private key", JOptionPane.ERROR_MESSAGE);
        } catch (NoPublicKeyForEmailException npkfee){
            JOptionPane.showMessageDialog(this,
                    "No public key for this email found",
                    "No public key", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error while sending the e-mail: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateFields() {
//        if (fieldTo.getText().equals("")) {
//            JOptionPane.showMessageDialog(this,
//                    "Please enter To address!",
//                    "Error", JOptionPane.ERROR_MESSAGE);
//            fieldTo.requestFocus();
//            return false;
//        }

        if (fieldSubject.getText().equals("")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter subject!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            fieldSubject.requestFocus();
            return false;
        }

        if (textAreaMessage.getText().equals("")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter message!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            textAreaMessage.requestFocus();
            return false;
        }

        return true;
    }
}
