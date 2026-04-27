package billing.stepDefinitions.ui;

import billing.stepDefinitions.api.ApiBaseStepDefinition;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContext;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.services.ui.FxSwapRateservices;
import org.testng.asserts.SoftAssert;

import java.sql.SQLException;
import java.util.Map;

public class FxSwapDefinitions{
    private ScenarioContext scenarioContext;
    private final FxSwapRateservices fxSwapRateservices;
    private SoftAssert softAssert;
    // Constructor to initialize with ScenarioContext
    public FxSwapDefinitions(ScenarioContext scenarioContext) {
        this.fxSwapRateservices = new FxSwapRateservices(scenarioContext); // Pass ScenarioContext
        softAssert = SoftAssertContainer.getInstance();
    }

    private static final Logger logger = LogManager.getLogger(FxSwapDefinitions.class);

    @Given("^I navigate to the \"FX Swap Rates\" page$")
    public void navigateToFXSwapRatesPage() {
        fxSwapRateservices.navigateToFxSwapRatesPage();
        logger.info("Navigated to FX Swap Rates page.");
    }

    @And("^the \"FX Swap Rates\" page loads successfully$")
    public void verifyPageLoad() {
        fxSwapRateservices.validatetheFxSwapTableElements();
    }

    @Given("^I open the \"Add New Rate Rule\" modal$")
    public void openAddNewRateRuleModal() {
        fxSwapRateservices.openAddNewruleBox();
        logger.info("Add New Rate Rule modal opened successfully.");
        softAssert.assertAll();
    }

    @When("^I enter valid values in the following fields:$")
    public void enterValidValuesInFields(Map<String, String> fields) {
        fxSwapRateservices.fillAddRateForm(fields);
        logger.info("Entered values into the Add New Rate Rule Modal: {}", fields);
    }

    @Then("^validate If the entries provided are existing already and save$")
    public void verifyNewEntryInTable() throws SQLException {
        fxSwapRateservices.validateIfAddedDataStored();
    }


    @Given("^I locate the row with \"Tenor\" as \"([^\"]*)\" and \"Day Count\" as \"([^\"]*)\"$")
    public void locateRow(String tenor, String dayCount) throws SQLException {
        fxSwapRateservices.findRowByTenorAndDayCount(tenor, dayCount);
    }

    @When("I click the {string} icon for that row")
    public void i_click_the_icon_for_that_row(String data) {
        // Write code here that turns the phrase above into concrete actions
        fxSwapRateservices.locateTheExactEditOrDelete(data);
    }

    @When("^I update the \"([^\"]*)\" field to \"([^\"]*)\"$")
    public void updateField(String fieldName, String fieldValue) {
        fxSwapRateservices.updateField(fieldName, fieldValue);
        logger.info("Field {} updated to {}", fieldName, fieldValue);
    }

    @When("I click {string}")
    public void i_click(String string) {
        if (string.equalsIgnoreCase("Save"))
            fxSwapRateservices.clickSave();
    }

    @Then("the updated row reflects the following values:")
    public void the_updated_row_reflects_the_following_values(DataTable dataTable) throws SQLException, InterruptedException {
        fxSwapRateservices.validatetheUpdatedData(dataTable);
    }

    @When("I confirm the deletion")
    public void i_confirm_the_deletion() {
        fxSwapRateservices.clickYesOfDelete();
    }

    @Then("the row no longer exists in the FX Swap Rates table")
    public void the_row_no_longer_exists_in_the_fx_swap_rates_table() throws SQLException, InterruptedException {
        fxSwapRateservices.validateDeletionOfARule();

    }


}
