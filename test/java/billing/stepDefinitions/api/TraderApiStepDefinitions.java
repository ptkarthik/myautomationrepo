package billing.stepDefinitions.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContext;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.TestConstants;
import org.billing.api.payloads.request.post.TraderPostRequest;
import org.billing.api.payloads.request.put.TraderPutRequest;
import org.billing.api.responses.put.FieldErrorResponse;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.services.ui.TraderServices;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.sql.SQLException;

public class TraderApiStepDefinitions {

    private static final Logger logger = LogManager.getLogger(TraderApiStepDefinitions.class);
    private TraderServices traderServices;
    private SoftAssert softAssert;
    private ScenarioContextWithObject scenarioContextWithObject;
    private ScenarioContext scenarioContext;

    public TraderApiStepDefinitions(ScenarioContextWithObject scenarioContextWithObject, ScenarioContext scenarioContext) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.scenarioContext = scenarioContext;
        this.traderServices = new TraderServices(scenarioContextWithObject, scenarioContext);
        this.softAssert = SoftAssertContainer.getInstance();
    }

    @Given("I send a GET request to the endpoint")
    public void sendGetRequestToEndpoint() throws JsonProcessingException {
        traderServices.createATradersGetAllRequest();
    }

    @Then("I should receive a response with status code {int}")
    public void validateResponseStatusCode(int statusCode) throws JsonProcessingException {
        traderServices.validateApiStatusCode(statusCode);
    }

    @Then("I should receive a response with status code {int} for GetAll")
    public void validateResponseStatusCodeGetAll(int statusCode) throws JsonProcessingException {
        traderServices.validateApiStatusCodeGetAll(statusCode);
    }

    @Then("I should receive a response with status code {int} for upload")
    public void validateResponseStatusCodeForUpload(int statusCode) throws JsonProcessingException {
        traderServices.validateApiStatusCodeForUpload(statusCode);
    }

    @And("the response should include a list of traders")
    public void validateListOfTradersInResponse() throws SQLException {
        traderServices.validatetheGetReponseReceivedWithDBAndAPI();
    }

    @When("I send a GET request to the endpoint with the following query parameters:")
    public void sendGetRequestWithQueryParameters(DataTable dataTable) throws JsonProcessingException {
        traderServices.createATradersGetRequest(dataTable);
    }

    @Then("The response should include an empty array")
    public void validateOutOfBoundsMessage() {
        traderServices.validateEmptyArrayOfResponse();
    }

    @When("I send a POST request to the endpoint with the following query parameters and I attach the file as form data:")
    public void uploadCsvFile(DataTable queryParams) {
        traderServices.createTradersUploadRequest(queryParams);
    }

    @Then("the response should match the file uploaded vs the CSV files")
    public void validateCsvUploadResponse() throws IOException {
        traderServices.createATradersGetAllRequest();
        traderServices.readExcelAndConvertTheEntriestoPOJO();
        traderServices.compareExcelVSAPIAndLogResults();
    }

    @Given("I send a POST request to the endpoint with the following body:")
    public void sendPostRequestForTrader(DataTable body) throws JsonProcessingException {
        TraderPostRequest traderPostRequest = traderServices.createATradersPostRequest(body);
        scenarioContextWithObject.setData(TestConstants.TRADER_POSTREQ, traderPostRequest);
        traderServices.hitEndPointUsingCreatedPostReq(((TraderPostRequest) scenarioContextWithObject.
                getData(TestConstants.TRADER_POSTREQ)));
    }

    @Then("The response should confirm the trader has been successfully created")
    public void validateTraderCreatedConfirmation() throws JsonProcessingException {
        traderServices.createATradersGetAllRequest();
        traderServices.compareResponseVsDataGiven();
    }


    @Given("I send a POST request to the endpoint {string} with the following body:")
    public void createTraderForUpdateTesting(String endpoint, DataTable body) {
        // Implementation goes here
    }

    @When("I send a PUT request to the endpoint {string} with the following body:")
    public void sendPutRequestToUpdateTrader(String endpoint, DataTable data) throws JsonProcessingException {
        TraderPutRequest traderPutRequest = traderServices.createAPutTraderRequest(endpoint, data);
        scenarioContextWithObject.setData(TestConstants.TRADER_PUTREQ, traderPutRequest);
        traderServices.hitEndPointUsingCreatedPutReq((TraderPutRequest) scenarioContextWithObject.getData(TestConstants.TRADER_PUTREQ));
    }

    @Then("The response should contain an error message indicating {string}")
    public void validatePutErrorResponse(String errorMessage) {
        FieldErrorResponse errorResponse = (FieldErrorResponse) scenarioContextWithObject.getData(TestConstants.TRADER_PUTERROR_RESPOJO);
        String actualMessage = errorResponse.getFieldErrors().get(0).getMessage();

        // Log expected and actual values
        logger.info("Validating error message. Expected: '{}', Actual: '{}'", errorMessage, actualMessage);

        softAssert.assertEquals(actualMessage, errorMessage);
    }

    @When("I send a DELETE request to the endpoint {string}")
    public void sendDeleteRequest(String endpoint) {
        traderServices.createDeleteRequest(endpoint);
    }

    @Then("The response should confirm the trader has been successfully deleted")
    public void validateTraderDeletion() throws JsonProcessingException {
        traderServices.fetchDB();
        traderServices.validateIfTheRecordDoesntExist();
    }

}