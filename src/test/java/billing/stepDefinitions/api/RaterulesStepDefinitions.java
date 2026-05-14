package billing.stepDefinitions.api;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContext;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.services.apiservices.Raterulesservices;
import org.testng.asserts.SoftAssert;

public class RaterulesStepDefinitions {
    private static final Logger logger = LogManager.getLogger(billing.stepDefinitions.api.RaterulesStepDefinitions.class);
    Raterulesservices rateRulesServices;
    private ScenarioContextWithObject scenarioContextWithObject;

    private ScenarioContext scenarioContext;
    private SoftAssert softAssert;

    public RaterulesStepDefinitions(ScenarioContextWithObject scenarioContextWithObject, ScenarioContext scenarioContext) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.scenarioContext = scenarioContext;
        this.rateRulesServices = new Raterulesservices(scenarioContextWithObject, scenarioContext);
        this.softAssert = SoftAssertContainer.getInstance();
    }

    @Given("the billing method is {string}")
    public void the_billing_method_is(String string) {
        rateRulesServices.selectTheBillingMethod(string);
    }

    @Given("the following rate rule to delete:")
    public void the_following_rate_rule_to_delete(DataTable dataTable) {
        rateRulesServices.getTheBillingMethodAndId(dataTable);
    }

    @Then("the response should match the expected {string} response")
    public void the_response_should_match_the_expected_response(String string) {
        rateRulesServices.validateTheResponse(string);
    }

    @Given("I have a valid request payload with <client>, <broker> and <rate>")
    public void i_have_a_valid_request(DataTable dataTable) {
        rateRulesServices.sendPostRequest(dataTable);
    }

    @When("I send a DELETE request to the rate rule endpoint")
    public void i_send_a_delete_request_to_the_rate_rule_endpoint() {
        rateRulesServices.sendDeleteRequest();
    }

    @Then("the response of deletion of rate rule status should be {int}")
    public void the_response_status_should_be_as_expected(int expectedStatus) {
        rateRulesServices.validateTheDeleteResponseStatus(expectedStatus);
    }

    @Given("the following rate rule to fetch:")
    public void the_following_rate_rule_to_fetch(DataTable dataTable) {
        rateRulesServices.getTheBillingMethodAndId(dataTable);

    }
    @When("I send a GET request to the rate rule endpoint")
    public void i_send_a_get_request_to_the_rate_rule_endpoint() {
        rateRulesServices.sendGetRequest();

    }


}

