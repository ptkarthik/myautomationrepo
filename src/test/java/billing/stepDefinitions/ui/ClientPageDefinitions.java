package billing.stepDefinitions.ui;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContext;

public class ClientPageDefinitions {

    private static final Logger logger = LogManager.getLogger(ClientPageDefinitions.class);
    private final ScenarioContext scenarioContext;

    public ClientPageDefinitions(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @When("The user Click to Add New Clients")
    public void the_user_click_to_add_new_clients() {
    }
    @Then("User validates the Add New Client Modal Details")
    public void user_validates_the_add_new_client_modal_details() {

    }
    @When("the user enters the following Client details")
    public void the_user_enters_the_following_client_details(io.cucumber.datatable.DataTable dataTable) {

    }
    @When("the user saves the client")
    public void the_user_saves_the_client() {
    }
    @Then("the new client is saved successfully")
    public void the_new_client_is_saved_successfully() {

    }
}
