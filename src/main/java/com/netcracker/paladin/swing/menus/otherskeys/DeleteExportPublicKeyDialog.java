package com.netcracker.paladin.swing.menus.otherskeys;

import com.netcracker.paladin.infrastructure.services.encryption.EncryptionService;
import com.netcracker.paladin.swing.SwingPaladinEmail;
import com.netcracker.paladin.swing.exceptions.NoFileSelectedException;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class DeleteExportPublicKeyDialog extends JDialog {
    private JFrame parent;

    private EncryptionService encryptionService;

    private DefaultComboBoxModel<String> comboBoxModelEmails;
    private DefaultButtonModel buttonModelSend;

    private JLabel labelEmail = new JLabel("Email: ");
    private JComboBox comboBoxDelete;

    private JButton buttonDelete = new JButton("Delete");
    private JButton buttonExport = new JButton("Export");

    public DeleteExportPublicKeyDialog(JFrame parent, EncryptionService encryptionService) {
        super(parent, "Delete/export public keys", true);
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

        constraints.gridx = 0;
        constraints.gridy = 1;
        comboBoxDelete = new JComboBox(comboBoxModelEmails);
        add(comboBoxDelete);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.EAST;
        add(buttonDelete, constraints);
        buttonDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonDeleteActionPerformed(event);
            }
        });

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        add(buttonExport, constraints);
        buttonExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonExportActionPerformed(event);
            }
        });
    }

    private void buttonDeleteActionPerformed(ActionEvent event) {
        try {
            String email = (String) comboBoxDelete.getSelectedItem();
            encryptionService.deletePublicKey(email);

            comboBoxModelEmails.removeElement(email);
            if(comboBoxModelEmails.getSize() == 0){
                comboBoxModelEmails.addElement(((SwingPaladinEmail) parent).getPlaceholderEmail());
                buttonModelSend.setEnabled(false);
                buttonDelete.setEnabled(false);
            }

            JOptionPane.showMessageDialog(DeleteExportPublicKeyDialog.this,
                    "Public key was deleted successfully!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error while deleting new public key: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buttonExportActionPerformed(ActionEvent event) {
        try {
            String email = (String) comboBoxDelete.getSelectedItem();
            byte[] publicKey = encryptionService.getPublicKey(email);
            File selectedFile = showSaveFileDialog(email);
            FileUtils.writeByteArrayToFile(selectedFile, publicKey);

            JOptionPane.showMessageDialog(this,
                    "Public key was exported successfully!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error while deleting new public key: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private File showSaveFileDialog(String email) throws NoFileSelectedException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(email+"_publicKey"));
        fileChooser.setDialogTitle("Specify a file to save public key");

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }else{
            throw new NoFileSelectedException();
        }
    }
}
