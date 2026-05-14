package billing.stepDefinitions.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContext;
import org.billing.TestConstants;
import org.billing.api.payloads.request.post.FxSwapRatePostRequest;
import org.billing.api.payloads.request.put.FxSwapRatePutRequest;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.dbclasses.FxSwapRateDBData;
import org.billing.services.ui.FxSwapRateservices;
import org.testng.asserts.SoftAssert;

import java.sql.SQLException;
import java.util.List;

public class FxSwapApiStepDefinitions {
    private static final Logger logger = LogManager.getLogger(FxSwapApiStepDefinitions.class);
    FxSwapRateservices fxSwapRateservices;
    private ScenarioContext scenarioContext;
    private SoftAssert softAssert;
    FxSwapRatePostRequest fxSwapRatePostRequest;
    FxSwapRatePutRequest fxSwapRatePutRequest;

    public FxSwapApiStepDefinitions(ScenarioContext scenarioContext) {
        this.fxSwapRateservices = new FxSwapRateservices(scenarioContext);
        softAssert = SoftAssertContainer.getInstance();
    }

    @Given("I create a valid POST request payload for FxSwapRate data")
    public void i_create_a_valid_post_request_payload_for_fx_swap_rate_data(DataTable dataTable) throws SQLException {
        fxSwapRatePostRequest = fxSwapRateservices.createAFxSwapPostRequest(dataTable);
    }

    @When("I send a POST request to the endpoint")
    public void i_send_a_post_request_to_the_api_fx_swap_rates_endpoint() throws JsonProcessingException {
        fxSwapRateservices.hitEndPointUsingCreatedPostReq(fxSwapRatePostRequest);
    }

    @When("I send a POST request to the endpoint using jsonpath")
    public void i_send_a_post_request_to_the_api_fx_swap_rates_endpoint_using_jsonpath() throws JsonProcessingException {
        fxSwapRateservices.hitEndPointUsingJsonPathPostReq(fxSwapRatePostRequest);
    }

    @Then("The API should return a {int} Created response")
    public void the_api_should_return_a_created_response(Integer int1) throws JsonProcessingException {
        fxSwapRateservices.validateApiStatusCode(int1);
    }


    @Then("The response body should include a unique FxSwapRate ID")
    public void the_response_body_should_include_a_unique_fx_swap_rate_id() throws SQLException {
        fxSwapRateservices.validateFxResponseUniqueIDReceived();
    }

    @Then("The response payload should match the request payload")
    public void the_response_payload_should_match_the_request_payload() throws SQLException {
        fxSwapRateservices.validateFxResponseDBPayloadWithRetry();
    }

    @Given("I create a new FxSwapRate entry via POST request:")
    public void i_create_a_new_fx_swap_rate_entry_via_post_request(DataTable dataTable) throws JsonProcessingException, SQLException {
        fxSwapRatePostRequest = fxSwapRateservices.createAFxSwapPostRequest(dataTable);
        fxSwapRateservices.hitEndPointUsingCreatedPostReq(fxSwapRatePostRequest);
    }

    @Given("I store the ID of the newly created FxSwapRate")
    public void i_store_the_id_of_the_newly_created_fx_swap_rate() throws SQLException {
        fxSwapRateservices.validateFxResponseUniqueIDReceived();
    }

    @When("I create a PUT request payload to update the FxSwapRate properties:")
    public void i_create_a_put_request_payload_to_update_the_fx_swap_rate_properties(DataTable dataTable) throws JsonProcessingException {
        fxSwapRatePutRequest = fxSwapRateservices.createAFxSwapPutRequest(dataTable);
    }

    @When("I send a PUT request to the endpoint with the stored ID")
    public void i_send_a_put_request_to_the_api_fx_swap_rates_endpoint_with_the_stored_id() throws JsonProcessingException {
        fxSwapRateservices.hitEndPointUsingCreatedPutReq(fxSwapRatePutRequest);
    }

    @Then("The API should return a {int} OK response")
    public void the_api_should_return_a_ok_response(Integer int1) throws JsonProcessingException {
        fxSwapRateservices.validateApiStatusCode(int1);
    }

    @Then("The response body should reflect the updated FxSwapRate properties")
    public void the_response_body_should_reflect_the_updated_fx_swap_rate_properties() throws SQLException, JsonProcessingException {
        fxSwapRateservices.validateFxPUTResponseAPIPayload();
      //  fxSwapRateservices.validateFxResponseDBPayloadWithRetry();
    }

    @Given("I generate a random invalid FxSwapRate ID \\(e.g., {int})")
    public void i_generate_a_random_invalid_fx_swap_rate_id_e_g(Integer id) {
        fxSwapRateservices.setTheFxSwapRate(id);
    }

    @When("I send a DELETE request to the endpoint with the invalid ID")
    public void i_send_a_delete_request_to_the_api_fx_swap_rates_endpoint_with_the_invalid_id() throws JsonProcessingException {
        fxSwapRateservices.hitEndPointUsingDelReq();
    }

    @Then("The API should return a {int} Not Found response")
    public void the_api_should_return_a_not_found_response(Integer responseCode) {
        fxSwapRateservices.assertTheCode(responseCode);
    }

    @When("I send a DELETE request to the endpoint with the stored ID")
    public void i_send_a_delete_request_to_the_endpoint_with_the_stored_id() throws JsonProcessingException {
        fxSwapRateservices.hitEndPointUsingDelReq();

    }

    @Then("The API should return a {int} No Content response")
    public void the_api_should_return_a_no_content_response(Integer responseCode) {
        fxSwapRateservices.assertTheCode(responseCode);
    }

    @Then("The entry should no longer exist when checked with a GET request")
    public void the_entry_should_no_longer_exist_when_checked_with_a_get_request() throws SQLException {
        List<FxSwapRateDBData> fxSwapRateDBData = fxSwapRateservices.fetchDBDetails();
        fxSwapRateservices.validateIfTheRecordDoesntExist(fxSwapRateDBData);
    }

    @Given("I create a POST request payload with field exceeding limits:")
    public void i_create_a_post_request_payload_with_field_exceeding_limits(DataTable dataTable) throws SQLException {
        fxSwapRatePostRequest = fxSwapRateservices.createAFxSwapPostRequest(dataTable);
    }
    @Then("The API should return a {int} Bad Request response")
    public void the_api_should_return_a_bad_request_response(Integer responseCode) {
        fxSwapRateservices.assertTheCode(responseCode);
    }
    @Then("The response body should include an error such as:")
    public void the_response_body_should_include_an_error_such_as(DataTable dataTable) {
        fxSwapRateservices.validateErrorResponse(dataTable);
    }

}
