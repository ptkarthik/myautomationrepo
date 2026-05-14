package billing.stepDefinitions.api;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.core.Baseclass;

public class APIHooks extends Baseclass {
    private static final Logger logger = LogManager.getLogger(APIHooks.class);

    @Before
    public void beforeHooks(Scenario scenario) {
        SoftAssertContainer.initialize();
//        DBConfig dbConfig = new DBConfig();
//        dbConfig.setUpDbConfig();
        // Confirm value is set
        logger.debug("ThreadContext scenarioName set as: {}", ThreadContext.get("scenarioName"));
        logger.info("==============================");
        logger.info("Starting Scenario: {}", scenario.getName());
        logger.info("==============================");
    }

    @After
    public void afterHooks(Scenario scenario) {
        try {
            // Trigger all assertions in the container
            SoftAssertContainer.getInstance().assertAll();
        } catch (AssertionError e) {
            // Capture assertion errors and log them
            System.err.println("Soft assertion errors found during the test: " + e.getMessage());
            throw e; // Rethrow to mark scenario as failed in the report
        } finally {
            // Cleanup after assertions are flushed
            SoftAssertContainer.cleanup();
        }
        // Log completion and clear ThreadContext scenarioName
        logger.info("==============================");
        logger.info("Completed Scenario: {}", scenario.getName());
        logger.debug("Clearing ThreadContext scenarioName: {}", ThreadContext.get("scenarioName"));
        logger.info("==============================");

        ThreadContext.clearAll();
    }
}
