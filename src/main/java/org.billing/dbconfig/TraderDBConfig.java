package org.billing.dbconfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.dbclasses.TraderDbData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TraderDBConfig extends DBConfig {
    private static final Logger logger = LogManager.getLogger(TraderDBConfig.class);

    // Fetch data from the database
    public List<TraderDbData> fetchDbData() throws SQLException {
        List<TraderDbData> ruleSets = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            logger.debug("the postgreSql has been successfully set");// Forces the driver to load
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("PostgreSQL driver not found in classpath!", e);
        }
        Connection connection = DriverManager.getConnection(DBConfig.dbUrl, DBConfig.dbUser, DBConfig.dbPassword);
        String query = "SELECT * FROM public.trader\n" +
                "ORDER BY id ASC";
        logger.debug(query);
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            TraderDbData ruleSet = new TraderDbData(
                    String.valueOf(resultSet.getString("id")),
                    resultSet.getString("name"),
                    resultSet.getBoolean("toraTrader")
            );
            ruleSets.add(ruleSet);
        }
        return ruleSets;
    }
}
