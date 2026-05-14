package org.billing.utils.apiutilities;

import io.restassured.http.Header;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.TestConstants;
import org.billing.api.APIUtils.APIAuthentication;
import org.billing.api.endpoints.EndPoints;
import org.billing.services.apiservices.CapRulesService;
import org.billing.utils.propconfig.PropertiesReader;

import java.util.*;

public class GenericUtils {
    private static final Logger logger = LogManager.getLogger(GenericUtils.class);
    private Response response;
    private ScenarioContextWithObject scenarioContextWithObject;

    public GenericUtils(ScenarioContextWithObject scenarioContextWithObject) {
        this.scenarioContextWithObject = scenarioContextWithObject;
    }
    public void createDeleteRequest(String endpoint, String id) {
        // Start logging the API call
        logger.info("Starting DELETE request to Endpoint: {}", endpoint);

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode(); // Get authorization token
        headers.add(new Header("Content-Type", "application/json")); // Add Content-Type header
        headers.add(new Header("Authorization", "Bearer " + authToken)); // Add Authorization header

        // Setup path variables
        Map<String, String> pathData = new HashMap<>();
        pathData.put("id",id);

        // Log detailed header information
        logger.info("Authorization Token Generated: {}", authToken);
        logger.info("Headers: {}", headers);

        // Making the DELETE API call
        logger.info("Initiating DELETE request...");
        response = APIUtils.deleteWithAuthAndPath(
                PropertiesReader.getProperty("baseUrl"),
                pathData,
                endpoint, // Base URL from property
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

    public static String getRandomLetters(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // Generate a random uppercase letter (A-Z)
            char letter = (char) ('A' + random.nextInt(26));
            sb.append(letter);
        }
        return sb.toString();
    }
}
