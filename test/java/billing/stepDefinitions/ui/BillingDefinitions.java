package billing.stepDefinitions.ui;

import org.billing.Context.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.core.Baseclass;
import org.billing.pages.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

/**
 * Step definitions for Billing application scenarios.
 */
public class BillingDefinitions extends Baseclass {
    private static final Logger logger = LogManager.getLogger(BillingDefinitions.class);
    private final ScenarioContext scenarioContext;

    BillingDefinitions(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }
    HomePage homePage = new HomePage();
    BrokerInvoicePage brokerPage = new BrokerInvoicePage();
    CapRulesPage capRulePage = new CapRulesPage();
    ViewOrdersPage viewOrdersPage = new ViewOrdersPage();
    List<WebElement> versions;
    String capValue;

    /**
     * Navigates the user to the login page.
     */
    @Given("The user is in the login page of the Billing application")
    public void theUserIsInTheLoginPageOfTheBillingApplication() {
        try {
            logger.info("Navigating to the login page of the Billing application.");
            // Your implementation logic here
        } catch (Exception e) {
            logger.error("Error occurred while navigating to the login page of the Billing application.", e);
        }
    }

    /**
     * Navigates the user to a specific page.
     */
    @When("The user navigates to the {string} page")
    public void the_user_navigates_to_the_page(String subMenu) {
        try {
            logger.info("Navigating to the '{}' page.", subMenu);
            clickElement(By.linkText(subMenu), 10);
            logger.info("Navigated to the '{}' page successfully.", subMenu);
        } catch (Exception e) {
            logger.error("Failed to navigate to the '{}' page.", subMenu, e);
        }
    }

    /**
     * Fetches client and broker-specific values based on input.
     */
    @When("The user fetch the value of client as {string} and broker as {string}")
    public void the_user_fetch_the_value_of_client_as_and_broker_as(String client, String broker) {
        try {
            logger.info("Fetching the cap rule value for client '{}' and broker '{}'.", client, broker);
            capValue = capRulePage.getCapRuleValue(client, broker);
            logger.debug("Cap rule value fetched: {}", capValue);
        } catch (Exception e) {
            logger.error("Error occurred while fetching the cap rule value for client '{}' and broker '{}'.", client, broker, e);
        }
    }

    /**
     * Selects the month with client and broker values.
     */
    @When("The user selects the month with client as {string} and broker as {string}")
    public void the_user_selects_the_month_with_client_as_and_broker_as(String client, String broker) {
        try {
            logger.info("Selecting month with client '{}' and broker '{}'.", client, broker);
            viewOrdersPage.selectClient(client);
            logger.info("Client '{}' selected successfully.", client);
            viewOrdersPage.selectBroker(broker);
            logger.info("Broker '{}' selected successfully.", broker);
            viewOrdersPage.selectDates();
            logger.info("Date range selected successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while selecting month with client '{}' and broker '{}'.", client, broker, e);
        }
    }


    /**
     * Navigates the user back to the home page.
     */
    @And("The user navigates to the home page")
    public void theUserNavigatesToTheHomePage() {
        try {
            logger.info("Navigating to the home page.");
            homePage.navigateToHomePage();
            logger.info("Navigation to the home page successful.");
        } catch (Exception e) {
            logger.error("Error occurred while navigating to the home page.", e);
        }
    }

    /**
     * Selects the month-year.
     */
    @And("The user selects the month-year {string}")
    public void theUserSelectsTheMonthYear(String monthYear) {
        try {
            logger.info("Selecting month-year '{}'.", monthYear);
            homePage.selectMonthYear(monthYear);
            logger.info("Month-year '{}' selected successfully.", monthYear);
        } catch (Exception e) {
            logger.error("Error occurred while selecting month-year '{}'.", monthYear, e);
        }
    }

    /**
     * Selects billing method.
     */
    @And("The user selects the billing method to {string}")
    public void theUserSelectsThebillingMethod(String billingMethod) {
        try {
            logger.info("Selecting billing method '{}'.", billingMethod);
            brokerPage.selectBillingMethod(billingMethod);
            logger.info("Billing method '{}' selected successfully.", billingMethod);
        } catch (Exception e) {
            logger.error("Error occurred while selecting billing method '{}'.", billingMethod, e);
        }
    }

    /**
     * Filters broker records by broker name.
     */
    @And("The user filters the records by broker {string}")
    public void theUserFiltersTheRecordsByBroker(String broker) {
        try {
            logger.info("Filtering records for broker '{}'.", broker);
            brokerPage.filterByBroker(broker);
            logger.info("Records filtered successfully for broker '{}'.", broker);
        } catch (Exception e) {
            logger.error("Error occurred while filtering records for broker '{}'.", broker, e);
        }
    }

    /**
     * Verifies if the broker is displayed in search results.
     */
    @Then("The broker {string} is displayed in the search result")
    public void theBrokerIsDisplayedInTheSearchResult(String broker) {
        try {
            logger.info("Verifying if broker '{}' is displayed in search results.", broker);
            brokerPage.verifyBrokerInSearchResult(broker);
            logger.info("Broker '{}' is displayed successfully in search results.", broker);
        } catch (Exception e) {
            logger.error("Error occurred while verifying broker '{}' in search results.", broker, e);
        }
    }

    /**
     * Verifies the available versions for a broker.
     */
    @And("The user verifies the number of versions available for {string}")
    public void theUserVerifiesTheNumberOfVersionsAvailableFor(String broker) {
        try {
            logger.info("Verifying the number of versions available for broker '{}'.", broker);
            versions = brokerPage.verifyNumberOfVersions(broker);
            logger.info("Number of versions available for broker '{}': {}", broker, versions.size());
        } catch (Exception e) {
            logger.error("Error occurred while verifying the number of versions for broker '{}'.", broker, e);
        }
    }

    /**
     * Generates a new build for a broker.
     */
    @When("The user generates a new build for {string}")
    public void theUserGeneratesANewBuildFor(String broker) {
        try {
            logger.info("Generating new build for broker '{}'.", broker);
            brokerPage.generateNewBuild(broker);
            logger.info("New build generated successfully for broker '{}'.", broker);
        } catch (Exception e) {
            logger.error("Error occurred while generating a new build for broker '{}'.", broker, e);
        }
    }

    /**
     * Generates a fast invoice build for a broker.
     */
    @When("The user generates a new fast invoice build for {string}")
    public void theUserGeneratesANewFastInvoiceBuildFor(String broker) {
        try {
            logger.info("Generating new fast invoice build for broker '{}'.", broker);
            brokerPage.generateNewFastInvoiceBuild(broker);
            logger.info("New fast invoice build generated successfully for broker '{}'.", broker);
        } catch (Exception e) {
            logger.error("Error occurred while generating new fast invoice build for broker '{}'.", broker, e);
        }
    }

    /**
     * Verifies if a new version is generated for a broker.
     */
    @Then("A new version should be generated for {string}")
    public void aNewVersionShouldBeGeneratedFor(String broker) {
        try {
            logger.info("Verifying if a new version is generated for broker '{}'.", broker);
            brokerPage.verifyNewVersionGenerated(broker, versions.size());
            logger.info("New version generated successfully for broker '{}'.", broker);
        } catch (Exception e) {
            logger.error("Error occurred while verifying the new version for broker '{}'.", broker, e);
        }
    }
}