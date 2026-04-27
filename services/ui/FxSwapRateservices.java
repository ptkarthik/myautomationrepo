package org.billing.services.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContext;
import org.billing.TestConstants;
import org.billing.api.APIUtils.APIAuthentication;
import org.billing.api.endpoints.EndPoints;
import org.billing.api.payloads.request.post.FxSwapRatePostRequest;
import org.billing.api.payloads.request.put.FxSwapRatePutRequest;
import org.billing.api.responses.post.FxSwapRatePostResponse;
import org.billing.api.responses.put.FxswapRatePutResponse;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.dbclasses.FxSwapRateDBData;
import org.billing.dbconfig.FXSwapRateDBconfig;
import org.billing.pages.FXSwapRatesPage;
import org.billing.utils.apiutilities.APIUtils;
import org.billing.utils.general.GeneralUtlis;
import org.billing.utils.propconfig.PropertiesReader;
import org.billing.validation.GeneralValidations;
import org.billing.validation.ValidationUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.sql.SQLException;
import java.util.*;


public class FxSwapRateservices extends BaseServices {
    private static final Logger logger = LogManager.getLogger(FxSwapRateservices.class);
    // Mock data or instance of the page under test
    FXSwapRatesPage fxSwapRatesPage = new FXSwapRatesPage();
    GeneralValidations generalValidations = new GeneralValidations();
    private final ScenarioContext scenarioContext;
    ObjectMapper objectMapper = new ObjectMapper();
    private Response response;
    private SoftAssert softAssert;
    FxSwapRatePostResponse fxSwapRatePostResponse;
    JsonPath jsonPath;

    FXSwapRateDBconfig fxSwapRateDBconfig = new FXSwapRateDBconfig();

    public FxSwapRateservices(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        softAssert = SoftAssertContainer.getInstance();
    }


    public void navigateToFxSwapRatesPage() {
        ValidationUtils.waitForClickable(fxSwapRatesPage.getHomePageText());
        GeneralUtlis.clickUsingJS(fxSwapRatesPage.getFxSwapRatePage());
    }

    public void validatetheFxSwapTableElements() {
        // List of WebElements that we want to validate
        List<WebElement> elementsToValidate = List.of(
                fxSwapRatesPage.getMainTable(),      // Page header: "Fx Swap Rates"
                fxSwapRatesPage.getAddNewRateRuleButton(),  // Button: "Add New Rate Rule"
                fxSwapRatesPage.getFxSwapRatesHeader(),             // The main table element
                fxSwapRatesPage.getPaginationfirstButton(),    // Table column header: "Action"
                fxSwapRatesPage.getPaginationPrevButton(),     // Table column header: "Range"
                fxSwapRatesPage.getPaginationLastButton(),      // Table column header: "Rate"
                fxSwapRatesPage.getPaginationNextButton(),  // Pagination button: "<<"
                fxSwapRatesPage.getDayCountHeader(),
                fxSwapRatesPage.getActionColumnHeader(),
                fxSwapRatesPage.getTenorHeader(),
                fxSwapRatesPage.getRevenueShareHeader()
                // Pagination button: ">>"
        );
        // Loop over each WebElement to validate its existence
        for (WebElement element : elementsToValidate) {
            generalValidations.validateDisplayed(element, element.getText());
        }
        logger.info("[INFO] All required WebElements on the Fx Swap Rates page are present and visible.");
    }

    public void openAddNewruleBox() {
        GeneralUtlis.clickUsingJS(fxSwapRatesPage.getAddNewRateRuleButton());
        validateNewrulebox();
    }

    private void validateNewrulebox() {
        generalValidations.validateDisplayed(fxSwapRatesPage.getAddRateRuleMainTable(), "Add rule Main Table");
        generalValidations.validateText(fxSwapRatesPage.getAddNewRateRuleTextButton(),
                "Add new Rate Rule");
        generalValidations.validateText(fxSwapRatesPage.getTenorTextOnAddNewRuleBox(),
                "Tenor");
        generalValidations.validateText(fxSwapRatesPage.getLowerBoundTextOnAddNewRuleBox(),
                "Day Count Lower Bound");
        generalValidations.validateText(fxSwapRatesPage.getUpperBoundTextOnAddNewRuleBox(),
                "Day Count Upper Bound");
        generalValidations.validateText(fxSwapRatesPage.getRevenueShareTextOnAddNewRuleBox(),
                "Revenue Share");
        generalValidations.validateText(fxSwapRatesPage.getMaxUpperBoundTextOnAddNewRuleBox(),
                "Max Upper Bound Limit");
        generalValidations.validateText(fxSwapRatesPage.getMaxUpperBoundTextOnAddNewRuleBox(),
                "Max Upper Bound Limit");
        generalValidations.validateText(fxSwapRatesPage.getSaveOnAddNewRuleBox(),
                "Save");
        generalValidations.validateText(fxSwapRatesPage.getCancelOnAddNewRuleBox(),
                "Cancel");
        generalValidations.validateCheckboxUnselectedByDefault(fxSwapRatesPage.getMaxUpperBoundCheckBoxOnAddNewRuleBox(),
                " CheckBox");
        generalValidations.validateEnabled(fxSwapRatesPage.getTenorInputBox(), "Tenor Input Box");
        generalValidations.validateEnabled(fxSwapRatesPage.getLowerBoundInputBox(), "Lower InputBox");
        generalValidations.validateEnabled(fxSwapRatesPage.getUpperBoundInputBox(), "Upper Input Box");
        generalValidations.validateEnabled(fxSwapRatesPage.getRevenueShareInputBox(), "Revenue share Input Box");
        generalValidations.validateDisplayed(fxSwapRatesPage.getAddNewRateRuleCloseButton(), "Close Button");
    }

    public void fillAddRateForm(Map<String, String> fields) {
        scenarioContext.setData(TestConstants.TENOR, fields.get(TestConstants.TENOR));
        scenarioContext.setData(TestConstants.LOWERDAYCOUNT, fields.get(TestConstants.LOWERDAYCOUNT));
        scenarioContext.setData(TestConstants.UPPERDAYCOUNT, fields.get(TestConstants.UPPERDAYCOUNT));
        scenarioContext.setData(TestConstants.REVENUE_SHARE, fields.get(TestConstants.REVENUE_SHARE));
        scenarioContext.setData(TestConstants.MAX_ENABLED, fields.get(TestConstants.MAX_ENABLED));
        addInputsForCreatingARateRule(scenarioContext);
    }

    private void addInputsForCreatingARateRule(ScenarioContext scenarioContext) {
        fxSwapRatesPage.getTenorInputBox().sendKeys(scenarioContext.getData(TestConstants.TENOR));
        fxSwapRatesPage.getUpperBoundInputBox().sendKeys(scenarioContext.getData(TestConstants.UPPERDAYCOUNT));
        fxSwapRatesPage.getLowerBoundInputBox().sendKeys(scenarioContext.getData(TestConstants.LOWERDAYCOUNT));
        fxSwapRatesPage.getRevenueShareInputBox().sendKeys(scenarioContext.getData(TestConstants.REVENUE_SHARE));
    }

    public void clickSave() {
        GeneralUtlis.clickUsingJS(fxSwapRatesPage.getSaveOnAddNewRuleBox());
    }

    public void clickYesOfDelete() {
        GeneralUtlis.clickUsingJS(fxSwapRatesPage.getYesButtonOfDeleteFxSwap());
    }

    public void clickCancel() {
        GeneralUtlis.clickUsingJS(fxSwapRatesPage.getCancelOnAddNewRuleBox());
    }

    public List<FxSwapRateDBData> fetchDBDetails() throws SQLException {
        List<FxSwapRateDBData> fxSwapRateDBData = fxSwapRateDBconfig.fetchDbData();
        return fxSwapRateDBData;
    }

    public void validateIfAddedDataStored() throws SQLException {
        fxSwapRateDBconfig.setUpDbConfig();
        List<FxSwapRateDBData> fxSwapRateDBData = fxSwapRateDBconfig.fetchDbData();
        Map<String, Object> result = fxSwapRateDBconfig.validateInput(storeUIInput(scenarioContext.
                        getData(TestConstants.TENOR),
                scenarioContext.getData(TestConstants.LOWERDAYCOUNT),
                scenarioContext.getData(TestConstants.UPPERDAYCOUNT),
                scenarioContext.getData(TestConstants.MAX_ENABLED),
                scenarioContext.getData(TestConstants.REVENUE_SHARE)), fxSwapRateDBData);
        result.forEach((key, value) -> System.out.println(key + ": " + value));
        boolean isValid = (boolean) result.getOrDefault("IsValid", false);
        if (!isValid) {
            fxSwapRatesPage.getSaveOnAddNewRuleBox().click();
            validateTheErrorTextOnNewRateRuleBox(result);
        } else {
            fxSwapRatesPage.getSaveOnAddNewRuleBox().click();
        }
    }

    private void validateTheErrorTextOnNewRateRuleBox(Map<String, Object> result) {
        HashSet<String> tenorsSet;
        String tenorText = "";
        List<String> overlappingRanges;
        tenorsSet = (HashSet<String>) result.get("Matching Tenors");
        overlappingRanges = (List<String>) result.get("Overlapping Day Ranges");
        if (tenorsSet != null) {
            for (String textOFTenor : tenorsSet) {
                tenorText = textOFTenor;
            }
            if (overlappingRanges != null) {
                generalValidations.validateTextContains(fxSwapRatesPage.getInvalidRateRuleTextTwo(), tenorText,
                        "Invalid Tenor Text Details");

            } else {
                generalValidations.validateTextContains(fxSwapRatesPage.getInvalidRateRuleTextTwo(), tenorText,
                        "Invalid Tenor Text Details");
            }
        }
        if (overlappingRanges != null) {
            for (String overLappingText : overlappingRanges) {
                generalValidations.validateTextContains(fxSwapRatesPage.getInvalidRateRuleTextTwo(),
                        overLappingText,
                        "Invalid OverLapping DayCount Details");
            }
        }
    }

    public Map<String, String> storeUIInput(String tenor, String dayCountLower, String dayCountUpper,
                                            String maxUpperBound, String revenueShare) {
        Map<String, String> inputData = new HashMap<>();
        inputData.put("Tenor", tenor);
        inputData.put("Day Count Lower", dayCountLower);
        inputData.put("Day Count Upper", dayCountUpper);
        inputData.put("Max Upper Bound", maxUpperBound);
        inputData.put("Revenue Share", revenueShare);
        return inputData;
    }

    public void findRowByTenorAndDayCount(String tenor, String dayCount) throws SQLException {
        scenarioContext.setData(TestConstants.TENOR, tenor);
        scenarioContext.setData(TestConstants.DAY_COUNT, dayCount);
        List<FxSwapRateDBData> fxSwapRateDBData = fxSwapRateDBconfig.fetchDbData();
        // Split Day Count into lower and upper bounds (e.g., "5 - 10" → lower=5, upper=10)
        String[] dayCountParts = scenarioContext.getData(TestConstants.DAY_COUNT).split(" - ");
        int lowerBound = Integer.parseInt(dayCountParts[0].trim());
        int upperBound = Integer.parseInt(dayCountParts[1].trim());
        // Find a matching row in the DB with the same tenor and bounds
        Optional<FxSwapRateDBData> matchingRow = fxSwapRateDBData.stream()
                .filter(row -> row.getTenor().equalsIgnoreCase(scenarioContext.getData(TestConstants.TENOR)) &&
                        row.getLowerBound() == lowerBound &&
                        row.getUpperBound() == upperBound)
                .findFirst();
        logger.debug("matching row validation is being performed");
        logger.info("matching row validation is being performed");
        Assert.assertTrue(matchingRow.isPresent(), "The Matching " +
                "row which contains both " + tenor + " " + "and" + " " + dayCount + " is not found ");
    }

    public void locateTheExactEditOrDelete(String data) {
        boolean next = ValidationUtils.isElementDisabled(fxSwapRatesPage.getPaginationNextButton());
        try {
            do {
                List<WebElement> listOfFxRows = fxSwapRatesPage.getMainTableRowsList().
                        findElements(By.xpath(TestConstants.TR));
                for (WebElement row : listOfFxRows) {
                    List<WebElement> columnsOfTheRow = row.findElements(By.xpath(TestConstants.TD));
                    for (WebElement colmumn : columnsOfTheRow) {
                        if (colmumn.getText().equalsIgnoreCase(scenarioContext.getData(TestConstants.DAY_COUNT))) {
                            if (data.equalsIgnoreCase("edit")) {
                                row.findElement(By.xpath(".//a[@title='Edit Rate Rule']")).click();
                            } else {
                                row.findElement(By.xpath(".//a[@title='Delete Rate Rule']")).click();
                            }
                        }
                    }
                }
            }
            while (!next);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    public void updateField(String fieldName, String fieldValue) {
        if (fieldName.equalsIgnoreCase(TestConstants.REVENUE_SHARE)) {
            fxSwapRatesPage.getRevenueShareInputBox().clear();
            fxSwapRatesPage.getRevenueShareInputBox().sendKeys(fieldValue);
        }
    }

    public void validatetheUpdatedData(DataTable dataTable) throws SQLException, InterruptedException {
        logger.info("Starting validation of updated data...");

        List<FxSwapRateDBData> fxSwapRateDBData;
        boolean rowUpdated = false;
        int retryCount = 0;
        final int MAX_RETRIES = 5; // Maximum number of retries
        final int WAIT_TIME = 2000; // Wait time between retries in milliseconds (2 seconds)
        final double TOLERANCE = 0.000001;

        // Split Day Count into lower and upper bounds (e.g., "5 - 10")
        String[] dayCountParts = dataTable.asMaps().get(0).get(TestConstants.DAY_COUNT).split(" - ");
        int lowerBound = Integer.parseInt(dayCountParts[0].trim());
        int upperBound = Integer.parseInt(dayCountParts[1].trim());
        double expectedRevenueShare = Double.parseDouble(dataTable.asMaps().get(0).get(TestConstants.REVENUE_SHARE));
        String expectedTenor = dataTable.asMaps().get(0).get(TestConstants.TENOR);

        logger.info("Expected Validation Criteria:");
        logger.info("- Tenor: {}", expectedTenor);
        logger.info("- Day Count: {} to {} (Lower Bound: {}, Upper Bound: {})", dayCountParts[0], dayCountParts[1], lowerBound, upperBound);
        logger.info("- Revenue Share: {}", expectedRevenueShare);

        // Retry until row is found or retries are exhausted
        do {
            logger.info("Fetching data from the database (Attempt {}/{})", retryCount + 1, MAX_RETRIES);

            // Fetch database rows
            fxSwapRateDBData = fxSwapRateDBconfig.fetchDbData();
            logger.info("Fetched {} rows from the database for comparison.", fxSwapRateDBData.size());

            // Check if the updated row exists in the database
            rowUpdated = fxSwapRateDBData.stream()
                    .anyMatch(row -> row.getTenor().equalsIgnoreCase(expectedTenor)
                            && row.getLowerBound() == lowerBound
                            && row.getUpperBound() == upperBound
                            && Math.abs(row.getRevenueShare() - expectedRevenueShare) < TOLERANCE);

            if (rowUpdated) {
                logger.info("Validation passed! Matching row found for Tenor='{}', Day Count='{} - {}', Revenue Share='{}'",
                        expectedTenor, lowerBound, upperBound, expectedRevenueShare);
                break; // Exit loop if the row is updated
            }

            // Wait before retrying
            Thread.sleep(WAIT_TIME);
            retryCount++;

        } while (!rowUpdated && retryCount < MAX_RETRIES);

        // If the row is still not updated after retries, throw an assertion error
        Assert.assertTrue(rowUpdated, String.format(
                "No matching row found in DB after %d attempts with Revenue Share='%s' for Tenor='%s' and Day Count='%s'",
                MAX_RETRIES,
                expectedRevenueShare,
                expectedTenor,
                dataTable.asMaps().get(0).get(TestConstants.DAY_COUNT)
        ));
    }

    public void validateDeletionOfARule() throws SQLException, InterruptedException {
        logger.info("Starting validation of deleted data...");

        List<FxSwapRateDBData> fxSwapRateDBData;
        boolean rowExists = true; // Set to true initially, assume the row exists
        int retryCount = 0;
        final int MAX_RETRIES = 5; // Maximum number of retries
        final int WAIT_TIME = 2000; // Wait time between retries in milliseconds (2 seconds)

        // Split Day Count into lower and upper bounds (e.g., "5 - 10")
        String[] dayCountParts = scenarioContext.getData(TestConstants.DAY_COUNT).split(" - ");
        int lowerBound = Integer.parseInt(dayCountParts[0].trim());
        int upperBound = Integer.parseInt(dayCountParts[1].trim());
        String expectedTenor = scenarioContext.getData(TestConstants.TENOR);

        logger.info("Expected Deletion Check Criteria:");
        logger.info("- Tenor: {}", expectedTenor);
        logger.info("- Day Count: {} to {} (Lower Bound: {}, Upper Bound: {})", dayCountParts[0], dayCountParts[1], lowerBound, upperBound);

        // Retry until row no longer exists or retries are exhausted
        do {
            logger.info("Fetching data from the database for deleted validation (Attempt {}/{})", retryCount + 1, MAX_RETRIES);

            // Fetch database rows
            fxSwapRateDBData = fxSwapRateDBconfig.fetchDbData();
            logger.info("Fetched {} rows from the database for comparison.", fxSwapRateDBData.size());

            // Check if any row still exists in the database
            rowExists = fxSwapRateDBData.stream()
                    .anyMatch(row -> row.getTenor().equalsIgnoreCase(expectedTenor)
                            && row.getLowerBound() == lowerBound
                            && row.getUpperBound() == upperBound);

            if (!rowExists) {
                logger.info("Validation passed! No matching row found for Tenor='{}', Day Count='{} - {}', Revenue Share='{}'",
                        expectedTenor, lowerBound, upperBound);
                break; // Exit loop if the row is deleted
            }

            // Wait before retrying
            Thread.sleep(WAIT_TIME);
            retryCount++;

        } while (rowExists && retryCount < MAX_RETRIES);

        // If the row is still not deleted after retries, throw an assertion error
        Assert.assertFalse(rowExists, String.format(
                "Row still exists in DB after %d attempts with Tenor='%s', and Day Count='%s'",
                MAX_RETRIES,
                expectedTenor,
                scenarioContext.getData(TestConstants.DAY_COUNT)
        ));
    }

    public FxSwapRatePostRequest createAFxSwapPostRequest(DataTable dataTable) throws SQLException {
        // Log the received data table
        logger.info("Received DataTable: {}", dataTable.asMaps());
      //  fxSwapRateDBconfig.clearFxSwapRateDB();

        // Retrieve and log each value from the DataTable
        String tenor = dataTable.asMaps().get(0).get("tenor").trim();
        String lowerBoundInDays = dataTable.asMaps().get(0).get("lowerBoundInDays");
        String upperBoundInDays = dataTable.asMaps().get(0).get("upperBoundInDays");
        String revenueShare = dataTable.asMaps().get(0).get("revenueShare");
        String maxUpperBound = dataTable.asMaps().get(0).get("maxUpperBound");

        logger.info("Extracted values from DataTable - Tenor: {}, LowerBoundInDays: {}, UpperBoundInDays: {}, RevenueShare: {}",
                tenor, lowerBoundInDays, upperBoundInDays, revenueShare);

        // Store data in the scenario context
        scenarioContext.setData(TestConstants.TENOR, tenor);
        scenarioContext.setData(TestConstants.LOWERDAYCOUNT, lowerBoundInDays);
        scenarioContext.setData(TestConstants.UPPERDAYCOUNT, upperBoundInDays);
        scenarioContext.setData(TestConstants.REVENUE_SHARE, revenueShare);
        scenarioContext.setData(TestConstants.MAXUPPERBOUND, maxUpperBound);

        // Log scenario context state
        logger.info("Scenario Context State - LowerDayCount: {}, UpperDayCount: {}, Tenor: {}, RevenueShare: {}",
                scenarioContext.getData(TestConstants.LOWERDAYCOUNT),
                scenarioContext.getData(TestConstants.UPPERDAYCOUNT),
                scenarioContext.getData(TestConstants.TENOR),
                scenarioContext.getData(TestConstants.REVENUE_SHARE));

        // Create FxSwapRatePostRequest object with values from scenario context
        FxSwapRatePostRequest fxSwapRatePostRequest = new FxSwapRatePostRequest(
                scenarioContext.getData(TestConstants.LOWERDAYCOUNT),
                scenarioContext.getData(TestConstants.UPPERDAYCOUNT),
                scenarioContext.getData(TestConstants.TENOR),
                scenarioContext.getData(TestConstants.REVENUE_SHARE),
                scenarioContext.getData(TestConstants.MAXUPPERBOUND));
        ;

        // Log the created request object
        logger.info("Created FxSwapRatePostRequest: {}", fxSwapRatePostRequest);

        return fxSwapRatePostRequest;
    }

    public FxSwapRatePutRequest createAFxSwapPutRequest(DataTable dataTable) {
        // Log the received data table
        logger.info("Received DataTable: {}", dataTable.asMaps());

        // Retrieve and log each value from the DataTable
        String tenor = dataTable.asMaps().get(0).get("tenor").trim();
        String lowerBoundInDays = dataTable.asMaps().get(0).get("lowerBoundInDays");
        String upperBoundInDays = dataTable.asMaps().get(0).get("upperBoundInDays");
        String revenueShare = dataTable.asMaps().get(0).get("revenueShare");

        logger.info("Extracted values from DataTable - Tenor: {}, LowerBoundInDays: {}, UpperBoundInDays: {}, RevenueShare: {}",
                tenor, lowerBoundInDays, upperBoundInDays, revenueShare);

        // Store data in the scenario context
        scenarioContext.setData(TestConstants.TENOR, tenor);
        scenarioContext.setData(TestConstants.LOWERDAYCOUNT, lowerBoundInDays);
        scenarioContext.setData(TestConstants.UPPERDAYCOUNT, upperBoundInDays);
        scenarioContext.setData(TestConstants.REVENUE_SHARE, revenueShare);

        // Log scenario context state
        logger.info("Scenario Context State - LowerDayCount: {}, UpperDayCount: {}, Tenor: {}, RevenueShare: {}",
                scenarioContext.getData(TestConstants.LOWERDAYCOUNT),
                scenarioContext.getData(TestConstants.UPPERDAYCOUNT),
                scenarioContext.getData(TestConstants.TENOR),
                scenarioContext.getData(TestConstants.REVENUE_SHARE));

        // Create FxSwapRatePostRequest object with values from scenario context
        FxSwapRatePutRequest fxSwapRatePutRequest = new FxSwapRatePutRequest(
                Integer.valueOf(scenarioContext.getData(TestConstants.FXSWAPRATEID)),
                scenarioContext.getData(TestConstants.LOWERDAYCOUNT),
                scenarioContext.getData(TestConstants.UPPERDAYCOUNT),
                scenarioContext.getData(TestConstants.TENOR),
                scenarioContext.getData(TestConstants.REVENUE_SHARE)
        );

        // Log the created request object
        logger.info("Created FxSwapRatePostRequest: {}", fxSwapRatePutRequest);

        return fxSwapRatePutRequest;
    }

    public void hitEndPointUsingCreatedPostReq(FxSwapRatePostRequest fxSwapRatePostRequest) throws JsonProcessingException {
        logger.info("Starting API call to Endpoint: {}", EndPoints.POST_FXSWAPRATE); // Log the endpoint we're hitting

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));
        logger.info("Authorization Token Generated: {}", authToken); // Log the token details (optional for debugging)

        // Serialize the payload
        String payload = objectMapper.writeValueAsString(fxSwapRatePostRequest);
        logger.info("Serialized Payload: {}", payload); // Log the JSON payload being sent to the API

        // Log the headers list
        logger.info("Headers: {}", headers);

        // Perform the POST request
        logger.info("Initiating POST request...");
        response = APIUtils.postWithAuth(
                PropertiesReader.getProperty("baseUrl"),
                EndPoints.POST_FXSWAPRATE,
                payload,
                headers
        );

        System.out.println(response.asString());

        // Deserialize the response JSON into a Java object (FxSwapRatePostResponse)
        fxSwapRatePostResponse = objectMapper.readValue(response.asString(), FxSwapRatePostResponse.class);

        // Log the response after the API call
        if (response != null) {
            logger.info("API Response: {}", response.getBody()); // Log the response body
            logger.info("Response Status Code: {}", response.getStatusCode()); // Log the status code
        } else {
            logger.error("API call failed. Response is null.");
        }
    }

    public void hitEndPointUsingJsonPathPostReq(FxSwapRatePostRequest fxSwapRatePostRequest) throws JsonProcessingException {
        logger.info("Starting API call to Endpoint: {}", EndPoints.POST_FXSWAPRATE); // Log the endpoint we're hitting

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));
        logger.info("Authorization Token Generated: {}", authToken); // Log the token details (optional for debugging)

        // Serialize the payload
        String payload = objectMapper.writeValueAsString(fxSwapRatePostRequest);
        logger.info("Serialized Payload: {}", payload); // Log the JSON payload being sent to the API

        // Log the headers list
        logger.info("Headers: {}", headers);

        // Perform the POST request
        logger.info("Initiating POST request...");
        response = APIUtils.postWithAuth(
                PropertiesReader.getProperty("baseUrl"),
                EndPoints.POST_FXSWAPRATE,
                payload,
                headers
        );
        jsonPath = new JsonPath(response.asString());
        // Log the response after the API call
        if (response != null) {
            logger.info("API Response: {}", response.getBody()); // Log the response body
            logger.info("Response Status Code: {}", response.getStatusCode()); // Log the status code
        } else {
            logger.error("API call failed. Response is null.");
        }
    }

    public void hitEndPointUsingDelReq() throws JsonProcessingException {
        // Start logging the API call
        logger.info("Starting DELETE request to Endpoint: {}", EndPoints.DELETE_FXSWAPRATE);

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode(); // Get authorization token
        headers.add(new Header("Content-Type", "application/json")); // Add Content-Type header
        headers.add(new Header("Authorization", "Bearer " + authToken)); // Add Authorization header

        // Log detailed header information
        logger.info("Authorization Token Generated: {}", authToken);
        logger.info("Headers: {}", headers);

        // Constructing the DELETE endpoint dynamically using scenario context
        String endpoint = EndPoints.DELETE_FXSWAPRATE + "/" + scenarioContext.getData(TestConstants.FXSWAPRATEID);
        logger.info("DELETE Endpoint: {}", endpoint);

        // Making the DELETE API call
        logger.info("Initiating DELETE request...");
        response = APIUtils.deleteWithAuth(
                PropertiesReader.getProperty("baseUrl"), // Base URL from property
                endpoint, // DELETE Endpoint
                headers // Headers
        );

        // Log the returned status code
        int statusCode = response.getStatusCode();
        logger.info("Response Status Code: {}", statusCode);

        // Check if the response body is empty
        if (response.asString().isEmpty()) {
            logger.info("Response Body: EMPTY (The server returned no content)."); // Log if body is empty
        } else {
            logger.info("Response Body: {}", response.asString()); // Log the response body if present
        }

        logger.info("DELETE request execution completed successfully.");
    }

    public void hitEndPointUsingCreatedPutReq(FxSwapRatePutRequest fxSwapRatePutRequest) throws JsonProcessingException {
        logger.info("Starting API call to Endpoint: {}", EndPoints.POST_FXSWAPRATE); // Log the endpoint we're hitting

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));
        logger.info("Authorization Token Generated: {}", authToken); // Log the token details (optional for debugging)

        // Serialize the payload
        String payload = objectMapper.writeValueAsString(fxSwapRatePutRequest);
        logger.info("Serialized Payload: {}", payload); // Log the JSON payload being sent to the API

        // Log the headers list
        logger.info("Headers: {}", headers);

        // Perform the POST request
        logger.info("Initiating PUT request...");
        response = APIUtils.putWithAuth(
                PropertiesReader.getProperty("baseUrl"),
                EndPoints.PUT_FXSWAPRATE + "/" + scenarioContext.getData(TestConstants.FXSWAPRATEID),
                payload,
                headers
        );

        // Log the response after the API call
        if (response != null) {
            logger.info("API Response: {}", response.getBody()); // Log the response body
            logger.info("Response Status Code: {}", response.getStatusCode()); // Log the status code
        } else {
            logger.error("API call failed. Response is null.");
        }
    }

    public void validateApiStatusCode(Integer int1) throws JsonProcessingException {
        try {
            // Log the raw API response string
            logger.info("Raw API Response String: {}", response.asString());

            // Log the deserialized response object
            logger.info("Deserialized Response: {}", fxSwapRatePostResponse);

            // Assert the API status code is as expected
            logger.info("Validating API Status Code: Expected = {}, Actual = {}",
                    int1, response.getStatusCode());
            softAssert.assertEquals(
                    Integer.valueOf(response.getStatusCode()),
                    int1,
                    "The Data is not as requested"
            );

            // Log success if validation passes (Note: softAssert does not fail immediately, so you may need to call softAssert.assertAll later in your test)
            logger.info("API status code validation passed!");
        } catch (Exception e) {
            // Catch all other exceptions to log unexpected errors
            logger.error("Unexpected error occurred: {}", e.getMessage(), e);
            throw e;
        }
    }


    public void validateFxResponseUniqueIDReceived() throws SQLException {
        try {
            // Log the start of DB configuration
            logger.info("Starting database configuration setup...");
            fxSwapRateDBconfig.setUpDbConfig();
            logger.info("Database configuration setup completed successfully.");

            // Fetch the data from the database
            logger.info("Fetching data from the database using FxSwapRateDBConfig...");
            List<FxSwapRateDBData> fxSwapRateDBData = fxSwapRateDBconfig.fetchDbData();
            logger.info("Fetched {} records from the database.", fxSwapRateDBData.size());

            // Log the ID from the API response
            String responseId = String.valueOf(fxSwapRatePostResponse.getId());
            scenarioContext.setData(TestConstants.FXSWAPRATEID, responseId);
            logger.info("API Response ID to be validated: {}", responseId);

            // Verify if the response ID exists in the database
            boolean idExists = fxSwapRateDBData.stream()
                    .anyMatch(s -> s.getId().equalsIgnoreCase(scenarioContext.getData(TestConstants.FXSWAPRATEID)));

            // Log the result of the validation
            if (idExists) {
                logger.info("The unique ID '{}' from the API response exists in the database.", responseId);
            } else {
                logger.warn("The unique ID '{}' from the API response does NOT exist in the database.", responseId);
            }

        } catch (SQLException e) {
            // Log details if an SQL exception occurs
            logger.error("A database error occurred: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            // Log details if any unexpected exception occurs
            logger.error("An unexpected error occurred: {}", e.getMessage(), e);
            throw e;
        }
    }


    public void validateFxResponseDBPayloadWithRetry() throws SQLException {
        try {
            // Log the start of database configuration
            logger.info("Starting database configuration setup...");
            fxSwapRateDBconfig.setUpDbConfig();
            logger.info("Database configuration setup completed successfully.");

            // Constants for retry logic
            final int maxRetries = 5; // Maximum retry attempts
            final int delayInMillis = 2000; // Delay between retries in milliseconds
            boolean dataExists = false;

            // Retrieve data from scenario context for validation
            String lowerDayCount = scenarioContext.getData(TestConstants.LOWERDAYCOUNT);
            String upperDayCount = scenarioContext.getData(TestConstants.UPPERDAYCOUNT);
            String tenor = scenarioContext.getData(TestConstants.TENOR);
            String revenueShare = scenarioContext.getData(TestConstants.REVENUE_SHARE);
            double revenueShareValue = Double.parseDouble(revenueShare);
            double threshold = revenueShareValue * 0.0001; // Dynamic threshold for floating-point comparison

            // Log the context data
            logger.info("Validating using scenario context data and dynamic threshold:");
            logger.info(" - Tenor: {}", tenor);
            logger.info(" - Lower Day Count: {}", lowerDayCount);
            logger.info(" - Upper Day Count: {}", upperDayCount);
            logger.info(" - Revenue Share: {}", revenueShare);
            logger.info(" - Dynamic Threshold for Revenue Share: {}", threshold);

            // Retry mechanism to handle potential latency
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                logger.info("Attempt {} of {}: Fetching data from the database...", attempt, maxRetries);
                List<FxSwapRateDBData> fxSwapRateDBData = fxSwapRateDBconfig.fetchDbData();
                logger.info("Fetched {} records from the database on attempt {}.", fxSwapRateDBData.size(), attempt);

                // Verify if the matching record exists
                dataExists = fxSwapRateDBData.stream()
                        .anyMatch(record ->
                                record.getTenor().equalsIgnoreCase(tenor) &&
                                        record.getLowerBound() == Integer.parseInt(lowerDayCount) &&
                                        record.getUpperBound() == Integer.parseInt(upperDayCount) &&
                                        Math.abs(record.getRevenueShare() - revenueShareValue) < threshold);

                // If record is found, break the retry loop
                if (dataExists) {
                    logger.info("Validation successful: Matching record found in the database on attempt {}.", attempt);
                    break;
                }

                // If no match is found, log and wait for retry
                logger.warn("No matching record found in the database on attempt {}. Retrying in {} ms...", attempt, delayInMillis);
                Thread.sleep(delayInMillis);
            }

            // Final result after retry attempts
            if (!dataExists) {
                logger.error("Validation failed: No matching record found in the database after {} attempts.", maxRetries);
            }
        } catch (SQLException e) {
            // Handle database-related errors
            logger.error("A database error occurred: {}", e.getMessage(), e);
            throw e;
        } catch (InterruptedException e) {
            // Handle thread interruption
            logger.error("Retry mechanism was interrupted: {}", e.getMessage(), e);
            Thread.currentThread().interrupt(); // Restore the interrupted status
            throw new RuntimeException("Validation interrupted due to retry mechanism failure", e);
        } catch (Exception e) {
            // Handle generic unexpected errors
            logger.error("An unexpected error occurred during validation: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void validateFxPUTResponseAPIPayload() throws JsonProcessingException {
        // Log the start of database configuration
        logger.info("Starting Response PUT Validation...");
        boolean dataExists = false;

        // Retrieve data from scenario context for validation
        String lowerDayCount = scenarioContext.getData(TestConstants.LOWERDAYCOUNT);
        String upperDayCount = scenarioContext.getData(TestConstants.UPPERDAYCOUNT);
        String tenor = scenarioContext.getData(TestConstants.TENOR);
        String revenueShare = scenarioContext.getData(TestConstants.REVENUE_SHARE);
        double revenueShareValue = Double.parseDouble(revenueShare);

        // Log the context data
        logger.info("Validating using scenario context data and dynamic threshold:");
        logger.info(" - Tenor: {}", tenor);
        logger.info(" - Lower Day Count: {}", lowerDayCount);
        logger.info(" - Upper Day Count: {}", upperDayCount);
        logger.info(" - Revenue Share: {}", revenueShare);

        FxswapRatePutResponse fxswapRatePutResponse = objectMapper.readValue(response.asString(), FxswapRatePutResponse.class);
        // Validating each field with detailed logs
        logger.info("Validating fields of FxSwapRatePutResponse...");

        // Validate Lower Bound Day Count
        if (fxswapRatePutResponse.getLowerBoundInDays() == Integer.parseInt(lowerDayCount)) {
            logger.info("Lower Day Count matches successfully. Expected: {}, Actual: {}", lowerDayCount, fxswapRatePutResponse.getLowerBoundInDays());
        } else {
            logger.error("Lower Day Count validation failed. Expected: {}, Actual: {}", lowerDayCount, fxswapRatePutResponse.getLowerBoundInDays());
        }

        // Validate Upper Bound Day Count
        if (fxswapRatePutResponse.getUpperBoundInDays() == Integer.parseInt(upperDayCount)) {
            logger.info("Upper Day Count matches successfully. Expected: {}, Actual: {}", upperDayCount, fxswapRatePutResponse.getUpperBoundInDays());
        } else {
            logger.error("Upper Day Count validation failed. Expected: {}, Actual: {}", upperDayCount, fxswapRatePutResponse.getUpperBoundInDays());
        }

        // Validate Tenor
        if (fxswapRatePutResponse.getTenor().equalsIgnoreCase(tenor)) {
            logger.info("Tenor matches successfully. Expected: {}, Actual: {}", tenor, fxswapRatePutResponse.getTenor());
        } else {
            logger.error("Tenor validation failed. Expected: {}, Actual: {}", tenor, fxswapRatePutResponse.getTenor());
        }

        // Validate Revenue Share (handles floating-point precision by using a small threshold)
        final double threshold = 0.00001; // Precision threshold for floating-point comparison
        if (Math.abs(fxswapRatePutResponse.getRevenueShare() - revenueShareValue) < threshold) {
            logger.info("Revenue Share matches successfully. Expected: {}, Actual: {}", revenueShareValue, fxswapRatePutResponse.getRevenueShare());
        } else {
            logger.error("Revenue Share validation failed. Expected: {}, Actual: {}", revenueShareValue, fxswapRatePutResponse.getRevenueShare());
        }

        // Validate Formatted Revenue Share
        if (fxswapRatePutResponse.getFormattedRevenueShare().equals(revenueShare)) {
            logger.info("Formatted Revenue Share matches successfully. Expected: {}, Actual: {}", revenueShare, fxswapRatePutResponse.getFormattedRevenueShare());
        } else {
            logger.error("Formatted Revenue Share validation failed. Expected: {}, Actual: {}", revenueShare, fxswapRatePutResponse.getFormattedRevenueShare());
        }

        // Validate Day Count
        String expectedDayCount = lowerDayCount + " - " + upperDayCount; // Example: "5000 - 5003"
        if (fxswapRatePutResponse.getDayCount().equals(expectedDayCount)) {
            logger.info("Day Count matches successfully. Expected: {}, Actual: {}", expectedDayCount, fxswapRatePutResponse.getDayCount());
        } else {
            logger.error("Day Count validation failed. Expected: {}, Actual: {}", expectedDayCount, fxswapRatePutResponse.getDayCount());
        }

        // Completed Validation
        logger.info("FxSwapRatePutResponse validation completed.");
    }

    public void setTheFxSwapRate(Integer id) {
        scenarioContext.setData(TestConstants.FXSWAPRATEID, String.valueOf(id));
    }


    public void assertTheCode(Integer expectedResponseCode) {
        int actualStatusCode = response.getStatusCode(); // Extract actual status code
        logger.info("Validating the Response Status Code..."); // Start validation log
        logger.info("Expected Status Code: {}", expectedResponseCode);
        logger.info("Actual Status Code: {}", actualStatusCode);

        // Perform assertion to validate the status code
        if (expectedResponseCode.equals(actualStatusCode)) {
            logger.info("Validation Passed: The actual status code matches the expected status code.");
        } else {
            logger.error("Validation Failed: Expected Status Code = {}, Actual Status Code = {}",
                    expectedResponseCode, actualStatusCode);
        }

        // Perform assertion (soft to continue the test execution)
        softAssert.assertEquals(
                actualStatusCode,
                expectedResponseCode.intValue(),
                "The response status code did not match the expected value."
        );
    }

    public void assertTheText(String expectedText, String actualText) {
        logger.info("Validating the Response Text...");
        logger.info("Expected Text: {}", expectedText);
        logger.info("Actual Text: {}", actualText);

        // Perform assertion to validate the text
        if (expectedText.equals(actualText)) {
            logger.info("Validation Passed: The actual text matches the expected text.");
        } else {
            logger.error("Validation Failed: Expected Text = '{}', Actual Text = '{}'", expectedText, actualText);
        }

        // Perform assertion (soft to continue the test execution)
        softAssert.assertEquals(
                actualText,
                expectedText,
                String.format("The response text did not match the expected value. Expected: '%s', Actual: '%s'", expectedText, actualText)
        );

    }

    // Method to validate that the record with a given ID does not exist in the list
    public void validateIfTheRecordDoesntExist(List<FxSwapRateDBData> fxSwapRateDBData) {
        String idToCheck = scenarioContext.
                getData(TestConstants.FXSWAPRATEID);
        logger.info("Starting validation to ensure the record with ID {} does not exist in the database.", idToCheck);

        // Check if the ID exists in the list
        boolean idExists = fxSwapRateDBData.stream()
                .anyMatch(record -> record.getId().equalsIgnoreCase(idToCheck));

        // Log and assert based on the result
        if (idExists) {
            logger.error("Validation Failed: Record with ID {} is still present in the database.", idToCheck);
        } else {
            logger.info("Validation Passed: Record with ID {} does not exist in the database.", idToCheck);
        }

        // Use soft assertion to log the failure but not stop test execution
        softAssert.assertFalse(
                idExists,
                String.format("Record with ID %s should not exist, but it was found in the database!", idToCheck)
        );
    }

    public void validateErrorResponse(DataTable dataTable) {
        String message = jsonPath.getString(dataTable.asMaps().get(0).get("field"));
        String sanitizedExpectedText = dataTable.asMaps().get(0).get("issue").strip().replaceAll("^\"|\"$", "");
        assertTheText(sanitizedExpectedText, message);
    }
}
