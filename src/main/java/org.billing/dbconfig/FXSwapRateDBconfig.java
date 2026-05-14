package org.billing.dbconfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.TestConstants;
import org.billing.dbclasses.FxSwapRateDBData;
import org.billing.dbclasses.FxswapValidationResult;

import java.sql.*;
import java.util.*;

public class FXSwapRateDBconfig extends DBConfig {
    private static final Logger logger = LogManager.getLogger(FXSwapRateDBconfig.class);
    FxswapValidationResult fxswapValidationResult = new FxswapValidationResult();

    // Fetch data from the database
    public List<FxSwapRateDBData> fetchDbData() throws SQLException {
        List<FxSwapRateDBData> ruleSets = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
            logger.debug("the postgreSql has been successfully set");// Forces the driver to load
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("PostgreSQL driver not found in classpath!", e);
        }
        Connection connection = DriverManager.getConnection(DBConfig.dbUrl, DBConfig.dbUser, DBConfig.dbPassword);
        String query = "SELECT id, lower_bound_in_days, upper_bound_in_days, tenor, " +
                "revenue_share, max_upper_bound FROM public.fx_swap_rate";
        logger.debug(query);
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            FxSwapRateDBData ruleSet = new FxSwapRateDBData(
                    String.valueOf(resultSet.getInt("id")),
                    Integer.valueOf(resultSet.getInt("lower_bound_in_days")),
                    Integer.valueOf(resultSet.getInt("upper_bound_in_days")),
                    resultSet.getString("tenor"),
                    resultSet.getDouble("revenue_share"),
                    resultSet.getBoolean("max_upper_bound")
            );
            ruleSets.add(ruleSet);
        }
        return ruleSets;
    }

    public void clearFxSwapRateDB() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DriverManager.getConnection(DBConfig.dbUrl, DBConfig.dbUser, DBConfig.dbPassword);
            String query = "delete from public.fx_swap_rate"; // Specify schema if needed
            logger.debug("Executing query: {}", query);

            preparedStatement = connection.prepareStatement(query);

            int rowsDeleted = preparedStatement.executeUpdate();

            logger.info("Deleted {} rows from public.fx_swap_rate table.", rowsDeleted);
            System.out.println("Deleted " + rowsDeleted + " rows from public.fx_swap_rate table.");
        } catch (SQLException e) {
            logger.error("SQL error while deleting from public.fx_swap_rate: {}", e.getMessage(), e);
            System.err.println("SQL error while deleting from public.fx_swap_rate: " + e.getMessage());
            // Optionally, print stack trace for debugging
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                logger.error("Error closing resources: {}", e.getMessage(), e);
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    // Validate input data against DB
    public Map<String, Object> validateInput(Map<String, String> inputData, List<FxSwapRateDBData> dbData) {
        Set<String> conflictingTenors = new HashSet<>();
        List<String> overlappingRanges = new ArrayList<>();
        String inputTenor = inputData.get(TestConstants.TENOR);
        Integer inputLower = Integer.valueOf(inputData.get(TestConstants.LOWERDAYCOUNT));
        Integer inputUpper = Integer.valueOf(inputData.get(TestConstants.UPPERDAYCOUNT));
        for (FxSwapRateDBData ruleSet : dbData) {
            // Tenor name match
            if (ruleSet.getTenor().trim().equalsIgnoreCase(inputTenor)) {
                conflictingTenors.add(ruleSet.getTenor());
                logConflict(ruleSet);
            }

            // Check bounds
            if (rangesOverlap(inputLower, inputUpper, ruleSet.getLowerBound(), ruleSet.getUpperBound())) {
                overlappingRanges.add(ruleSet.getLowerBound() + "," + " " + ruleSet.getUpperBound());
                logConflict(ruleSet);
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();

        if (!conflictingTenors.isEmpty()) {
            result.put("Matching Tenors", conflictingTenors);
        }

        if (!overlappingRanges.isEmpty()) {
            result.put("Overlapping Day Ranges", overlappingRanges);
        }

        if (result.isEmpty()) {
            result.put("Status", "Valid – no conflicts");
            result.put("IsValid", true);
        } else {
            result.put("Status", "Conflict – cannot create rule");
            result.put("IsValid", false);
        }
        return result;  // No conflicts found
    }

    private void logConflict(FxSwapRateDBData ruleSet) {
        logger.error("Conflicting Record: ID=" + ruleSet.getId() + ", Tenor=" + ruleSet.getTenor() +
                ", Lower=" + ruleSet.getLowerBound() + ", Upper=" + ruleSet.getUpperBound());
    }

    // Helper method to check if ranges overlap
    private boolean rangesOverlap(Integer inputLower, Integer inputUpper, Integer dbLower, Integer dbUpper) {
        return inputLower <= dbUpper && inputUpper >= dbLower;
    }
}
