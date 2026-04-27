package org.billing.dbconfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BrokerPageDBConfig extends DBConfig {
    private static final Logger logger = LogManager.getLogger(BrokerPageDBConfig.class);

    public void getAllDataOfBrokerQuery(String brokerName) {
        // SQL Query to execute
        String query = "SELECT * FROM public.broker WHERE name = ?";

        // Try-With-Resources ensures connections are closed properly
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            logger.debug("Database connection established successfully.");

            // Set the broker name parameter in the query
            stmt.setString(1, brokerName);
            logger.debug("Prepared statement created with broker name: {}", brokerName);

            // Execute the query
            try (ResultSet rs = stmt.executeQuery()) {
                // Check if the broker exists in the database
                if (rs.next()) {
                    String foundBrokerName = rs.getString("name");
                    logger.info("Broker '{}' found in the database.", foundBrokerName);
                } else {
                    logger.warn("Broker '{}' not found in the database.", brokerName);
                }
            }

        } catch (Exception e) {
            logger.error("Error occurred while connecting to the Billing Database or executing the query.", e);
        }
    }
}
