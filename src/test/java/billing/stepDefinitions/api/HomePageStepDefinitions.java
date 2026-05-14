package billing.stepDefinitions.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContext;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.api.responses.get.GetAllOrders;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.lock.BuildSynchronization;
import org.billing.services.apiservices.HomePageServices;
import org.billing.services.apiservices.Raterulesservices;
import org.billing.services.apiservices.ViewOrdersServices;
import org.testng.asserts.SoftAssert;

import java.util.List;

public class HomePageStepDefinitions {
    private static final Logger logger = LogManager.getLogger(HomePageStepDefinitions.class);
    HomePageServices homePageServices;
    ViewOrdersServices viewOrdersServices;
    Raterulesservices raterulesservices;
    private ScenarioContextWithObject scenarioContextWithObject;

    private ScenarioContext scenarioContext;
    private SoftAssert softAssert;
    List<GetAllOrders> clientClassifiedResult;

    public HomePageStepDefinitions(ScenarioContextWithObject scenarioContextWithObject, ScenarioContext scenarioContext) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.scenarioContext = scenarioContext;
        this.homePageServices = new HomePageServices(scenarioContextWithObject, scenarioContext);
        this.viewOrdersServices = new ViewOrdersServices(scenarioContextWithObject, scenarioContext);
        this.softAssert = SoftAssertContainer.getInstance();
        this.raterulesservices = new Raterulesservices(scenarioContextWithObject, scenarioContext);
    }

    @Given("the system has orders data available via the ViewOrders API")
    public void the_system_has_orders_data_available_via_the_api() throws Exception {
        List<GetAllOrders> listOfAllOrders = viewOrdersServices.getAllPagedOrdersFromAPI();
        if ((listOfAllOrders.size() == 0)) {
            homePageServices.executeHomeRebuildAction();
            homePageServices.waitForBuildToBeSuccessfulForCurrentMonth();
        } else {
            logger.info("Total Orders retrieved from ViewOrders API: " + listOfAllOrders.size());
        }

    }

    @Given("I find an {string} and {string} Invoice template to Link to a template rules")
    public void i_find_an_invoice_template_to_link_to_a_template_rules(String billingMethod,String type) throws Exception {
        homePageServices.searchAndSelectInvoiceTemplateForRateRuleLinking(billingMethod,type);
    }

    @When("I link the Invoice template created to the template rules via the API")
    public void i_link_the_invoice_template_to_the_template_rules_via_the_api() {
        createATemplateRuleAndLinkToInvoiceTemplate();
    }

    private void createATemplateRuleAndLinkToInvoiceTemplate() {
        homePageServices.createATemplateRuleBasedOnOrderData();
        homePageServices.linkTemplateRuleToInvoiceTemplate();
    }

    @When("I retrieve orders and group them by broker, client, and other relevant fields")
    public void i_retrieve_orders_and_group_them_by_broker_client_and_other_relevant_fields() {
        viewOrdersServices.groupTheOrderByBrokerAndClient();
        viewOrdersServices.GroupingBySecurityType();
        if (viewOrdersServices.checkIftheToraRateIsAllSame()) {
            logger.info("All Tora Rates are same, cannot proceed with further grouping");
        } else {
            viewOrdersServices.furtherGroupingWithOtherField();
        }

    }

    @When("I apply the rate rule using broker, client, and other field details for the current month via the API")
    public void i_apply_the_rate_rule_using_broker_client_and_other_field_details_for_the_current_month_via_the_api() throws Exception {
        raterulesservices.handleSingleVolumeRateRule();
    }

    @When("I trigger the {string} process for the month via the API")
    public void i_trigger_the_process_for_the_month_via_the_api(String action) {
        homePageServices.executeHomeRebuildAction(action);
    }

    @When("I wait for the build to be successfully for current month via the API")
    public void i_wait_for_the_build_to_be_successfull() throws Exception {
        homePageServices.waitForBuildToBeSuccessfulForCurrentMonth();
    }

    @When("I wait for the build to be successfully for chosen month via the API")
    public void i_wait_for_the_build_to_be_successfullforChosenMonth() throws Exception {
        homePageServices.waitForBuildToBeSuccessfulForChosenMonth();
    }

    @Given("I check the template rules and make the created with highPriority")
    public void i_check_the_template_rules_and_make_the_created_with_high_priority() throws JsonProcessingException {
        homePageServices.lookForCreatedTemplateRuleAndMakeItHighPriority();
    }



    @Then("I retrieve the updated orders from the view orders API")
    public void i_retrieve_the_updated_orders_from_the_api(String string) {
        // Step 1: Fetch all orders
        List<GetAllOrders> listOfAllOrders = viewOrdersServices.getAllPagedOrdersFromAPI();
        scenarioContextWithObject.setData("FinalOrders", listOfAllOrders); // Initialize with all orders

        // Step 2: Broker & Client grouping
        if (viewOrdersServices.isGroupedByBroker() && viewOrdersServices.isGroupedByClient()) {
            viewOrdersServices.getOrdersByBrokerAndClient(listOfAllOrders);
        }

        // Step 3: Security Type grouping
        if (viewOrdersServices.isGroupedBySecurityCode()) {
            List<GetAllOrders> currentOrders = (List<GetAllOrders>) scenarioContextWithObject.getData("FinalOrders");
            viewOrdersServices.getOrdersBySecurityType(currentOrders);
        }

        // Step 4: Market grouping
        if (viewOrdersServices.isGroupedByMarket()) {
            List<GetAllOrders> currentOrders = (List<GetAllOrders>) scenarioContextWithObject.getData("FinalOrders");
            viewOrdersServices.getOrdersByMarket(currentOrders);
        }
    }

    @Then("validate the generated Invoice as per the linked template for the orders via the API")
    public void validate_the_generated_invoice_as_per_the_linked_template_for_the_orders_via_the_api() throws JsonProcessingException, InterruptedException {
        homePageServices.validateTheGeneratedInvoiceAsPerLinkedTemplate();
    }


@Then("I verify that the commission is applied as per the rate rule for each order")
public void i_verify_that_the_commission_is_applied_as_per_the_rate_rule_for_each_order() {
    viewOrdersServices.validateTheCommissionAfterRateRuleApplication();
}

    @When("we get the available brokers and clients details from the orders via the API")
    public void we_get_the_available_brokers_and_clients_details_from_the_orders_via_the_api() throws JsonProcessingException, InterruptedException {
        homePageServices.getTheLisOfBrokersAndClients();
    }

    @When("we select the first {string} with data and run the build for that month via the API")
    public void the_system_has_orders_data_list_in_the_home_page_and_we_select_the_first_fixed_rate_with_data(String orderType) throws Exception {
        synchronized (BuildSynchronization.BUILD_LOCK) {
            if (!BuildSynchronization.buildCompleted) {
                homePageServices.selectAndTriggerFirstBuildWithGivenOrderType(orderType);
                homePageServices.waitForBuildToBeSuccessfulForChosenMonth();
                BuildSynchronization.buildCompleted = true;
            } else {
                // Wait for the build to be completed by the other scenario
                homePageServices.selectAndTriggerFirstBuildWithGivenOrderType(orderType);
                homePageServices.waitForBuildToBeSuccessfulForChosenMonth();}
        }

    }

}
