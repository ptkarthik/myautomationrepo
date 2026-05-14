package billing.stepDefinitions.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContext;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.TestConstants;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.dbclasses.CurrencyRateDbData;
import org.billing.services.ui.CurrencyRateServices;
import org.testng.asserts.SoftAssert;

import java.sql.SQLException;
import java.util.List;

public class CurrencyRateApiStepDefinitions {

    private static final Logger logger = LogManager.getLogger(CurrencyRateApiStepDefinitions.class);
    private CurrencyRateServices currencyRateServices;
    private SoftAssert softAssert;
    private ScenarioContextWithObject scenarioContextWithObject;
    private ScenarioContext scenarioContext;

    public CurrencyRateApiStepDefinitions(ScenarioContextWithObject scenarioContextWithObject, ScenarioContext scenarioContext) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.scenarioContext = scenarioContext;
        this.currencyRateServices = new CurrencyRateServices(scenarioContextWithObject, scenarioContext);
        this.softAssert = SoftAssertContainer.getInstance();
    }

    @Given("the following payload for the POST request:")
    public void the_following_payload_for_the_post_request(DataTable dataTable) {
        currencyRateServices.createAPostRequest(dataTable);
    }

    @When("a POST request is sent to {string} with {string} authorization")
    public void a_post_request_is_sent_to_with_authorization(String endpoint, String authStatus) {
        currencyRateServices.sendPostRequest(endpoint, authStatus);
    }


    @Then("check the response is displayed in response List")
    public void check_the_response_is_displayed_in_ReponseList() throws JsonProcessingException {
        if (((Response) scenarioContextWithObject.getData(TestConstants.RESPONSE)).getStatusCode() == 201) {
            currencyRateServices.createCurrencyRateGetAllRequest();
            currencyRateServices.compareResponseVsDataGiven();
        }
    }

    @When("a GET request is sent to {string} with {string} authorization")
    public void a_get_request_is_sent_to_with_authorization(String endpoint, String authStatus) {
        currencyRateServices.sendGetRequest(endpoint, authStatus);
    }

    @Then("validate if the response is matching with DB")
    public void validate_if_the_response_is_matching_with_db() throws SQLException {
        List<CurrencyRateDbData> currencyRateDbDataList = currencyRateServices.fetchDBDetails();
        currencyRateServices.validateDBvsAPIData(currencyRateDbDataList);
    }

    @When("a DELETE request is sent to {string} with {string} authorization")
    public void a_delete_request_is_sent_to_with_authorization(String endpoint, String authorizationStatus) {
        logger.info("Sending DELETE request to '{}' with authorization status '{}'.", endpoint,
                authorizationStatus);
        currencyRateServices.sendDeleteRequest(endpoint, authorizationStatus);
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(int statusCode) throws JsonProcessingException {
        currencyRateServices.validateApiStatusCode(statusCode);
    }

    @Given("the following payload for the PUT request:")
    public void the_following_payload_for_the_put_request(DataTable dataTable) {
        // Create the PUT request payload dynamically from the DataTable
        currencyRateServices.createAPutRequest(dataTable);
    }

    @When("a PUT request is sent to {string} with {string} authorization")
    public void a_put_request_is_sent_to_with_authorization(String endpoint, String authorizationStatus) {
        logger.info("Sending PUT request to '{}' with authorization status '{}'.", endpoint, authorizationStatus);
        currencyRateServices.sendPutRequest(endpoint, authorizationStatus);
    }
}