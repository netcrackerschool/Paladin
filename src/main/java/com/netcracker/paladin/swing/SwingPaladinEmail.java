package com.netcracker.paladin.swing;

/**
 * Created on 26.10.14.
 */

import com.netcracker.paladin.infrastructure.services.config.ConfigService;
import com.netcracker.paladin.infrastructure.services.email.EmailService;
import com.netcracker.paladin.infrastructure.services.encryption.EncryptionService;
import com.netcracker.paladin.swing.dialogs.StartDialog;
import com.netcracker.paladin.swing.menus.file.FileMenu;
import com.netcracker.paladin.swing.menus.otherskeys.OthersKeysMenu;
import com.netcracker.paladin.swing.menus.ownkeys.OwnKeysMenu;
import com.netcracker.paladin.swing.tabs.TabRead;
import com.netcracker.paladin.swing.tabs.TabSend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class SwingPaladinEmail extends JFrame {
    private final ConfigService configService;
    private final EmailService emailService;
    private final EncryptionService encryptionService;

    private final DefaultComboBoxModel<String> comboBoxModelEmails;
    private final String PLACEHOLDER_EMAIL = "No public keys";
    private final DefaultButtonModel buttonModelSend;

    private final static String TABSEND = "Send an email";
    private final static String TABREAD = "Read emails";

    private final TabSend tabSend;
    private final TabRead tabRead;

    public SwingPaladinEmail(ConfigService configService, EmailService emailService, EncryptionService encryptionService) {
        super("Paladin Email");
        this.configService = configService;
        this.emailService = emailService;
        this.encryptionService = encryptionService;

        comboBoxModelEmails = new DefaultComboBoxModel(this.encryptionService.getAllEmailsWithPublicKey().toArray());
        buttonModelSend = new DefaultButtonModel();
        if(comboBoxModelEmails.getSize() == 0){
            comboBoxModelEmails.addElement(getPlaceholderEmail());
            buttonModelSend.setEnabled(false);
        }else{
            buttonModelSend.setEnabled(true);
        }

        tabSend = new TabSend(this, emailService, encryptionService);
        tabRead = new TabRead(emailService, encryptionService);
    }

    public void addComponentToPane(Container pane) {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(TABSEND, tabSend);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        tabbedPane.addTab(TABREAD, tabRead);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        pane.add(tabbedPane);
    }

    private void createAndShowGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.addComponentToPane(this.getContentPane());

        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.add(new FileMenu(this, configService, emailService, encryptionService));
        jMenuBar.add(new OwnKeysMenu(this, configService, emailService, encryptionService));
        jMenuBar.add(new OthersKeysMenu(this, configService, emailService, encryptionService));
        this.setJMenuBar(jMenuBar);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        StartDialog startDialog = new StartDialog(this, encryptionService);
        startDialog.setVisible(true);
    }

    public void launch() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public DefaultComboBoxModel<String> getComboBoxModelEmails() {
        return comboBoxModelEmails;
    }

    public String getPlaceholderEmail() {
        return PLACEHOLDER_EMAIL;
    }

    public DefaultButtonModel getButtonModelSend() {
        return buttonModelSend;
    }

//    public void AddEmailToModel(String email){
//        if(comboBoxModelEmails.getSize() == 1){
//            comboBoxModelEmails.removeElement(getPlaceholderEmail());
//            buttonModelSend.setEnabled(true);
//        }
//    }
}
