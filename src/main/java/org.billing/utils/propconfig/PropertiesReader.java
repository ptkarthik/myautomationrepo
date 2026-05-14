package org.billing.utils.propconfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {

    private static Properties properties;

    /**
     * Load the config.properties file
     */
    static {
        try {
            properties = new Properties();
            // Ensure path points to src/main/resources/config.properties
            FileInputStream fis = new FileInputStream("config.properties");
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            // Print error and terminate if file loading fails
            System.err.println("Failed to load config.properties file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the value of a property by its key.
     *
     * @param key The property key
     * @return The value associated with the key
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}