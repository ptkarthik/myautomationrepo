package org.billing.services.ui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContext;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.TestConstants;
import org.billing.api.APIUtils.APIAuthentication;
import org.billing.api.endpoints.EndPoints;
import org.billing.api.payloads.request.post.TraderPostRequest;
import org.billing.api.payloads.request.put.TraderPutRequest;
import org.billing.api.responses.get.GetAllTraders;
import org.billing.api.responses.post.TraderPostResponse;
import org.billing.api.responses.put.FieldErrorResponse;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.commonpojos.ToraTrader;
import org.billing.dbclasses.TraderDbData;
import org.billing.dbconfig.TraderDBConfig;
import org.billing.utils.apiutilities.APIUtils;
import org.billing.utils.general.ExcelUtil;
import org.billing.utils.propconfig.PropertiesReader;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.billing.utils.apiutilities.GenericUtils.getRandomLetters;

/**
 * TraderServices Class: Provides service methods for "Trader" API interactions
 * Corresponds to Trader API Step Definitions.
 */
public class TraderServices extends BaseServices {
    private static final Logger logger = LogManager.getLogger(TraderServices.class);
    private final ScenarioContextWithObject scenarioContextWithObject;
    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SoftAssert softAssert;
    private TraderDBConfig traderDBConfig = new TraderDBConfig();
    private Response response;
    private JsonPath jsonPath;


    public TraderServices(ScenarioContextWithObject scenarioContextWithObject, ScenarioContext scenarioContext) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.scenarioContext = scenarioContext;
        softAssert = SoftAssertContainer.getInstance();
    }


    // ===== Navigation and Page Interactions Methods =====

    public void navigateToTradersPage() {
        logger.info("Navigating to Trader Page...");
        // Simulate navigation using validation utilities for UI workflow
    }

    // ===== API Methods =====

    public TraderPostRequest createATradersPostRequest(DataTable dataTable) throws JsonProcessingException {
        // Log the received data table
        logger.info("Received DataTable: {}", dataTable.asMaps());
        boolean isToraTraderBooleanValue = false;

        // Retrieve initial values from the DataTable
        String id = dataTable.asMaps().get(0).get(TestConstants.TRADERID).trim();
        String name = dataTable.asMaps().get(0).get(TestConstants.TRADERNAME);
        String isToraTrader = dataTable.asMaps().get(0).get(TestConstants.ISTORATRADER);

        if (isToraTrader.equalsIgnoreCase("true")) {
            isToraTraderBooleanValue = true;
        }

        // A condition to check and ensure ID uniqueness
        boolean isUnique = false;
        int retryCount = 0;

        logger.info("Checking for unique trader ID...");
        while (!isUnique && retryCount < 10) { // Retry maximum 10 times to find a unique ID
            if (idExistsInDatabase(id) || idExistsInAPI(id)) { // Check existence in DB or API
                logger.warn("Trader ID {} already exists. Generating a new ID...", id);
                id = generateNewTraderId(id, retryCount); // Generate new ID dynamically
                retryCount++;
            } else {
                isUnique = true; // Exit the loop once a unique ID is found
            }
        }

        // If still not unique after retries, throw an exception
        if (!isUnique) {
            throw new RuntimeException("Failed to find a unique Trader ID after 10 attempts.");
        }


        // Store data in the scenario context
        scenarioContextWithObject.setData(TestConstants.TRADERID, id);
        scenarioContextWithObject.setData(TestConstants.TRADERNAME, name);
        scenarioContextWithObject.setData(TestConstants.ISTORATRADER, isToraTraderBooleanValue);


        // Create FxSwapRatePostRequest object with values from scenario context
        TraderPostRequest traderPostRequestPost = new TraderPostRequest(
                (String) scenarioContextWithObject.getData(TestConstants.TRADERID),
                (String) scenarioContextWithObject.getData(TestConstants.TRADERNAME),
                (boolean) scenarioContextWithObject.getData(TestConstants.ISTORATRADER));

        // Log the created request object
        logger.info("Created TraderPost: {}", traderPostRequestPost);
        return traderPostRequestPost;
    }

    private boolean idExistsInDatabase(String id) {
        try {
            logger.info("Checking in database if ID {} exists...", id);
            traderDBConfig.setUpDbConfig(); // Set up database configuration
            List<TraderDbData> existingRecords = traderDBConfig.fetchDbData(); // Fetch all traders
            return existingRecords.stream().anyMatch(record -> record.getId().equalsIgnoreCase(id));
        } catch (SQLException e) {
            logger.error("Error while querying database for trader ID existence: {}", e.getMessage());
            throw new RuntimeException("Database error during ID existence check", e);
        }
    }

    private boolean idExistsInAPI(String traderId) throws JsonProcessingException {
        logger.info("Checking in API if ID {} exists...", traderId);
        createATradersGetAllRequest(); // Fetch all traders using GET API

        // Retrieve all trader IDs from the API response
        List<GetAllTraders> allTraders = (List<GetAllTraders>) scenarioContextWithObject.getData(TestConstants.LISTOFALLTRADERS);

        // Check if the trader ID matches
        return allTraders.stream().anyMatch(trader -> trader.getId().equalsIgnoreCase(traderId));
    }

    private String generateNewTraderId(String baseId, int retryCount) {
        // Generate a random three-letter alphabetical string
        String randomLetters1 = getRandomLetters(3);
        String randomLetters2 = getRandomLetters(3);
        // Optionally, you can add more uniqueness (e.g., timestamp)
        String newId = baseId + "_" + randomLetters1 +randomLetters2+ "_" + retryCount;
        logger.info("Generated new ID: {}", newId);
        return newId;
    }

    public void createATradersGetAllRequest() throws JsonProcessingException {
        try {
            logger.info("Fetching Traders Details using the GET API endpoint.");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", "Bearer " + APIAuthentication.postAuthorizationCode()));
            response = APIUtils.getAllWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    EndPoints.GET_ALL_TRADERS,
                    headers
            );

            scenarioContextWithObject.setData(TestConstants.GET_RESPONSE, response);
            logger.info("GET request for Traders completed. Response status code: {}", ((Response) scenarioContextWithObject.
                    getData(TestConstants.GET_RESPONSE)).
                    getStatusCode());
            // Log the deserialized response object
            ObjectMapper objectMapper = new ObjectMapper();
            List<GetAllTraders> getAllTraders = objectMapper.
                    readValue(((Response) scenarioContextWithObject.getData(TestConstants.GET_RESPONSE)).asString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, GetAllTraders.class));
            logger.info("Deserialized Response: size is {}", getAllTraders.size());
            scenarioContextWithObject.setData(TestConstants.LISTOFALLTRADERS, getAllTraders);
        } catch (Exception e) {
            logger.error("Error occurred while fetching broker details.", e);
            throw e;
        }
    }

    public void hitEndPointUsingCreatedPostReq(TraderPostRequest traderPostRequest) throws JsonProcessingException {
        logger.info("Starting API call to Endpoint: {}", EndPoints.POST_TRADER); // Log the endpoint we're hitting

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));
        logger.info("Authorization Token Generated: {}", authToken); // Log the token details (optional for debugging)

        // Serialize the payload
        String payload = objectMapper.writeValueAsString(traderPostRequest);
        logger.info("Serialized Payload: {}", payload); // Log the JSON payload being sent to the API

        // Log the headers list
        logger.info("Headers: {}", headers);

        // Perform the POST request
        logger.info("Initiating POST request...");
        response = APIUtils.postWithAuth(
                PropertiesReader.getProperty("baseUrl"),
                EndPoints.POST_TRADER,
                payload,
                headers
        );
        logger.info(response.asString());
        scenarioContextWithObject.setData(TestConstants.RESPONSE, response);
        // Deserialize the response JSON into a Java object (FxSwapRatePostResponse)
        TraderPostResponse traderPostResponse = objectMapper.readValue(((Response) scenarioContextWithObject.
                getData(TestConstants.RESPONSE)).asString(), TraderPostResponse.class);
        scenarioContextWithObject.setData(TestConstants.TRADER_POSTRESPOJO, traderPostResponse);
        // Log the response after the API call
        if (scenarioContextWithObject.getData(TestConstants.RESPONSE) != null) {
            logger.info("API Response: {}", ((Response) scenarioContextWithObject.getData(TestConstants.
                    RESPONSE)).getBody().asString()); // Log the response body
            logger.info("Response Status Code: {}", ((Response) scenarioContextWithObject.
                    getData(TestConstants.RESPONSE))
                    .getStatusCode()); // Log the status code
        } else {
            logger.error("API call failed. Response is null.");
        }
    }

    public void hitEndPointUsingCreatedPutReq(TraderPutRequest traderPutRequest) throws JsonProcessingException {
        logger.info("Starting API call to Endpoint: {}", EndPoints.PUT_TRADER); // Log the endpoint we're hitting

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));
        logger.info("Authorization Token Generated: {}", authToken);

        // Setup path variables
        Map<String, String> pathData = new HashMap<>();
        pathData.put("id", (String) scenarioContextWithObject.getData(TestConstants.PATH_VARIABLE));

        // Serialize the payload
        String payload = objectMapper.writeValueAsString(traderPutRequest);
        logger.info("Serialized Payload: {}", payload);
        logger.info("Headers: {}", headers);

        try {
            // Perform the PUT request
            response = APIUtils.putWithAuthAndPath(
                    PropertiesReader.getProperty("baseUrl"),
                    pathData,
                    EndPoints.PUT_TRADER,
                    payload,
                    headers
            );

            // Store the response in the scenario context
            scenarioContextWithObject.setData(TestConstants.RESPONSE, response);

            // Log the response body and status code
            if (response != null) {
                int statusCode = response.getStatusCode();
                logger.info("PUT Request Response Status Code: {}", statusCode);
                String responseBody = response.getBody().asString();
                logger.info("Response Body: {}", responseBody);

                // Deserialize the response if required
                try {
                    FieldErrorResponse fieldErrorResponse = objectMapper.readValue(responseBody, FieldErrorResponse.class);
                    scenarioContextWithObject.setData(TestConstants.TRADER_PUTERROR_RESPOJO, fieldErrorResponse);
                } catch (Exception e) {
                    logger.warn("Failed to deserialize PUT response: {}", e.getMessage(), e);
                }
            } else {
                logger.error("API call failed. Response is null.");
                throw new RuntimeException("PUT request returned a null response.");
            }

        } catch (Exception e) {
            logger.error("Error occurred during PUT request: {}", e.getMessage(), e);
            throw new RuntimeException("Exception occurred during PUT request execution.", e);
        }
    }

    public void createATradersGetRequest(DataTable dataTable) throws JsonProcessingException {
        try {
            String size = dataTable.asMaps().get(0).get("size");
            String page = dataTable.asMaps().get(0).get("page");
            scenarioContext.setData(TestConstants.TRADERPAGESIZE, size);
            scenarioContext.setData(TestConstants.TRADERPAGENUMBER, page);
            logger.info("Fetching Traders Details using the GET API endpoint.");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", "Bearer " + APIAuthentication.postAuthorizationCode()));
            Map<String, String> queryParams = new HashMap<>();
            queryParams.put(TestConstants.TRADERPAGESIZE, scenarioContext.getData(TestConstants.TRADERPAGESIZE));
            queryParams.put(TestConstants.TRADERPAGENUMBER, scenarioContext.getData(TestConstants.TRADERPAGENUMBER));
            response = APIUtils.getWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    EndPoints.GET_TRADERS, queryParams,
                    headers
            );
            scenarioContextWithObject.setData(TestConstants.RESPONSE, response);
            logger.info("GET request for Traders completed. Response status code: {}", response.getStatusCode());
        } catch (Exception e) {
            logger.error("Error occurred while fetching broker details.", e);
            throw e;
        }
    }

    public void validateApiStatusCode(Integer int1) throws JsonProcessingException {
        try {
            // Log the raw API response string
            softAssert.assertEquals(
                    Integer.valueOf(((Response) scenarioContextWithObject.getData(TestConstants.RESPONSE)).getStatusCode()),
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

    public void validateApiStatusCodeGetAll(Integer int1) throws JsonProcessingException {
        try {
            // Log the raw API response string
            softAssert.assertEquals(
                    Integer.valueOf(((Response) scenarioContextWithObject.getData(TestConstants.GET_RESPONSE)).getStatusCode()),
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

    public void validateApiStatusCodeForUpload(Integer int1) throws JsonProcessingException {
        try {
            softAssert.assertEquals(
                    Integer.valueOf(((Response) scenarioContextWithObject.
                            getData(TestConstants.UPLOAD_RESPONSE)).getStatusCode()),
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

    public void validatetheGetReponseReceivedWithDBAndAPI() throws SQLException {
        try {
            // Log the start of database configuration
            logger.info("Starting database configuration setup...");
            traderDBConfig.setUpDbConfig();
            logger.info("Database configuration setup completed successfully.");

            // Constants for retry logic
            final int maxRetries = 5; // Maximum retry attempts
            final int delayInMillis = 2000; // Delay between retries in milliseconds
            boolean dataMatches = false;

            List<GetAllTraders> allTradersApiData = (List<GetAllTraders>)
                    scenarioContextWithObject.getData(TestConstants.LISTOFALLTRADERS);

            // Retry mechanism to handle potential latency
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                logger.info("Attempt {} of {}: Fetching data from the database...", attempt, maxRetries);
                List<TraderDbData> traderDbData = traderDBConfig.fetchDbData();
                logger.info("Fetched {} records from the database on attempt {}.", traderDbData.size(), attempt);

                // Compare data from API and database
                dataMatches = compareLists(allTradersApiData, traderDbData);

                // If a match is found, break the retry loop
                if (dataMatches) {
                    logger.info("Validation successful: API data matches database data on attempt {}.", attempt);
                    break;
                }

                // If no match is found, log and wait for retry
                logger.warn("No matching data found between API and database on attempt {}. Retrying in {} ms...", attempt, delayInMillis);
                Thread.sleep(delayInMillis);
            }

            // Final result after retry attempts
            if (!dataMatches) {
                logger.error("Validation failed: No matching data found between API and database after {} attempts.", maxRetries);
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

    public static List<GetAllTraders> fetchAllTraders() throws Exception {
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", "Bearer " + APIAuthentication.postAuthorizationCode()));
        Response response = APIUtils.getAllWithAuth(
                PropertiesReader.getProperty("baseUrl"),
                EndPoints.GET_ALL_TRADERS,
                headers
        );
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.asString(),
                new TypeReference<List<GetAllTraders>>() {});
    }

    /**
     * Compares two lists: one from the API and one from the database.
     *
     * @param apiData the list from the API
     * @param dbData  the list from the database
     * @return true if all the required fields match across both lists; false otherwise
     */
    private boolean compareLists(List<GetAllTraders> apiData, List<TraderDbData> dbData) {
        if (apiData == null || dbData == null || apiData.size() != dbData.size()) {
            logger.warn("The sizes of API data and database data do not match or one of them is null.");
            return false;
        }

        // Sort both lists to ensure the same order for comparison (if applicable)
        apiData.sort(Comparator.comparing(GetAllTraders::getId)); // Using a sample `getId()` for sorting API data
        dbData.sort(Comparator.comparing(TraderDbData::getId));   // Using a sample `getId()` for sorting DB data

        for (int i = 0; i < apiData.size(); i++) {
            GetAllTraders apiTrader = apiData.get(i);
            TraderDbData dbTrader = dbData.get(i);

            // Compare individual fields between the API trader and the DB trader
            if (!apiTrader.getId().equals(dbTrader.getId()) ||
                    !apiTrader.getName().equals(dbTrader.getName()) ||
                    !apiTrader.getId().equals(dbTrader.getId()) || // Add other fields here if needed
                    apiTrader.getIsToraTrader() != dbTrader.getToraTrader()) { // Ensure this field comparison makes sense for your data
                logger.warn("Mismatch found for trader at index {}: API Data = {}, DB Data = {}", i, apiTrader, dbTrader);
                return false; // Fail early if a mismatch is detected
            }
        }

        // If all records match
        return true;
    }

    /**
     * Compares two lists: one from the API (ToraTrader) and one from the database (GetAllTraders).
     * @return true if all the required fields match across both lists; false otherwise
     */
    public void compareExcelVSAPIAndLogResults() {
        // Retrieve data from the context
        List<ToraTrader> excelTradersList = (List<ToraTrader>) scenarioContextWithObject.getData(TestConstants.TRADERS_POJO);
        List<GetAllTraders> apiTradersList = (List<GetAllTraders>) scenarioContextWithObject.getData(TestConstants.LISTOFALLTRADERS);

        if (excelTradersList == null || apiTradersList == null) {
            softAssert.fail("One of the trader lists is null. Excel Data: " + excelTradersList + ", API Data: " + apiTradersList);
            return;
        }

        // Preprocess Excel data (convert TRUE, YES, FALSE, NO to Boolean equivalents for comparison)
        preprocessExcelData(excelTradersList);

        // Create a Set of API traders for quick lookups
        Set<String> apiTradersSet = apiTradersList.stream()
                .map(trader -> trader.getId() + "|" + trader.getName() + "|" + trader.getIsToraTrader())
                .collect(Collectors.toSet());

        // Check each Excel trader against the API dataset
        for (ToraTrader excelTrader : excelTradersList) {
            String excelTraderKey = excelTrader.getId() + "|" +
                    excelTrader.getName() + "|" +
                    excelTrader.getToraTraderString();

            if (apiTradersSet.contains(excelTraderKey)) {
                // Log success for matching trader
                logger.info("Excel trader exists in API data: {}", excelTraderKey);
                softAssert.assertTrue(true, "Excel trader exists: " + excelTraderKey);
            } else {
                // Log failure for missing trader
                logger.warn("Excel trader NOT found in API data: {}", excelTraderKey);
                softAssert.fail("Excel trader NOT found: " + excelTraderKey);
            }
        }
    }

    /**
     * Preprocess Excel data: Converts "TRUE" or "YES" (case-insensitive) to "true",
     * and "FALSE" or "NO" to "false" in the `ToraTraderString` field.
     */
    private void preprocessExcelData(List<ToraTrader> traders) {
        for (ToraTrader trader : traders) {
            if (trader.getToraTraderString() != null) {
                String normalizedValue = normalizeBooleanValue(trader.getToraTraderString());
                trader.setToraTraderString(normalizedValue);
            }
        }
    }

    /**
     * Converts common Boolean-like strings to standard Boolean representations:
     * "TRUE", "YES" -> "true"
     * "FALSE", "NO" -> "false"
     * Others remain unchanged.
     */
    private String normalizeBooleanValue(String value) {
        if (value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("YES")) {
            return "true";
        } else if (value.equalsIgnoreCase("FALSE") || value.equalsIgnoreCase("NO")) {
            return "false";
        }
        return value; // Return as-is if it doesn't match any condition
    }

    public void validateEmptyArrayOfResponse() {
        try {
            // Retrieve the list of all traders from the context
            List<GetAllTraders> allTradersApiData = (List<GetAllTraders>)
                    scenarioContextWithObject.getData(TestConstants.LISTOFALLTRADERS);

            // Check if the list is empty
            if (allTradersApiData == null || allTradersApiData.isEmpty()) {
                logger.info("Validation successful: The API response contains an empty list.");
                softAssert.assertTrue(allTradersApiData == null || allTradersApiData.isEmpty(),
                        "Expected an empty list, but the validation is successful."); // Accumulating assertion
            } else {
                logger.warn("Validation failed: The API response list is not empty. List has {} records.", allTradersApiData.size());
                softAssert.assertTrue(false,
                        "Validation failed: Expected an empty list, but found " + allTradersApiData.size() + " records.");
            }
        } catch (Exception e) {
            // Handle unexpected errors
            logger.error("An error occurred during validation: {}", e.getMessage(), e);
            softAssert.fail("An unexpected error occurred during validation: " + e.getMessage());
        }
    }

    public void createTradersUploadRequest(DataTable queryParams) {
        scenarioContextWithObject.setData("DataTableData", queryParams);
        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Authorization", "Bearer " + authToken));
        logger.info("Authorization Token Generated: {}", authToken); // Log the token details (optional for debugging)

        Map<String, Object> query = new HashMap<>();
        query.put("isHeaderPresent", queryParams.asMaps().get(0).get("isHeaderPresent"));
        // Log the headers list
        logger.info("Headers: {}", headers);

        // Path to the CSV file to be uploaded
        File file = new File(queryParams.asMaps().get(0).get("filePath"));
        response = APIUtils.uploadFile(PropertiesReader.getProperty("baseUrl"),
                EndPoints.UPLOAD_TRADERS,
                query, "file", file,
                headers);
        scenarioContextWithObject.setData(TestConstants.UPLOAD_RESPONSE, response);
        System.out.println(response.asString());
    }

    public void readExcelAndConvertTheEntriestoPOJO() throws IOException {
        boolean headerOn = false;
        DataTable dataTable = (DataTable) scenarioContextWithObject.getData("DataTableData");
        if (dataTable.asMaps().get(0).get("isHeaderPresent").equalsIgnoreCase("true")) {
            headerOn = true;
        }
        List<Map<String, String>> excelData = ExcelUtil.readExcelOrCsvWithDataTable(dataTable.asMaps().get(0).
                get("filePath"), headerOn, "Traders");
        List<ToraTrader> toraTradersList = ExcelUtil.mapToToraTraderList(excelData);
        scenarioContextWithObject.setData(TestConstants.TRADERS_POJO, toraTradersList);
    }


    public void compareResponseVsDataGiven() {
        try {
            // Fetch the stored trader details from the scenario context
            String traderId = (String) scenarioContextWithObject.getData(TestConstants.TRADERID);
            String traderName = (String) scenarioContextWithObject.getData(TestConstants.TRADERNAME);
            Boolean isToraTrader = (Boolean) scenarioContextWithObject.getData(TestConstants.ISTORATRADER);

            // Fetch the list of all traders from the scenario context
            List<GetAllTraders> allTraders = (List<GetAllTraders>) scenarioContextWithObject.
                    getData(TestConstants.LISTOFALLTRADERS);

            // Validate: Check if at least ONE of the traders in the list matches ALL the properties
            boolean isTraderValid = allTraders.stream()
                    .anyMatch(trader ->
                            trader.getId().equals(traderId) &&  // Match traderId
                                    trader.getName().equals(traderName) &&  // Match traderName
                                    trader.getIsToraTrader() == isToraTrader  // Match isToraTrader
                    );

            // Soft assertion to validate the result
            softAssert.assertTrue(
                    isTraderValid,
                    String.format(
                            "Validation failed: No trader in the list matches the expected properties [TraderID: %s, TraderName: %s, IsToraTrader: %s]",
                            traderId,
                            traderName,
                            isToraTrader
                    )
            );

            // Log validation success if all assertions pass
            logger.info("Trader details validated successfully using SoftAssertions.");
        } catch (Exception e) {
            logger.error("Error occurred during trader validation.", e);
            throw e;
        }
    }

    public TraderPutRequest createAPutTraderRequest(String endpoint, DataTable dataTable) {
        // Log the received data table
        logger.info("Received DataTable: {}", dataTable.asMaps());
        // Replace placeholder in endpoint
        String traderId = (String) scenarioContextWithObject.getData(TestConstants.TRADERID);
        endpoint = endpoint.replace("<TraderId>", traderId);
        boolean isToraTraderBooleanValue = false;
        String[] pathParamValue = endpoint.split("/");
        String pathVariable = pathParamValue[2];


        // Retrieve and log each value from the DataTable
        String id = traderId;
        String name = dataTable.asMaps().get(0).get(TestConstants.TRADERNAME);
        String isToraTrader = dataTable.asMaps().get(0).get(TestConstants.ISTORATRADER);
        if (isToraTrader.equalsIgnoreCase("true")) {
            isToraTraderBooleanValue = true;
        }

        // Store data in the scenario context
        scenarioContextWithObject.setData(TestConstants.TRADERID, id);
        scenarioContextWithObject.setData(TestConstants.PATH_VARIABLE, pathVariable);
        if (name == null || name.trim().isEmpty()) {
            name = "";  // Set name to an explicit empty value (to prevent NullPointerException)
        }
        scenarioContextWithObject.setData(TestConstants.TRADERNAME, name);
        scenarioContextWithObject.setData(TestConstants.ISTORATRADER, isToraTraderBooleanValue);

        // Create FxSwapRatePostRequest object with values from scenario context
        TraderPutRequest traderPutRequest = new TraderPutRequest(
                (String) scenarioContextWithObject.getData(TestConstants.TRADERID),
                (String) scenarioContextWithObject.getData(TestConstants.TRADERNAME),
                (boolean) scenarioContextWithObject.getData(TestConstants.ISTORATRADER));

        // Log the created request object
        logger.info("Created TraderPost: {}", traderPutRequest);
        return traderPutRequest;
    }

    public void createDeleteRequest(String endpoint) {
        String[] pathParamValue = endpoint.split("/");
        String pathVariable = pathParamValue[2];
        scenarioContextWithObject.setData(TestConstants.PATH_VARIABLE, pathVariable);
        // Start logging the API call
        logger.info("Starting DELETE request to Endpoint: {}", EndPoints.DELETE_TRADER);

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode(); // Get authorization token
        headers.add(new Header("Content-Type", "application/json")); // Add Content-Type header
        headers.add(new Header("Authorization", "Bearer " + authToken)); // Add Authorization header

        // Setup path variables
        Map<String, String> pathData = new HashMap<>();
        pathData.put("id", (String) scenarioContextWithObject.getData(TestConstants.PATH_VARIABLE));

        // Log detailed header information
        logger.info("Authorization Token Generated: {}", authToken);
        logger.info("Headers: {}", headers);

        // Making the DELETE API call
        logger.info("Initiating DELETE request...");
        response = APIUtils.deleteWithAuthAndPath(
                PropertiesReader.getProperty("baseUrl"),
                pathData,
                EndPoints.DELETE_TRADER, // Base URL from property
                headers // Headers
        );
        scenarioContextWithObject.setData(TestConstants.RESPONSE, response);

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

    public void fetchDB() throws JsonProcessingException {
        try {
            logger.info("Fetching Traders Details using the GET API endpoint.");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", "Bearer " + APIAuthentication.postAuthorizationCode()));
            response = APIUtils.getAllWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    EndPoints.GET_ALL_TRADERS,
                    headers
            );

            scenarioContextWithObject.setData(TestConstants.GET_RESPONSE, response);
            logger.info("GET request for Traders completed. Response status code: {}", ((Response) scenarioContextWithObject.
                    getData(TestConstants.GET_RESPONSE)).
                    getStatusCode());
            // Log the deserialized response object
            ObjectMapper objectMapper = new ObjectMapper();
            List<GetAllTraders> getAllTraders = objectMapper.
                    readValue(((Response) scenarioContextWithObject.getData(TestConstants.GET_RESPONSE)).asString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, GetAllTraders.class));
            logger.info("Deserialized Response: size is {}", getAllTraders.size());
            scenarioContextWithObject.setData(TestConstants.LISTOFALLTRADERS, getAllTraders);
        } catch (Exception e) {
            logger.error("Error occurred while fetching broker details.", e);
            throw e;
        }
    }

    public void validateIfTheRecordDoesntExist() {
        String idToCheck = (String) scenarioContextWithObject.getData(TestConstants.PATH_VARIABLE);
        logger.info("Starting validation to ensure the record with ID {} does not exist in the database.", idToCheck);

        // Check if the ID exists in the list
        List<GetAllTraders> allTraders = (List<GetAllTraders>) scenarioContextWithObject.getData(TestConstants.LISTOFALLTRADERS);
        boolean idExists = allTraders.stream()
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
}