package billing.stepDefinitions.ui;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.core.Baseclass;
import org.billing.dbconfig.DBConfig;

import java.time.Duration;
import java.util.Properties;

public class Hooks extends Baseclass {
    private static final Logger logger = LogManager.getLogger(Hooks.class);

    @Before
    public void beforeHooks(Scenario scenario) {
        ThreadContext.put("scenarioName", scenario.getName());
        SoftAssertContainer.initialize();
        DBConfig dbConfig = new DBConfig();
        dbConfig.setUpDbConfig();
        // Confirm value is set
        logger.debug("ThreadContext scenarioName set as: {}", ThreadContext.get("scenarioName"));
        logger.info("==============================");
        logger.info("Starting Scenario: {}", scenario.getName());
        logger.info("==============================");

        // Other setup logic
        Properties prop = readConfigFile();
        setDriver(prop.getProperty("execution_strategy"), prop.getProperty("browser"));
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(80));
        getDriver().get(prop.getProperty("internal_uat_url"));
        logger.debug("Browser initialized with URL: {}", prop.getProperty("internal_uat_url"));
        getDriver().manage().window().maximize();
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
    }

    @After
    public void afterHooks(Scenario scenario) {
        try {
            logger.info("Performing SoftAssert aggregation...");
            SoftAssertContainer.getInstance().assertAll(); // This aggregates all assertions
        } catch (AssertionError e) {
            // Log aggregated assertion failures in detail
            logger.error("Assertion errors encountered during scenario execution: {}", e.getMessage());
            throw e; // Rethrow to report the test as failed
        } finally {
            logger.info("Cleaning up SoftAssert Container...");
            SoftAssertContainer.cleanup(); // Cleanup SoftAssert to avoid memory leaks
        }
        // Log completion and clear ThreadContext scenarioName
        logger.info("==============================");
        logger.info("Completed Scenario: {}", scenario.getName());
        logger.debug("Clearing ThreadContext scenarioName: {}", ThreadContext.get("scenarioName"));
        logger.info("==============================");

        ThreadContext.clearAll();

        // Quit browser driver
        getDriver().quit();
        logger.info("Browser closed and test execution finished.");
    }
}
