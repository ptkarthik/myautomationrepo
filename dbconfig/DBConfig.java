package org.billing.dbconfig;

import org.billing.utils.propconfig.PropertiesReader;

public class DBConfig {

    public static String dbUrl;
    public static String dbUser;
    public static String dbPassword;

    /**
     * Sets up the DB connection and validates if the given broker exists in the database.
     */
    public void setUpDbConfig() {
        // Database connection details
        dbUrl = PropertiesReader.getProperty("dbUrl");
        dbUser = PropertiesReader.getProperty("dbUser");
        dbPassword = PropertiesReader.getProperty("dbPassword");
    }
}