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
import org.billing.Context.ScenarioContextWithObject;
import org.billing.TestConstants;
import org.billing.api.APIUtils.APIAuthentication;
import org.billing.api.endpoints.EndPoints;
import org.billing.api.payloads.request.post.CurrencyRatePostRequest;
import org.billing.api.payloads.request.put.CurrencyRatePutRequest;
import org.billing.api.responses.get.GetAllCurrencyRate;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.dbclasses.CurrencyRateDbData;
import org.billing.dbconfig.CurrencyRateDBConfig;
import org.billing.utils.apiutilities.APIUtils;
import org.billing.utils.propconfig.PropertiesReader;
import org.testng.asserts.SoftAssert;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static org.billing.utils.apiutilities.GenericUtils.getRandomLetters;

/**
 * CurrencyRateServices Class: Service methods for "Currency Rate" API interactions.
 */
public class CurrencyRateServices extends BaseServices {
    private static final Logger logger = LogManager.getLogger(CurrencyRateServices.class);
    private final ScenarioContextWithObject scenarioContextWithObject;
    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper;
    private final SoftAssert softAssert;
    private CurrencyRateDBConfig currencyRateDBConfig = new CurrencyRateDBConfig();
    private Response response;

    public CurrencyRateServices(ScenarioContextWithObject scenarioContextWithObject, ScenarioContext scenarioContext) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.objectMapper = new ObjectMapper();
        softAssert = SoftAssertContainer.getInstance();
        this.scenarioContext = scenarioContext;
    }

    /**
     * Creates a POST request payload from the DataTable and saves it in the `ScenarioContextWithObject`.
     *
     * @param dataTable DataTable containing payload data.
     */
    public void createAPostRequest(DataTable dataTable) {
        Map<String, String> dataMap = dataTable.asMaps().get(0);

        // Randomly choose 2 or 3 for the suffix length
        int suffixLength = 2 + new Random().nextInt(2); // 2 or 3

        String randomSuffix = getRandomLetters(suffixLength);

        String originalCode = dataMap.get("code");
        String newCode = originalCode + randomSuffix;

        CurrencyRatePostRequest request = new CurrencyRatePostRequest(
                dataMap.get("monthYear"),
                newCode,
                Double.parseDouble(dataMap.get("rate"))
        );

        scenarioContextWithObject.setData(TestConstants.CURRENCYCODE, newCode);
        scenarioContextWithObject.setData(TestConstants.CURRENCYMONTHYEAR, dataMap.get("monthYear"));
        scenarioContextWithObject.setData(TestConstants.CURRENCYRATE, dataMap.get("rate"));
        try {
            // Save the generated payload to Scenario Context with key "POST_PAYLOAD"
            scenarioContextWithObject.setData("POST_PAYLOAD", request);
            logger.info("POST request payload stored in ScenarioContextWithObject: {}", request);
        } catch (Exception e) {
            logger.error("Failed to store POST request payload.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends the POST request to the given endpoint with authorization and stores the response in `ScenarioContextWithObject`.
     *
     * @param endpoint            API endpoint to send the request to.
     * @param authorizationStatus Authorization type ("valid" or "unauthorized").
     */
    public void sendPostRequest(String endpoint, String authorizationStatus) {
        try {
            // Retrieve the payload from Scenario Context
            @SuppressWarnings("unchecked")
            CurrencyRatePostRequest payload = (CurrencyRatePostRequest)
                    scenarioContextWithObject.getData("POST_PAYLOAD");

            // Initialize headers
            List<Header> headers = new ArrayList<>();
            if (authorizationStatus.equalsIgnoreCase("valid")) {
                String authToken = APIAuthentication.postAuthorizationCode(); // Get authorization token
                headers.add(new Header("Content-Type", "application/json")); // Add Content-Type header
                headers.add(new Header("Authorization", "Bearer " + authToken));
                // Serialize payload to JSON
                String jsonPayload = objectMapper.writeValueAsString(payload);

                // Send the POST request
                Response response = APIUtils.postWithAuth(PropertiesReader.getProperty("baseUrl"),
                        endpoint, jsonPayload, headers);
                System.out.println(response.asString());

                JsonPath jsonPath = new JsonPath(response.asString());
                scenarioContextWithObject.setData(TestConstants.CURRENCYID, jsonPath.getString("id"));
                // Store the response in Scenario Context with key "POST_RESPONSE"
                scenarioContextWithObject.setData(TestConstants.RESPONSE, response);
                logger.info("POST request sent. Response status: {}", response.getStatusCode());
            } else {
                headers.add(new Header("Content-Type", "application/json"));
                // Serialize payload to JSON
                String jsonPayload = objectMapper.writeValueAsString(payload);
                // Send the POST request
                Response response = APIUtils.postWithAuth(PropertiesReader.getProperty("baseUrl"),
                        endpoint, jsonPayload, headers);
                System.out.println(response.asString());
                // Store the response in Scenario Context with key "POST_RESPONSE"
                scenarioContextWithObject.setData(TestConstants.RESPONSE, response);
                logger.info("POST request sent. Response status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error while sending POST request to endpoint '{}'.", endpoint, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends the GET request to the given endpoint with authorization and stores the response in `ScenarioContextWithObject`.
     *
     * @param endpoint            API endpoint to send the request to.
     * @param authorizationStatus Authorization type ("valid" or "unauthorized").
     */
    public void sendGetRequest(String endpoint, String authorizationStatus) {
        try {
            // Initialize headers
            List<Header> headers = new ArrayList<>();
            if (authorizationStatus.equalsIgnoreCase("valid")) {
                String authToken = APIAuthentication.postAuthorizationCode(); // Get authorization token
                headers.add(new Header("Content-Type", "application/json")); // Add Content-Type header
                headers.add(new Header("Authorization", "Bearer " + authToken));
            } else {
                logger.warn("Authorization status set to 'unauthorized'. No valid token will be sent.");
                headers.add(new Header("Content-Type", "application/json"));
            }

            // Send the GET request
            Response response = APIUtils.getAllWithAuth(PropertiesReader.getProperty("baseUrl"),
                    endpoint, headers);
            logger.info("GET request sent. Response status: {}, Response body: {}", response.getStatusCode(), response.asString());
            // Store the response in `ScenarioContextWithObject`
            scenarioContextWithObject.setData(TestConstants.RESPONSE, response);


            // Additional handling for valid responses
            if (((Response) scenarioContextWithObject.getData(TestConstants.RESPONSE)).getStatusCode() == 200) {
                JsonPath jsonPath = new JsonPath(((Response) scenarioContextWithObject.getData(TestConstants.RESPONSE))
                        .asString());
                if (jsonPath.getList("").isEmpty()) {
                    logger.info("Response body is an empty array.");
                } else {
                    logger.info("Response contains an array of currency rates.");
                    // Log the deserialized response object
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<GetAllCurrencyRate> getAllCurrencyRates = objectMapper.
                            readValue(((Response) scenarioContextWithObject.getData(TestConstants.RESPONSE)).asString(),
                                    objectMapper.getTypeFactory().constructCollectionType(List.class, GetAllCurrencyRate.class));
                    logger.info("Deserialized API Response: size is {}", getAllCurrencyRates.size());
                    scenarioContextWithObject.setData(TestConstants.LISTOFALLAPICURRENCYRATES, getAllCurrencyRates);
                }
            }

            // Handle authorization error
            if (response.getStatusCode() == 401) {
                logger.warn("Unauthorized access. Ensure valid authorization token is provided.");
            }

            // Log final response storage
            logger.info("Response has been stored in ScenarioContextWithObject under the key '{}'.", TestConstants.RESPONSE);

        } catch (Exception e) {
            logger.error("Error while sending GET request to endpoint '{}'.", endpoint, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the status code from the stored response.
     *
     * @return HTTP status code of the response.
     */
    public int getResponseStatusCode() {
        Response response = scenarioContextWithObject.getData("POST_RESPONSE", Response.class);
        assert response != null : "The POST response is not available in the context.";
        return response.getStatusCode();
    }

    /**
     * Retrieves the body of the stored response.
     *
     * @return Response body as a String.
     */
    public String getResponseBody() {
        Response response = scenarioContextWithObject.getData("POST_RESPONSE", Response.class);
        assert response != null : "The POST response is not available in the context.";
        return response.getBody().asString();
    }

    /**
     * Helper method: Parses the string to a Long if valid, otherwise returns null.
     *
     * @param value String value to parse.
     * @return Long value or null.
     */
    private Long parseLongIfValid(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            logger.warn("Unable to parse '{}' as Long.", value);
            return null;
        }
    }

    /**
     * Helper method: Parses the string to a Double if valid, otherwise returns null.
     *
     * @param value String value to parse.
     * @return Double value or null.
     */
    private Double parseDoubleIfValid(String value) {
        try {
            return (value != null && !value.isEmpty()) ? Double.parseDouble(value) : null;
        } catch (NumberFormatException e) {
            logger.warn("Unable to parse '{}' as Double.", value);
            return null;
        }
    }

    public void validateApiStatusCode(Integer int1) throws JsonProcessingException {
        try {
            // Log the raw API response string
            softAssert.assertEquals(
                    Integer.valueOf(((Response) scenarioContextWithObject.getData(TestConstants.RESPONSE)).
                            getStatusCode()),
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

    public void createCurrencyRateGetAllRequest() throws JsonProcessingException {
        try {
            logger.info("Fetching Currency Rate Details using the GET API endpoint.");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", "Bearer " + APIAuthentication.postAuthorizationCode()));
            response = APIUtils.getAllWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    EndPoints.GET_ALL_CURRENCYRATE,
                    headers
            );

            scenarioContextWithObject.setData(TestConstants.GET_RESPONSE, response);
            logger.info("GET request for Currency Rate completed. Response status code: {}",
                    ((Response) scenarioContextWithObject.
                            getData(TestConstants.GET_RESPONSE)).
                            getStatusCode());
            // Log the deserialized response object
            ObjectMapper objectMapper = new ObjectMapper();
            List<GetAllCurrencyRate> getAllCurrencyRates = objectMapper.
                    readValue(((Response) scenarioContextWithObject.getData(TestConstants.GET_RESPONSE)).asString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, GetAllCurrencyRate.class));
            logger.info("Deserialized Response: size is {}", getAllCurrencyRates.size());
            scenarioContextWithObject.setData(TestConstants.LISTOFALLAPICURRENCYRATES, getAllCurrencyRates);
        } catch (Exception e) {
            logger.error("Error occurred while fetching broker details.", e);
            throw e;
        }
    }

    public void compareResponseVsDataGiven() {
        try {
            // Fetch the stored trader details from the scenario context
            String currencyMonthYear = (String) scenarioContextWithObject.getData(TestConstants.CURRENCYMONTHYEAR);
            String currencyRateData = (String) scenarioContextWithObject.getData(TestConstants.CURRENCYRATE);
            String currencyCode = (String) scenarioContextWithObject.getData(TestConstants.CURRENCYCODE);
            String currencyId = (String) scenarioContextWithObject.getData(TestConstants.CURRENCYID);

            // Fetch the list of all traders from the scenario context
            List<GetAllCurrencyRate> allCurrencyRates = (List<GetAllCurrencyRate>) scenarioContextWithObject.
                    getData(TestConstants.LISTOFALLAPICURRENCYRATES);

            // Validate: Check if at least ONE of the traders in the list matches ALL the properties
            boolean isCurrencyRateValid = allCurrencyRates.stream()
                    .anyMatch(currencyRate ->
                            currencyRate.getId().equals(currencyId) &&
                                    currencyRate.getRate().equals(currencyRateData) &&  // Match traderId
                                    currencyRate.getCode().equals(currencyCode) &&  // Match traderName
                                    currencyRate.getMonthYear().equals(currencyMonthYear)  // Match isToraTrader
                    );

            // Soft assertion to validate the result
            softAssert.assertTrue(
                    isCurrencyRateValid,
                    String.format(
                            "Validation failed: No currency Rate in the list matches the expected properties " +
                                    "[CurrencyID: %s, CurrencyRate: %s,  Currencycode: %s, CurrencyMonthYear: %s]",
                            currencyId,
                            currencyRateData,
                            currencyCode,
                            currencyMonthYear
                    )
            );

            // Log validation success if all assertions pass
            logger.info("Currency details validated successfully using SoftAssertions.");
        } catch (Exception e) {
            logger.error("Error occurred during Currency Rate validation.", e);
            throw e;
        }
    }

    public List<CurrencyRateDbData> fetchDBDetails() throws SQLException {
        List<CurrencyRateDbData> currencyRateDbData = currencyRateDBConfig.fetchDbData();
        return currencyRateDbData;
    }

    public void validateDBvsAPIData(List<CurrencyRateDbData> currencyRateDbDataList) {

        try {
            // Retrieve the API response data from Scenario Context
            @SuppressWarnings("unchecked")
            List<GetAllCurrencyRate> getAllAPICurrencyRates = scenarioContextWithObject.getData(
                    TestConstants.LISTOFALLAPICURRENCYRATES,
                    List.class
            );

            // Log initial details about the size of both datasets
            logger.info("Validating API data against database data...");
            logger.info("API Response - Total Records: {}", getAllAPICurrencyRates.size());
            logger.info("Database Records - Total Records: {}", currencyRateDbDataList.size());

            // Sorting the DB and API datasets by ID in ascending order for proper comparison
            logger.info("Sorting both API and DB data by ID...");
            currencyRateDbDataList.sort(Comparator.comparing(CurrencyRateDbData::getMonth));
            getAllAPICurrencyRates.sort(Comparator.comparing(GetAllCurrencyRate::getMonthYear));

            // Validate that the sizes of the API and DB records match
            logger.info("Performing record count validation...");
            softAssert.assertEquals(
                    getAllAPICurrencyRates.size(),
                    currencyRateDbDataList.size(),
                    String.format("Mismatch in record count! API: %d, DB: %d",
                            getAllAPICurrencyRates.size(), currencyRateDbDataList.size())
            );

            // Perform record-by-record validation
            logger.info("Starting record-by-record validation between API and database...");
            for (int i = 0; i < currencyRateDbDataList.size(); i++) {
                CurrencyRateDbData dbRecord = currencyRateDbDataList.get(i);
                GetAllCurrencyRate apiRecord = getAllAPICurrencyRates.get(i);

                // Perform conversion for MonthYear in the DB record (e.g., 2025-07-01 → JULY-2025)
                String convertedMonthYear = convertDbMonthYearToApiFormat(dbRecord.getMonth());
                logger.info("Validating entry {}: DB Record [ID: {}, Code: {}, MonthYear: {}, " +
                                "Rate: {}] vs API Record [ID: {}, Code: {}, MonthYear: {}, Rate: {}]",
                        i + 1,
                        dbRecord.getId(), dbRecord.getCode(), convertedMonthYear, dbRecord.getRate(),
                        apiRecord.getId(), apiRecord.getCode(), apiRecord.getMonthYear(), apiRecord.getRate()
                );

                // Validate ID
                softAssert.assertEquals(
                        apiRecord.getId(),
                        dbRecord.getId(),
                        String.format("ID mismatch for Record %d: API (%s) vs DB (%s)", i + 1, apiRecord.getId(), dbRecord.getId())
                );

                // Validate Code
                softAssert.assertEquals(
                        apiRecord.getCode(),
                        dbRecord.getCode(),
                        String.format("Code mismatch for Record %d: API (%s) vs DB (%s)", i + 1, apiRecord.getCode(), dbRecord.getCode())
                );

                // Validate MonthYear
                softAssert.assertEquals(
                        apiRecord.getMonthYear(),
                        convertedMonthYear,
                        String.format("MonthYear mismatch for Record %d: API (%s) vs DB (%s)", i + 1, apiRecord.getMonthYear(),
                                dbRecord.getMonth())
                );

                // Validate Rate
                softAssert.assertEquals(
                        Double.valueOf(apiRecord.getRate()),
                        dbRecord.getRate(),
                        String.format("Rate mismatch for Record %d: API (%.4f) vs DB (%.4f)", i + 1,
                                Double.valueOf(apiRecord.getRate()), dbRecord.getRate())
                );
            }

            logger.info("Record-by-record API vs DB validation completed!");

        } catch (Exception e) {
            // Log unexpected error and rethrow
            logger.error("Unexpected error occurred while validating API data with the database.", e);
            throw new RuntimeException("Error during API vs DB validation!", e);
        }
    }

    /**
     * Converts a DB MonthYear value (e.g., "2025-07-01") to the API format (e.g., "JULY-2025").
     *
     * @param dbMonthYear the DB format MonthYear to be converted.
     * @return the converted MonthYear in API format.
     */
    private String convertDbMonthYearToApiFormat(String dbMonthYear) {
        try {
            // Parse the DB MonthYear (assumes DB format is YYYY-MM-DD)
            LocalDate date = LocalDate.parse(dbMonthYear);

            // Format the month in full uppercase (e.g., "JULY") and append the year (YYYY)
            return date.getMonth().toString() + "-" + date.getYear();
        } catch (Exception e) {
            logger.error("Error while converting DB MonthYear '{}' to API format.", dbMonthYear, e);
            throw new RuntimeException("Failed to convert DB MonthYear to API format!", e);
        }
    }

    /**
     * Sends a DELETE request to the given endpoint with the specified authorization status
     * and stores the response in `ScenarioContextWithObject`.
     *
     * @param endpoint            API endpoint to send the DELETE request to.
     * @param authorizationStatus Authorization type ("valid" or "unauthorized").
     */
    public void sendDeleteRequest(String endpoint, String authorizationStatus) {
        try {
            Map<String, String> pathData = Map.of();
            // Initialize headers for the DELETE request
            List<Header> headers = new ArrayList<>();
            if (authorizationStatus.equalsIgnoreCase("valid")) {
                String authToken = APIAuthentication.postAuthorizationCode(); // Get valid authorization token
                headers.add(new Header("Authorization", "Bearer " + authToken));
            } else {
                logger.warn("Authorization status set to 'unauthorized'. No valid token will be sent.");
                headers.add(new Header("Authorization", "Bearer INVALID_TOKEN")); // Simulate invalid token for unauthorized
            }

            // Send the DELETE request
            Response response = APIUtils.
                    deleteWithAuth(PropertiesReader.getProperty("baseUrl"),
                            endpoint, headers);

            // Log the response and store it in the scenario context
            logger.info("DELETE request sent to endpoint '{}'. Response status: {}, Response body: {}",
                    endpoint, response.getStatusCode(), response.asString());

            // Store the response in ScenarioContextWithObject
            scenarioContextWithObject.setData(TestConstants.RESPONSE, response);

        } catch (Exception e) {
            logger.error("Error occurred while sending DELETE request to endpoint '{}'.", endpoint, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a PUT request payload from the DataTable and saves it in the ScenarioContextWithObject.
     *
     * @param dataTable DataTable containing payload data.
     */
    public void createAPutRequest(DataTable dataTable) {
        Map<String, String> dataMap = dataTable.asMaps().get(0);

        CurrencyRatePutRequest request = new CurrencyRatePutRequest(
                Long.valueOf(dataMap.get("id")),
                dataMap.get("monthYear"),
                dataMap.get("code"),
                Double.valueOf(dataMap.get("rate"))
        );

        // Store the payload in ScenarioContextWithObject for later use
        scenarioContextWithObject.setData("PUT_PAYLOAD", request);
        logger.info("PUT request payload stored in ScenarioContextWithObject: {}", request);
    }

    /**
     * Sends the PUT request to the given endpoint with authorization and stores the response in ScenarioContextWithObject.
     *
     * @param endpoint            API endpoint to send the PUT request to.
     * @param authorizationStatus Authorization type ("valid" or "unauthorized").
     */
    /**
     * Sends the PUT request to the given endpoint with authorization and stores the response in `ScenarioContextWithObject`.
     *
     * @param endpoint            API endpoint to send the PUT request to.
     * @param authorizationStatus Authorization type ("valid" or "unauthorized").
     */
    public void sendPutRequest(String endpoint, String authorizationStatus) {
        try {
            // Retrieve the payload from ScenarioContextWithObject
            @SuppressWarnings("unchecked")
            CurrencyRatePutRequest payload = (CurrencyRatePutRequest) scenarioContextWithObject.getData("PUT_PAYLOAD");

            // Initialize headers for the PUT request
            List<Header> headers = new ArrayList<>();
            String jsonPayload = objectMapper.writeValueAsString(payload);

            if (authorizationStatus.equalsIgnoreCase("valid")) {
                // Add valid authorization token to headers
                String authToken = APIAuthentication.postAuthorizationCode(); // Fetch valid token
                headers.add(new Header("Content-Type", "application/json"));
                headers.add(new Header("Authorization", "Bearer " + authToken));

                logger.info("Authorization set to valid. Headers: {}", headers);
            } else if (authorizationStatus.equalsIgnoreCase("unauthorized")) {
                // Add invalid or missing token for unauthorized scenarios
                headers.add(new Header("Content-Type", "application/json"));
                headers.add(new Header("Authorization", "Bearer INVALID_TOKEN"));

                logger.warn("Authorization is set as unauthorized. Using an invalid token.");
            } else {
                // Handle the case for missing or malformed headers
                headers.add(new Header("Content-Type", "application/json")); // Only Content-Type, no Authorization
                logger.warn("Authorization is not provided. Request may fail due to missing headers.");
            }

            // Log the JSON payload being sent
            logger.info("PUT request payload: {}", jsonPayload);

            // Send the PUT request
            Response response = APIUtils.putWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    endpoint,
                    jsonPayload,
                    headers
            );

            // Log the response and store it in ScenarioContextWithObject
            logger.info("PUT request sent to '{}'. Response status: {}, Response body: {}",
                    endpoint, response.getStatusCode(), response.asString());
            scenarioContextWithObject.setData(TestConstants.RESPONSE, response);

        } catch (Exception e) {
            logger.error("Error while sending PUT request to '{}': {}", endpoint, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}