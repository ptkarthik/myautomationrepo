package billing.stepDefinitions.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.services.apiservices.CapRulesService;

import java.util.Map;

import static org.billing.api.APIUtils.APIAuthentication.postAuthorizationCode;

public class CapRulesApiStepDefinitions {

    private static final Logger logger = LogManager.getLogger(CapRulesApiStepDefinitions.class);
    private final ScenarioContextWithObject scenarioContextWithObject;
    private CapRulesService capRuleServices;

    public CapRulesApiStepDefinitions(ScenarioContextWithObject scenarioContextWithObject) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.capRuleServices = new CapRulesService(scenarioContextWithObject);
    }

    @Given("I send a POST request {string} to the CapRule endpoint with the following body:")
    public void sendPostRequestForCapRule(String apiEndPoint, DataTable dataTable) throws Exception {
        scenarioContextWithObject.setData("bearerToken", postAuthorizationCode());
        capRuleServices.createCapRule(dataTable.asMaps().get(0),
                scenarioContextWithObject.getData("bearerToken").toString(), apiEndPoint);

    }


    @Then("The response should confirm the CapRule has been successfully processed")
    public void the_response_should_confirm_the_cap_rule_has_been_successfully_processed() {
        try {
            capRuleServices.validateCapRulesDetails();
        } catch (Exception e) {
            logger.error("Error validating Cap Rule details: ", e);
            throw new RuntimeException("Cap Rule validation failed", e);
        }
    }

    @When("I increase the priority of the CapRule with endpoint {string}")
    public void i_increase_the_priority_of_the_cap_rule_with_endpoint(String endPoint) {
        capRuleServices.updatePriorityOfCapRule(endPoint, scenarioContextWithObject.getData("bearerToken").toString());
    }

    @Then("The response should confirm the priority of the CapRule is {string}")
    public void the_response_should_confirm_the_priority_of_the_cap_rule_is(String string) {
        try {
            capRuleServices.validatePriorityOfCapRule(string);
        } catch (Exception e) {
            logger.error("Error validating Cap Rule priority: ", e);
            throw new RuntimeException("Cap Rule priority validation failed", e);
        }
    }

    @When("I decrease the priority of the CapRule with endpoint {string}")
    public void i_decrease_the_priority_of_the_cap_rule_with_endpoint(String endPoint) {
        capRuleServices.updatePriorityOfCapRule(endPoint, scenarioContextWithObject.getData("bearerToken").toString());
    }

    @When("I send a POST request to fetch the CapRule with endpoint {string}")
    public void i_send_a_post_request_to_fetch_the_cap_rule_with_endpoint(String endPoint) throws JsonProcessingException {
        capRuleServices.fetchCapRule(endPoint, scenarioContextWithObject.getData("bearerToken").toString());
    }

    @When("I send a PUT request to the endpoint {string} by updating the clientName to {string}")
    public void i_send_a_put_request_to_the_endpoint_by_updating_the_client_name_to(String endPoint, String updatedClientName) throws JsonProcessingException {
        capRuleServices.updateCapRuleClientName(endPoint, updatedClientName, scenarioContextWithObject.getData("bearerToken").toString());

    }


    @When("I send a POST request to the endpoint {string} with path parameter {string} set to {string} and I attach the file {string} as form data")
    public void i_send_a_post_request_to_the_endpoint_with_path_parameter_set_to_and_i_attach_the_file_as_form_data(String endPoint, String pathParamName, String pathParamValue, String fileName) {
        scenarioContextWithObject.setData("bearerToken", postAuthorizationCode());
        capRuleServices.uploadCapRuleFile(endPoint, pathParamName, pathParamValue, fileName, scenarioContextWithObject.getData("bearerToken").toString());
    }

}
