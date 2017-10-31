package com.netcracker.paladin.infrastructure.services.config;

import com.netcracker.paladin.infrastructure.repositories.ConfigRepository;
import com.netcracker.paladin.infrastructure.repositories.exceptions.NoSavedConfigPropertiesException;

import java.util.Properties;

public class ConfigService {
    private final ConfigRepository configRepository;
    private Properties configProperties;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;

        try {
            configProperties = configRepository.loadProperties();
        } catch (NoSavedConfigPropertiesException e){
            Properties defaultProperties = new Properties();
            defaultProperties.setProperty("mail.smtp.host", "smtp.gmail.com");
            defaultProperties.setProperty("mail.smtp.port", "587");
            defaultProperties.setProperty("mail.user", "plzdontgay@gmail.com");
            defaultProperties.setProperty("mail.password", "plzdontgay1");
            defaultProperties.setProperty("mail.smtp.starttls.enable", "true");
            defaultProperties.setProperty("mail.smtp.auth", "true");

            configProperties = new Properties(defaultProperties);

            configRepository.saveProperties(configProperties);
        }
    }

    public Properties loadProperties() {
        return configProperties;
    }

    public void saveProperties(String host, String port, String user, String pass) {
        configProperties.setProperty("mail.smtp.host", host);
        configProperties.setProperty("mail.smtp.port", port);
        configProperties.setProperty("mail.user", user);
        configProperties.setProperty("mail.password", pass);
        configProperties.setProperty("mail.smtp.starttls.enable", "true");
        configProperties.setProperty("mail.smtp.auth", "true");

        configRepository.saveProperties(configProperties);
    }
}
