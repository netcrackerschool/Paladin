package com.netcracker.paladin.swing.menus.ownkeys;

import com.netcracker.paladin.infrastructure.services.config.ConfigService;
import com.netcracker.paladin.infrastructure.services.email.EmailService;
import com.netcracker.paladin.infrastructure.services.encryption.EncryptionService;
import com.netcracker.paladin.infrastructure.services.encryption.exceptions.NoPrivateKeyException;
import com.netcracker.paladin.swing.SwingPaladinEmail;
import com.netcracker.paladin.swing.exceptions.NoFileSelectedException;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created on 29.10.14.
 */
public class OwnKeysMenu extends JMenu {
    private final ConfigService configService;
    private final EmailService emailService;
    private final EncryptionService encryptionService;

    private final JFrame frame;

    private JFileChooser fileChooser = new JFileChooser();

    private DefaultComboBoxModel<String> comboBoxModelEmails;
    private DefaultButtonModel buttonModelSend;

    private final JMenuItem menuItemAddPrivateKey = new JMenuItem("Add private key..");
    private final JMenuItem menuItemSetMainPrivateKey = new JMenuItem("Set main private key..");
    private final JMenuItem menuItemExportPublicKey = new JMenuItem("Export public key..");
    private final JMenuItem menuItemAddOwnPublicKeyToDatabase = new JMenuItem("Add to database..");

    public OwnKeysMenu(final JFrame frame, final ConfigService configService, final EmailService emailService, final EncryptionService encryptionService) {
        super("Own keys");
        this.configService = configService;
        this.emailService = emailService;
        this.encryptionService = encryptionService;

        this.frame = frame;

        this.comboBoxModelEmails = ((SwingPaladinEmail) this.frame).getComboBoxModelEmails();
        this.buttonModelSend = ((SwingPaladinEmail) this.frame).getButtonModelSend();

        menuItemAddPrivateKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                AddPrivateKeyDialog dialog = new AddPrivateKeyDialog(frame, encryptionService);
                dialog.setVisible(true);
            }
        });
        add(menuItemAddPrivateKey);

        menuItemSetMainPrivateKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        File privateKeyFile = fileChooser.getSelectedFile();
                        byte[] privateKey = FileUtils.readFileToByteArray(privateKeyFile);
                        encryptionService.addPrivateKey(privateKey);

                        JOptionPane.showMessageDialog(frame, "Main private key was set successfully!");
                    }else{
                        return;
                    }
                } catch (NoPrivateKeyException npke) {
                    JOptionPane.showMessageDialog(frame,
                            "Please, set your private key first",
                            "No private key", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame,
                            "Error saving properties file: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(menuItemSetMainPrivateKey);

        menuItemExportPublicKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    File selectedFile = showSaveFileDialog();

                    FileUtils.writeByteArrayToFile(selectedFile, encryptionService.getOwnPublicKey());

                    JOptionPane.showMessageDialog(
                            frame,
                            "Public key was exported successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (NoPrivateKeyException npke) {
                    JOptionPane.showMessageDialog(frame,
                            "Please, set your private key first",
                            "No private key", JOptionPane.ERROR_MESSAGE);
                } catch (NoFileSelectedException nfse){
                    JOptionPane.showMessageDialog(frame,
                            "No file was selected. Key was not generated.",
                            "No file selected", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame,
                            "Error saving properties file: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(menuItemExportPublicKey);

        menuItemAddOwnPublicKeyToDatabase.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    String ownEmail = configService.loadProperties().getProperty("mail.user");

                    encryptionService.addPublicKey(ownEmail, encryptionService.getOwnPublicKey());

                    if(comboBoxModelEmails.getSize() == 1){
                        comboBoxModelEmails.removeElement(((SwingPaladinEmail) frame).getPlaceholderEmail());
                        buttonModelSend.setEnabled(true);
                    }
                    comboBoxModelEmails.addElement(ownEmail);

                    JOptionPane.showMessageDialog(
                            frame,
                            "Public key was added to database successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (NoPrivateKeyException npke) {
                    JOptionPane.showMessageDialog(frame,
                            "Please, set your private key first",
                            "No private key", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame,
                            "Error saving properties file: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(menuItemAddOwnPublicKeyToDatabase);
    }

    private File showSaveFileDialog() throws NoFileSelectedException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(configService.loadProperties().getProperty("mail.user")+"_publicKey"));
        fileChooser.setDialogTitle("Specify a file to save public key");

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }else{
            throw new NoFileSelectedException();
        }
    }
}
