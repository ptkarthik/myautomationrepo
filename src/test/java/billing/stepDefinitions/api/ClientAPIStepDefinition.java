package billing.stepDefinitions.api;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.services.apiservices.ClientServices;

import java.util.Map;

import static org.billing.api.APIUtils.APIAuthentication.postAuthorizationCode;

public class ClientAPIStepDefinition{
    private static final Logger logger = LogManager.getLogger(ClientAPIStepDefinition.class);
    private final ScenarioContextWithObject scenarioContextWithObject;
    private ClientServices clientServices;

    public ClientAPIStepDefinition(ScenarioContextWithObject scenarioContextWithObject) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.clientServices = new ClientServices(scenarioContextWithObject);
    }

    @Given("I fetch client details with following query parameters")
    public void i_fetch_client_details_with_following_query_parameters(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> clientDetailsMap = dataTable.asMap(String.class, String.class);
        clientServices.fetch_ClientDetails(clientDetailsMap, postAuthorizationCode());
    }

    @Given("user create client with following details")
    public void user_create_client_details_with_following_details(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> clientDetailsMap = dataTable.asMap(String.class, String.class);
        clientServices.createClient(clientDetailsMap, postAuthorizationCode());
    }

    @Then("I validate the client details")
    public void i_validate_the_client_details() {
        clientServices.validateClientDetails();
    }
}
