package billing.stepDefinitions.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.services.apiservices.TemplateRulesServices;

import static org.billing.api.APIUtils.APIAuthentication.postAuthorizationCode;

public class TemplateRulesApiStepDefinitions {

    private static final Logger logger = LogManager.getLogger(TemplateRulesApiStepDefinitions.class);
    private final ScenarioContextWithObject scenarioContextWithObject;
    private TemplateRulesServices templateRuleServices;

    public TemplateRulesApiStepDefinitions(ScenarioContextWithObject scenarioContextWithObject) throws Exception {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.templateRuleServices = new TemplateRulesServices(scenarioContextWithObject);
    }

    @Given("I send a POST request {string} to the TemplateRule endpoint with the following body:")
    public void sendPostRequestForCapRule(String apiEndPoint, DataTable dataTable) throws JsonProcessingException {
        scenarioContextWithObject.setData("bearerToken", postAuthorizationCode());
        templateRuleServices.createTemplateRule(dataTable.asMaps().get(0),
                scenarioContextWithObject.getData("bearerToken").toString(), apiEndPoint);

    }

    @Given("I send a POST request to the create TemplateRule without data table")
    public void sendPostRequestForCapRuleWithOutDataTable() throws JsonProcessingException {
        templateRuleServices.createTemplateRuleWithScenarioContextData();

    }

    @Given("I send a GET request {string} to get max priority endpoint")
    public void sendGetRequestForMaxPriority(String apiEndPoint) throws JsonProcessingException {
        scenarioContextWithObject.setData("bearerToken", postAuthorizationCode());
        templateRuleServices.getMaxPriority(
                scenarioContextWithObject.getData("bearerToken").toString(), apiEndPoint);

    }


    @Then("The response should confirm the TemplateRule has been successfully processed")
    public void the_response_should_confirm_the_template_rule_has_been_successfully_processed() {
        try {
            templateRuleServices.validateTemplateRulesDetails();
        } catch (Exception e) {
            logger.error("Error validating Cap Rule details: ", e);
            throw new RuntimeException("Template Rule validation failed", e);
        }
    }

//    I send a GET request "/api/template-rules/BROKER/VOLUME/get-max-priority" to get max priority endpoint
}
