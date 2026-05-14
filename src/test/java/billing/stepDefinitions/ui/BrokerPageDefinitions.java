package billing.stepDefinitions.ui;

import io.restassured.response.Response;
import org.billing.api.APIUtils.APIAuthentication;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.dbconfig.BrokerPageDBConfig;
import org.billing.Context.ScenarioContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.Header;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.utils.propconfig.PropertiesReader;
import org.billing.api.endpoints.EndPoints;
import org.billing.services.apiservices.BaseApiServices;
import org.billing.api.responses.post.BrokerResponse;
import org.billing.services.ui.BrokerPageServices;
import org.billing.utils.apiutilities.APIUtils;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Step Definitions for the Broker Page-related scenarios.
 */
public class BrokerPageDefinitions {

    private final BrokerPageServices brokerPageServices = new BrokerPageServices();
    private final ScenarioContext scenarioContext;
    private static final Logger logger = LogManager.getLogger(BrokerPageDefinitions.class);
    public BrokerPageDBConfig dbConfig = new BrokerPageDBConfig();
    private Response response;
    private SoftAssert softAssert;
    /**
     * Constructor to initialize ScenarioContext.
     */
    public BrokerPageDefinitions(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        softAssert = SoftAssertContainer.getInstance();
    }

    @And("The user navigates to the broker page")
    public void theUserNavigatesToTheBrokerPage() {
        try {
            brokerPageServices.openBrokerPage();
            logger.info("Successfully navigated to the Broker Page.");
        } catch (Exception e) {
            logger.error("Error occurred while navigating to the Broker Page.", e);
        }
    }

    @And("The user Click to Add New broker")
    public void theUserClicksToAddANewBroker() {
        try {
            brokerPageServices.clickAddBroker();
            logger.info("Clicked on the Add New Broker button.");
        } catch (Exception e) {
            logger.error("Error occurred while clicking the Add New Broker button.", e);
        }
    }

    @Then("User validates the AddBrokerTab Details")
    public void theUserValidatesTheAddBrokerTab() {
        try {
            logger.info("Starting validation of the Add Broker Tab.");
            brokerPageServices.validateAddBrokerTab();
            logger.info("Add Broker Tab validation completed successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while validating the Add Broker Tab.", e);
        }
    }

    @When("the user enters the following broker details")
    public void the_user_enters_the_following_broker_details(DataTable dataTable) {
        try {
            Map<String, String> brokerData = dataTable.asMaps().get(0);

            // Generate random Broker name for testing
            String brokerName = brokerData.get("BrokerName") + BaseApiServices.generateRandomString();
            scenarioContext.setData("BrokerName", brokerName);

            logger.info("Broker Name '{}' has been set in the scenario context.", brokerName);
            brokerPageServices.enterInputText(brokerName);
            logger.info("Entered broker details successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while entering broker details.", e);
        }
    }

    @When("the user saves the broker")
    public void the_user_saves_the_broker() {
        try {
            brokerPageServices.saveTheBroker();
            logger.info("The broker has been saved successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while saving the broker.", e);
        }
    }

    @Given("I fetch given broker details with Name")
    public void i_fetch_given_broker_details_with_name() {
        try {
            logger.info("Fetching broker details using the GET API Endpoint.");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", "Bearer " + APIAuthentication.postAuthorizationCode()));

            Map<String, String> brokerMap = new HashMap<>();
            brokerMap.put("name", scenarioContext.getData("BrokerName"));

            response = APIUtils.getWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    EndPoints.GET_ALL_BROKER,
                    brokerMap,
                    headers
            );
            logger.info("Broker details fetched successfully. Response status code: {}", response.getStatusCode());
            logger.debug("Response JSON: {}", response.asString());
        } catch (Exception e) {
            logger.error("Error occurred while fetching broker details.", e);
        }
    }

    @And("I validate the broker name in API response")
    public void validateUserName() {
        try {
            logger.info("Validating the broker name in the API response.");

            ObjectMapper objectMapper = new ObjectMapper();
            List<BrokerResponse> brokers = objectMapper.readValue(
                    response.asString(),
                    new TypeReference<List<BrokerResponse>>() {
                    }
            );

            String expectedBrokerName = scenarioContext.getData("BrokerName");
            String actualBrokerName = brokers.get(0).getName();

            logger.debug("Expected Broker Name: {}, Actual Broker Name: {}", expectedBrokerName, actualBrokerName);

            softAssert.assertEquals(expectedBrokerName, actualBrokerName, "Broker name does not match.");
            softAssert.assertAll();

            logger.info("Broker name validation in API response completed successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while validating the broker name in the API response.", e);
        }
    }

    @Then("the broker should be saved successfully")
    public void the_broker_should_be_saved_successfully() {
        try {
            String brokerName = scenarioContext.getData("BrokerName");
            logger.info("Validating the saved broker '{}' on the Broker Page.", brokerName);

            brokerPageServices.validateTheInputtedBrokerBySearch(brokerName);
            logger.info("Broker '{}' successfully validated after saving.", brokerName);
        } catch (Exception e) {
            logger.error("Error occurred while validating the saved broker.", e);
        }
    }

    @When("the user searches the added broker")
    public void the_user_searches_the_added_broker() {
        try {
            String brokerName = scenarioContext.getData("BrokerName");
            logger.info("Searching for the broker '{}' on the Broker Page.", brokerName);

            brokerPageServices.validateTheInputtedBrokerBySearch(brokerName);
            logger.info("Broker '{}' successfully found on the Broker Page.", brokerName);
        } catch (Exception e) {
            logger.error("Error occurred while searching for the added broker.", e);
        }
    }

    @When("the user deletes the broker back")
    public void the_user_deletes_the_broker_back() {
        try {
            String brokerName = scenarioContext.getData("BrokerName");
            logger.info("Deleting the broker '{}' from the Broker Page.", brokerName);

            brokerPageServices.userDeletestheBrokerBack(brokerName);
            logger.info("Broker '{}' successfully deleted.", brokerName);
        } catch (Exception e) {
            logger.error("Error occurred while deleting the broker.", e);
        }
    }

    @Then("validate broker is deleted back")
    public void validate_broker_is_deleted_back() {
        try {
            logger.info("Validating if the broker has been successfully deleted from the Broker Page.");
            brokerPageServices.validateTheDeletedBroker();
            logger.info("Broker deletion validation completed successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while validating broker deletion.", e);
        }
    }

    @Given("I validate the broker in DataBase")
    public void i_validate_the_broker_in_data_base() {
        try {
            String brokerName = scenarioContext.getData("BrokerName");
            logger.info("Validating the broker '{}' in the database.", brokerName);

            dbConfig.setUpDbConfig();
            dbConfig.getAllDataOfBrokerQuery(brokerName);
            softAssert.assertAll();

            logger.info("Database validation for broker '{}' completed successfully.", brokerName);
        } catch (Exception e) {
            logger.error("Error occurred while validating the broker in the database.", e);
        }
    }
}