package billing.stepDefinitions.api;

import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.utils.apiutilities.GenericUtils;
import org.testng.asserts.SoftAssert;

public class ApiBaseStepDefinition {
    public SoftAssert softAssert = new SoftAssert();
    public Response response;
    private static final Logger logger = LogManager.getLogger(ApiBaseStepDefinition.class);
    private ScenarioContextWithObject scenarioContextWithObject;
    private GenericUtils genericUtils;

    public ApiBaseStepDefinition(ScenarioContextWithObject scenarioContextWithObject) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.genericUtils = new GenericUtils(scenarioContextWithObject);
    }



    @When("I send a delete request to the endpoint {string}")
    public void sendDeleteRequest(String endpoint) {
        this.genericUtils.createDeleteRequest(endpoint, scenarioContextWithObject.getData("id").toString());
    }


}