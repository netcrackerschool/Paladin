package com.netcracker.paladin.swing.menus.otherskeys;

import com.netcracker.paladin.domain.SignedPublicKeyEntry;
import com.netcracker.paladin.infrastructure.services.config.ConfigService;
import com.netcracker.paladin.infrastructure.services.email.EmailService;
import com.netcracker.paladin.infrastructure.services.encryption.EncryptionService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created on 29.10.14.
 */
public class OthersKeysMenu extends JMenu {
    private final ConfigService configService;
    private final EmailService emailService;
    private final EncryptionService encryptionService;

    private final JFrame frame;

    private final JMenuItem menuItemAddPublicKey = new JMenuItem("Add public key..");
    private final JMenuItem menuItemDeleteExportPublicKey = new JMenuItem("Delete/export public key..");
    private final JMenuItem menuItemSharePublicKeys = new JMenuItem("Share public keys..");
    private final JMenuItem menuItemCheckPublicKeys = new JMenuItem("Check public keys..");

    public OthersKeysMenu(final JFrame frame, final ConfigService configService, final EmailService emailService, final EncryptionService encryptionService) {
        super("Others keys");
        this.configService = configService;
        this.emailService = emailService;
        this.encryptionService = encryptionService;

        this.frame = frame;

        menuItemAddPublicKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                AddPublicKeyDialog dialog = new AddPublicKeyDialog(frame, encryptionService);
                dialog.setVisible(true);
            }
        });
        add(menuItemAddPublicKey);

        menuItemDeleteExportPublicKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                DeleteExportPublicKeyDialog dialog = new DeleteExportPublicKeyDialog(frame, encryptionService);
                dialog.setVisible(true);
            }
        });
        add(menuItemDeleteExportPublicKey);

        menuItemSharePublicKeys.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                SharePublicKeysDialog dialog = new SharePublicKeysDialog(frame, emailService, encryptionService);
                dialog.setVisible(true);
            }
        });
        add(menuItemSharePublicKeys);

        menuItemCheckPublicKeys.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                List<SignedPublicKeyEntry> signedPublicKeyEntryList = emailService.readSignedPublicKeys();
                if(signedPublicKeyEntryList.size() == 0){
                    JOptionPane.showMessageDialog(frame,
                            "No new public keys found!");
                }
                for(SignedPublicKeyEntry signedPublicKeyEntry : signedPublicKeyEntryList) {
                    AcceptPublicKeyDialog dialog = new AcceptPublicKeyDialog(frame, encryptionService, signedPublicKeyEntry);
                    dialog.setVisible(true);
                }
            }
        });
        add(menuItemCheckPublicKeys);
    }
}
