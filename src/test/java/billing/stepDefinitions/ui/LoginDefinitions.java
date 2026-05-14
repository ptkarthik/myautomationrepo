package billing.stepDefinitions.ui;

import org.billing.Context.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.pages.LoginPage;


public class LoginDefinitions {
    private static final Logger logger = LogManager.getLogger(LoginDefinitions.class);
    private final ScenarioContext scenarioContext;

    public LoginDefinitions(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    LoginPage loginSteps = new LoginPage();

    @Given("The user is in the login page of Billing application")
    public void theUserIsInTheLoginPageOfBSPUATPage() {
    }

    @When("The user enters valid credentials as {string}, {string}")
    public void theUserEntersValidCredentials(String email, String password) {
        loginSteps.enterEmail(email);
        loginSteps.enterPassword(password);
        logger.info("The email {} has been inputted", email);
        logger.info("The password {} has been inputted", "********");
        loginSteps.clickLogIn();
        logger.debug("The email address and password has been inputted and log in button is clicked");
    }

}
