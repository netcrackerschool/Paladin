package com.netcracker.paladin.swing.dialogs;

import com.netcracker.paladin.infrastructure.services.encryption.EncryptionService;
import com.netcracker.paladin.swing.menus.ownkeys.AddPrivateKeyDialog;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created on 30.10.14.
 */
public class StartDialog extends AddPrivateKeyDialog {
    public StartDialog(JFrame parent, EncryptionService encryptionService) {
        super(parent, encryptionService);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
