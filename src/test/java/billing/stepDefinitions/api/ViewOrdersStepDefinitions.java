package billing.stepDefinitions.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContext;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.services.apiservices.HomePageServices;
import org.testng.asserts.SoftAssert;

public class ViewOrdersStepDefinitions {
    private static final Logger logger = LogManager.getLogger(ViewOrdersStepDefinitions.class);
    HomePageServices homePageServices;
    private ScenarioContextWithObject scenarioContextWithObject;

    private ScenarioContext scenarioContext;
    private SoftAssert softAssert;

    public ViewOrdersStepDefinitions(ScenarioContextWithObject scenarioContextWithObject, ScenarioContext scenarioContext) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.scenarioContext = scenarioContext;
        this.homePageServices = new HomePageServices(scenarioContextWithObject, scenarioContext);
        this.softAssert = SoftAssertContainer.getInstance();
    }

}
