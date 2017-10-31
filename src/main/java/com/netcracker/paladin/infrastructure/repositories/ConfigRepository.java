package com.netcracker.paladin.infrastructure.repositories;

import com.netcracker.paladin.infrastructure.repositories.exceptions.NoSavedConfigPropertiesException;

import java.util.Properties;

/**
 * Created on 30.10.14.
 */
public interface ConfigRepository {
    public Properties loadProperties() throws NoSavedConfigPropertiesException;

    public void saveProperties(Properties properties);
}
