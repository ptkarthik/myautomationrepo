package billing.stepDefinitions.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContext;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.api.APIUtils.APIAuthentication;
import org.billing.api.endpoints.EndPoints;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.services.apiservices.BaseApiServices;
import org.billing.api.responses.post.BrokerResponse;
import org.billing.api.payloads.request.post.BrokerAddRequestPayload;
import org.billing.utils.apiutilities.APIUtils;
import org.billing.utils.propconfig.PropertiesReader;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrokerAPIStepDefinitions {
    private static final Logger logger = LogManager.getLogger(BrokerAPIStepDefinitions.class);
    private final ScenarioContext scenarioContext;
    private final ScenarioContextWithObject scenarioContextWithObject;
    private SoftAssert softAssert;
    private Response response;

    public BrokerAPIStepDefinitions(ScenarioContext scenarioContext, ScenarioContextWithObject scenarioContextWithObject) {
        this.scenarioContext = scenarioContext;
        this.scenarioContextWithObject = scenarioContextWithObject;
        softAssert = SoftAssertContainer.getInstance();
    }

    /**
     * Adds a broker using the POST API endpoint.
     */
    @Given("I add the broker name")
    public void i_add_the_broker_name(DataTable dataTable) throws JsonProcessingException {
        try {
            logger.info("Adding a new broker using the POST API endpoint.");

            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", "Bearer " + APIAuthentication.postAuthorizationCode()));
            headers.add(new Header("Content-Type", "application/json"));

            Map<String, String> brokerMap = dataTable.asMaps().get(0);
            String addedBroker = brokerMap.get("name") + BaseApiServices.generateRandomString();

            logger.debug("Generated broker name '{}'.", addedBroker);

            scenarioContext.setData("BrokerName", addedBroker);

            BrokerAddRequestPayload brokerAddRequestPayload = new BrokerAddRequestPayload(
                    scenarioContext.getData("BrokerName"));

            ObjectMapper objectMapper = new ObjectMapper();
            String payload = objectMapper.writeValueAsString(brokerAddRequestPayload);

            logger.debug("Generated request payload: {}", payload);

            response = APIUtils.postWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    EndPoints.POST_BROKER,
                    payload,
                    headers
            );
            scenarioContextWithObject.setData("Response",response);
            logger.info("POST request to add broker completed. Response status code: {}", response.getStatusCode());
        } catch (Exception e) {
            logger.error("Error occurred while adding a new broker.", e);
            throw e;
        }
    }

    /**
     * Sets the broker name.
     */
    @Given("I mention the broker name")
    public void i_mention_the_broker_name(DataTable dataTable) {
        try {
            Map<String, String> brokerMap = dataTable.asMaps().get(0);
            scenarioContext.setData("BrokerName", brokerMap.get("name"));
            logger.info("Broker name '{}' has been set in the scenario context.", scenarioContext.getData("BrokerName"));
        } catch (Exception e) {
            logger.error("Error occurred while setting the broker name.", e);
            throw e;
        }
    }

    /**
     * Deletes the broker using the DELETE API endpoint.
     */
    @When("i delete the broker")
    public void i_delete_the_broker() {
        try {
            logger.info("Deleting broker '{}' using the DELETE API endpoint.", scenarioContext.getData("BrokerName"));

            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", "Bearer " + APIAuthentication.postAuthorizationCode()));

            Map<String, String> pathData = new HashMap<>();
            pathData.put("name", scenarioContext.getData("BrokerName"));

            response = APIUtils.deleteWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    pathData,
                    EndPoints.DELETE_BROKER,
                    headers
            );
            scenarioContextWithObject.setData("Response",response);
            logger.info("DELETE request for broker '{}' completed. Response status code: {}", scenarioContext.getData("BrokerName"), response.getStatusCode());
        } catch (Exception e) {
            logger.error("Error occurred while deleting broker '{}'.", scenarioContext.getData("BrokerName"), e);
            throw e;
        }
    }

    /**
     * Fetches broker details using the GET API endpoint.
     */
    @Given("I fetch broker details with Name")
    public void i_fetch_broker_details_with_name() {
        try {
            logger.info("Fetching broker details using the GET API endpoint.");

            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", "Bearer " + APIAuthentication.postAuthorizationCode()));
            Map<String,String> reqBody = Map.of("name", scenarioContext.getData("BrokerName"));
            logger.debug("Broker name '{}' set in the scenario context.", scenarioContext.getData("BrokerName"));

            response = APIUtils.postWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    EndPoints.GET_ALL_BROKER,
                    reqBody,
                    headers
            );
            scenarioContextWithObject.setData("Response",response);

            logger.info("GET request for broker details completed. Response status code: {}", response.getStatusCode());
        } catch (Exception e) {
            logger.error("Error occurred while fetching broker details.", e);
            throw e;
        }
    }

    /**
     * Validates the response status code.
     */
    @Then("I validate the response status code is {int}")
    public void validateStatusCode(int expectedStatusCode) {
        try {
            response = (Response) scenarioContextWithObject.getData("Response");
            logger.info("Validating response status code. Expected: {}, Actual: {}",
                    expectedStatusCode, response.getStatusCode());
            softAssert.assertEquals(expectedStatusCode, response.getStatusCode(), "Status code does not match.");
        } catch (Exception e) {
            logger.error("Error occurred while validating the response status code.", e);
            throw e;
        }
    }

    /**
     * Validates the added broker name in the response.
     */
    @And("I validate the added broker name")
    public void validateCreatedUserName() throws JsonProcessingException {
        try {
            logger.info("Validating the added broker name in the response.");

            ObjectMapper objectMapper = new ObjectMapper();
            BrokerResponse brokers = objectMapper.readValue(response.asString(), BrokerResponse.class);

            logger.debug("Response payload: {}", response.asString());

            softAssert.assertEquals(scenarioContext.getData("BrokerName"), brokers.getName(),
                    "Broker names do not match.");

            logger.info("Broker name validation successful.");
        } catch (Exception e) {
            logger.error("Error occurred while validating the added broker name.", e);
            throw e;
        }
    }

    /**
     * Validates the broker name present in the response list.
     */
    @And("I validate the broker name")
    public void validateUserName() throws JsonProcessingException {
        try {
            logger.info("Validating broker names in response list.");

            ObjectMapper objectMapper = new ObjectMapper();
            List<BrokerResponse> brokers = objectMapper.readValue(
                    response.asString(),
                    new TypeReference<List<BrokerResponse>>() {
                    }
            );

            logger.debug("Response payload: {}", response.asString());

            softAssert.assertEquals(scenarioContext.getData("BrokerName"), brokers.get(0).getName(),
                    "Broker name does not match.");
            logger.info("Broker name validation in response list successful.");
        } catch (Exception e) {
            logger.error("Error occurred while validating the broker name in response list.", e);
            throw e;
        }
    }
}