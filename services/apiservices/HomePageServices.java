package org.billing.services.apiservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.BillingMethod;
import org.billing.Context.ScenarioContext;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.api.APIUtils.APIAuthentication;
import org.billing.api.payloads.request.post.RegenerateFixedRateRequest;
import org.billing.api.payloads.request.post.TemplateRulesReqWithMinimal;
import org.billing.api.responses.InvoiceReport;
import org.billing.api.responses.InvoiceReportFile;
import org.billing.api.responses.Invoices;
import org.billing.api.responses.get.BuildResponse;
import org.billing.api.responses.post.InvoiceTemplate;
import org.billing.api.responses.post.TemplateRuleResponse;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.dbconfig.TraderDBConfig;
import org.billing.services.ui.BaseServices;
import org.billing.utils.apiutilities.APIUtils;
import org.billing.utils.general.GeneralUtlis;
import org.billing.utils.propconfig.PropertiesReader;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomePageServices extends BaseServices {
    private static final Logger logger = LogManager.getLogger(HomePageServices.class);
    private final ScenarioContextWithObject scenarioContextWithObject;
    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SoftAssert softAssert;
    private TraderDBConfig traderDBConfig = new TraderDBConfig();
    public Response response;
    private JsonPath jsonPath;
    private BillingMethod billingMethod;

    public HomePageServices(ScenarioContextWithObject scenarioContextWithObject, ScenarioContext scenarioContext) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.scenarioContext = scenarioContext;
        softAssert = SoftAssertContainer.getInstance();
    }

    public boolean executeHomeRebuildAction(String rebuildAction) {
        String baseUrl = PropertiesReader.getProperty("baseUrl");
        String endpoint = "api/home/" + rebuildAction;
        String fullUrl = baseUrl + endpoint;
        logger.info(fullUrl);

        // Use the utility to get the first date of the current month
        String buildMonth = GeneralUtlis.getFirstDateOfCurrentMonthIsoUtc();

        // Prepare headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        // Prepare request body
        String requestBody = String.format("{\"buildMonth\": \"%s\"}", buildMonth);

        // Send POST request
        Response response = APIUtils.postWithAuth(baseUrl, endpoint, requestBody, headers);

        // Validate status code (expecting 200 or 202 for async)
        int statusCode = response.getStatusCode();
        System.out.println("Response status code: " + statusCode);
        System.out.println("Response body: " + response.asString());

        return statusCode == 200 || statusCode == 202;
    }

    public boolean executeHomeRebuildAction() {
        String baseUrl = PropertiesReader.getProperty("baseUrl");
        String endpoint = "api/home/" + "EXTRACT_DATA";
        String fullUrl = baseUrl + endpoint;
        logger.info(fullUrl);

        // Use the utility to get the first date of the current month
        String buildMonth = GeneralUtlis.getFirstDateOfCurrentMonthIsoUtc();

        // Prepare headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        // Prepare request body
        String requestBody = String.format("{\"buildMonth\": \"%s\"}", buildMonth);

        // Send POST request
        Response response = APIUtils.postWithAuth(baseUrl, endpoint, requestBody, headers);

        // Validate status code (expecting 200 or 202 for async)
        int statusCode = response.getStatusCode();
        System.out.println("Response status code: " + statusCode);
        System.out.println("Response body: " + response.asString());

        return statusCode == 200 || statusCode == 202;
    }

    public void waitForBuildToBeSuccessfulForCurrentMonth() throws Exception {
        String monthToCheck = GeneralUtlis.getFirstDateOfCurrentMonthIsoUtc(); // e.g., "2025-11-01"
        String[] expectedParts = monthToCheck.split("-"); // ["2025", "11", "01"]
        String expectedYear = expectedParts[0];
        String expectedMonth = expectedParts[1];

        String baseUrl = PropertiesReader.getProperty("baseUrl");
        int size = 20;

        // Poll for up to 12 minutes, every 5 seconds
        boolean isBuilt = new FluentWait<>(null)
                .withTimeout(Duration.ofMinutes(12))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(Exception.class)
                .until(x -> {
                    int page = 0;
                    while (true) {
                        String endpoint = String.format("api/builds?page=%d&size=%d", page, size);

                        List<Header> headers = new ArrayList<>();
                        String authToken = APIAuthentication.postAuthorizationCode();
                        headers.add(new Header("Content-Type", "application/json"));
                        headers.add(new Header("Authorization", "Bearer " + authToken));

                        Response response = APIUtils.getAllWithAuth(baseUrl, endpoint, headers);

                        String responseBody = response.asString();
                        int statusCode = response.statusCode();

                        if (statusCode == 200 && (responseBody.trim().startsWith("[") ||
                                responseBody.trim().startsWith("{"))) {
                            ObjectMapper mapper = new ObjectMapper();
                            List<BuildResponse> buildsList = null;
                            try {
                                buildsList = mapper.readValue(responseBody,
                                        new com.fasterxml.jackson.core.type.TypeReference<List<BuildResponse>>() {
                                        });
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            scenarioContextWithObject.setData("BuildsList", buildsList);

                            if (buildsList == null || buildsList.isEmpty()) {
                                break; // No more data
                            }

                            for (BuildResponse build : buildsList) {
                                String[] responseParts = build.getMonth().split("-");
                                String responseYear = responseParts[0];
                                String responseMonth = responseParts[1];
                                String buildState = build.getBuildState();

                                if (expectedYear.equals(responseYear) && expectedMonth.equals(responseMonth)
                                        && "BUILT".equalsIgnoreCase(buildState)) {
                                    System.out.println("Build for " + expectedYear + "-" + expectedMonth + " is successful!");
                                    return true;
                                }
                            }
                            page++;
                        } else {
                            System.err.println("API call failed or returned non-JSON response. Status: " + statusCode);
                            break;
                        }
                    }
                    System.out.println("Build for " + expectedYear + "-" + expectedMonth + " not successful yet.");
                    return false;
                });

        if (!isBuilt) {
            throw new RuntimeException("Build for " + expectedYear + "-" + expectedMonth + " did not reach 'BUILT' state within 12 minutes.");
        }
    }

    public void waitForBuildToBeSuccessfulForChosenMonth() throws Exception {
        String monthToCheck = (String) scenarioContextWithObject.getData("TriggeredBuildMonth"); // e.g., "2025-11-01" or "2025-11"
        String expectedMonthYear = monthToCheck.length() >= 7 ? monthToCheck.substring(0, 7) : monthToCheck;

        String baseUrl = PropertiesReader.getProperty("baseUrl");
        int size = 20;

        // Poll for up to 12 minutes, every 5 seconds
        boolean isBuiltOrFailed = new FluentWait<>(null)
                .withTimeout(Duration.ofMinutes(12))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(Exception.class)
                .until(x -> {
                    int page = 0;
                    while (true) {
                        String endpoint = String.format("api/builds?page=%d&size=%d", page, size);

                        List<Header> headers = new ArrayList<>();
                        String authToken = APIAuthentication.postAuthorizationCode();
                        headers.add(new Header("Content-Type", "application/json"));
                        headers.add(new Header("Authorization", "Bearer " + authToken));

                        Response response = APIUtils.getAllWithAuth(baseUrl, endpoint, headers);
                        String responseBody = response.asString();
                        int statusCode = response.statusCode();

                        if (statusCode == 200 && (responseBody.trim().startsWith("[") || responseBody.trim().startsWith("{"))) {
                            ObjectMapper mapper = new ObjectMapper();
                            List<BuildResponse> buildsList = null;
                            try {
                                buildsList = mapper.readValue(responseBody,
                                        new com.fasterxml.jackson.core.type.TypeReference<List<BuildResponse>>() {
                                        });
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            scenarioContextWithObject.setData("BuildsList", buildsList);

                            if (buildsList == null || buildsList.isEmpty()) {
                                break; // No more data
                            }

                            for (BuildResponse build : buildsList) {
                                String buildMonthYear = build.getMonth().length() >= 7 ? build.getMonth().substring(0, 7) : build.getMonth();
                                String buildState = build.getBuildState();

                                if (expectedMonthYear.equals(buildMonthYear)) {
                                    if ("BUILT".equalsIgnoreCase(buildState)) {
                                        System.out.println("Build for " + expectedMonthYear + " is successful!");
                                        return true;
                                    } else if ("FAILED".equalsIgnoreCase(buildState)) {
                                        System.out.println("Build for " + expectedMonthYear + " is Failed!");
                                        throw new RuntimeException("Build for " + expectedMonthYear + " is Failed!");
                                    }
                                }
                            }
                            page++;
                        } else {
                            System.err.println("API call failed or returned non-JSON response. Status: " + statusCode);
                            break;
                        }
                    }
                    System.out.println("Build for " + expectedMonthYear + " not successful yet.");
                    return false;
                });

        if (!isBuiltOrFailed) {
            throw new RuntimeException("Build for " + monthToCheck + " did not reach 'BUILT' state within 12 minutes.");
        }
    }


    public String selectAndTriggerFirstBuildWithGivenOrderType(String orderType) throws Exception {
        String baseUrl = PropertiesReader.getProperty("baseUrl");
        int page = 0;
        int size = 20;
        boolean found = false;
        String monthWithData = null;

        // Prepare headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));
        ObjectMapper mapper = new ObjectMapper();

        while (!found) {
            String getEndpoint = String.format("api/builds?page=%d&size=%d", page, size);
            Response response = APIUtils.getAllWithAuth(baseUrl, getEndpoint, headers);
            String responseBody = response.asString();
            int statusCode = response.statusCode();

            if (statusCode != 200 || !(responseBody.trim().startsWith("[") || responseBody.trim().startsWith("{"))) {
                throw new RuntimeException("API call failed or returned non-JSON response. Status: " + statusCode);
            }

            List<BuildResponse> buildsList = mapper.readValue(responseBody,
                    new com.fasterxml.jackson.core.type.TypeReference<List<BuildResponse>>() {
                    });

            if (buildsList == null || buildsList.isEmpty()) {
                break; // No more data
            }

            // Try to find a non-zero month in this page
            monthWithData = findFirstNonZeroMonth(buildsList, orderType);
            if (monthWithData != null) {
                found = true;
                break;
            }
            page++;
        }

        if (monthWithData == null) {
            throw new RuntimeException("No month found with non-zero " + orderType + " in all pages.");
        }
        System.out.println("First month with non-zero " + orderType + ": " + monthWithData);

        // Store in scenario context for later use
        scenarioContextWithObject.setData("TriggeredBuildMonth", monthWithData);

        // 3. Trigger build for that month
        String postEndpoint = "api/home/" + orderType;
        String requestBody = String.format("{\"buildMonth\": \"%s\"}", monthWithData);

        Response postResponse = APIUtils.postWithAuth(baseUrl, postEndpoint, requestBody, headers);
        logger.info("the reposne body is " + postResponse.asString());
        int postStatus = postResponse.getStatusCode();
        System.out.println("Triggered build for " + monthWithData + " with status: " + postStatus);

        if (postStatus == 200 || postStatus == 202) {
            return monthWithData;
        } else {
            throw new RuntimeException("Failed to trigger build for " + monthWithData + ". Status: " + postStatus);
        }
    }

    public String findFirstNonZeroMonth(List<BuildResponse> buildsList, String rateType) {
        for (BuildResponse build : buildsList) {
            switch (rateType) {
                case "EXTRACT_DATA":
                    if (build.getOrdersCount() > 0) return build.getMonth();
                    break;
                case "FIXED_RATES":
                    if (build.getFixedRatesCount() > 0) return build.getMonth();
                    break;
                case "CONNECTIVITY_RATES":
                    if (build.getConnectivityRatesCount() > 0) return build.getMonth();
                    break;
                case "CONSULTING_RATES":
                    if (build.getDevelopmentRatesCount() > 0) return build.getMonth();
                    break;
                case "NETWORK_RATES":
                    if (build.getAgencyRatesCount() > 0) return build.getMonth();
                    break;
            }
        }
        return null; // Not found
    }


    public void searchAndSelectInvoiceTemplateForRateRuleLinking(String billingMethod, String type) throws Exception {
        getARandomInvoiceTemplateAndStoreInContext(billingMethod, type);
    }


    public void getARandomInvoiceTemplateAndStoreInContext(String billingMethod, String type) throws Exception {
        String baseUrl = PropertiesReader.getProperty("baseUrl");
        int page = 0;
        int size = 20;
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));
        ObjectMapper mapper = new ObjectMapper();

        String endpoint = String.
                format("api/invoice-templates/%s/%s?page=%d&size=%d", type, billingMethod, page, size);
        Response response = APIUtils.getAllWithAuth(baseUrl, endpoint, headers);
        String responseBody = response.asString();
        System.out.println(responseBody);

        List<InvoiceTemplate> templates = mapper.readValue(responseBody,
                new com.fasterxml.jackson.core.type.TypeReference<List<InvoiceTemplate>>() {
                });
        System.out.println(templates.size());
        if (templates == null || templates.isEmpty()) {
            throw new RuntimeException("No invoice templates found.");
        }

        // Select one random invoice template
        InvoiceTemplate randomTemplate = templates.get(new Random().nextInt(templates.size()));
        String invoiceTemplateName = randomTemplate.getName(); // Or any other property you need
        String invoiceTemplateId = String.valueOf(randomTemplate.getId());
        scenarioContextWithObject.setData("InvoiceTemplateID", invoiceTemplateId);
        scenarioContextWithObject.setData("InvoiceTemplateName", invoiceTemplateName);
        scenarioContextWithObject.setData("BillingMethod", billingMethod);
        scenarioContextWithObject.setData("TemplateType", type);
        System.out.println("Random Invoice Template name from invoice template: " + invoiceTemplateName);
    }

    public void createATemplateRuleBasedOnOrderData() {
    }

    public void linkTemplateRuleToInvoiceTemplate() {
    }

    public void getTheLisOfBrokersAndClients() throws JsonProcessingException, InterruptedException {
        executeRegenerateAll();
        fetchTheGeneratedallInvoicesData();
    }

    public void fetchInvoicesByRandomBrokerName() throws JsonProcessingException {
        // Retrieve dynamic values from scenario context
        String brokerName = (String) scenarioContextWithObject.getData("RandomBrokerName");
        String type = (String) scenarioContextWithObject.getData("Type"); // e.g., "BROKER"
        String billingMethod = (String) scenarioContextWithObject.getData("BillingMethod"); // e.g., "FIXED"
        String formattedMonth = (String) scenarioContextWithObject.getData("FormattedMonth"); // e.g., "December-2025"

        if (brokerName == null || brokerName.isEmpty()) {
            throw new IllegalStateException("RandomBrokerName is not set in scenario context.");
        }
        if (type == null || billingMethod == null || formattedMonth == null) {
            throw new IllegalStateException("Type, BillingMethod, or FormattedMonth is not set in scenario context.");
        }

        // Construct the endpoint URL dynamically
        String baseUrl = "https://apac.dev.poems.refinitiv.com/billing/";
        String endpoint = String.format("api/invoices/%s/%s/%s?name=%s",
                type, billingMethod, formattedMonth, brokerName);

        // Prepare headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        // Make the GET request
        Response response = APIUtils.getAllWithAuth(baseUrl, endpoint, headers);

        // Print or process the response
        System.out.println("Invoices for broker '" + brokerName + "': " + response.asString());

        // Optionally, store the response in scenario context
        scenarioContextWithObject.setData("InvoicesByRandomBroker", response.asString());
        // Parse the response into a List<Invoices>
        ObjectMapper mapper = new ObjectMapper();
        List<Invoices> invoicesList = mapper.readValue(response.asString(),
                new com.fasterxml.jackson.core.type.TypeReference<List<Invoices>>() {
                });

        if (invoicesList == null || invoicesList.isEmpty()) {
            throw new RuntimeException("No invoice found for broker: " + brokerName);
        }

        // Since only one invoice is expected, return the first one
        Invoices invoice = invoicesList.get(0);

        // Optionally, store the invoice in scenario context
        scenarioContextWithObject.setData("InvoiceByRandomBroker", invoice);

        // Print details for verification
        System.out.println("Invoice Name: " + invoice.getName());
        if (invoice.getInvoiceReports() != null) {
            for (InvoiceReport report : invoice.getInvoiceReports()) {
                System.out.println("  Report ID: " + report.getReportId());
                System.out.println("  Build Month: " + report.getBuildMonth());
                // ... print more details as needed
            }
        }

    }

    public void fetchInvoicesByRandomClientName() throws JsonProcessingException {
        // Retrieve dynamic values from scenario context
        String clientName = (String) scenarioContextWithObject.getData("RandomClientName");
        String type = (String) scenarioContextWithObject.getData("Type"); // e.g., "Client"
        String billingMethod = (String) scenarioContextWithObject.getData("BillingMethod"); // e.g., "FIXED"
        String formattedMonth = (String) scenarioContextWithObject.getData("FormattedMonth"); // e.g., "December-2025"

        if (clientName == null || clientName.isEmpty()) {
            throw new IllegalStateException("RandomBrokerName is not set in scenario context.");
        }
        if (type == null || billingMethod == null || formattedMonth == null) {
            throw new IllegalStateException("Type, BillingMethod, or FormattedMonth is not set in scenario context.");
        }

        // Construct the endpoint URL dynamically
        String baseUrl = "https://apac.dev.poems.refinitiv.com/billing/";
        String endpoint = String.format("api/invoices/%s/%s/%s?name=%s",
                type, billingMethod, formattedMonth, clientName);

        // Prepare headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        // Make the GET request
        Response response = APIUtils.getAllWithAuth(baseUrl, endpoint, headers);

        // Print or process the response
        System.out.println("Invoices for broker '" + clientName + "': " + response.asString());

        // Optionally, store the response in scenario context
        scenarioContextWithObject.setData("InvoicesByRandomBroker", response.asString());
        // Parse the response into a List<Invoices>
        ObjectMapper mapper = new ObjectMapper();
        List<Invoices> invoicesList = mapper.readValue(response.asString(),
                new com.fasterxml.jackson.core.type.TypeReference<List<Invoices>>() {
                });

        if (invoicesList == null || invoicesList.isEmpty()) {
            throw new RuntimeException("No invoice found for Client: " + clientName);
        }

        // Since only one invoice is expected, return the first one
        Invoices invoice = invoicesList.get(0);

        // Optionally, store the invoice in scenario context
        scenarioContextWithObject.setData("InvoiceByRandomClient", invoice);

        // Print details for verification
        System.out.println("Invoice Name: " + invoice.getName());
        if (invoice.getInvoiceReports() != null) {
            for (InvoiceReport report : invoice.getInvoiceReports()) {
                System.out.println("  Report ID: " + report.getReportId());
                System.out.println("  Build Month: " + report.getBuildMonth());
                // ... print more details as needed
            }
        }

    }

    private void fetchTheGeneratedallInvoicesData() throws JsonProcessingException, InterruptedException {
        String baseUrl = (String) scenarioContextWithObject.getData("BaseUrl");
        String getEndpoint = (String) scenarioContextWithObject.getData("GetEndpoint");
        List<Header> headers = (List<Header>) scenarioContextWithObject.getData("Headers");
        String type = (String) scenarioContextWithObject.getData("Type");

        ObjectMapper mapper = new ObjectMapper();

        // Poll GET endpoint for up to 3 minutes, every 5 seconds
        boolean foundInvoices = new FluentWait<>(null)
                .withTimeout(Duration.ofMinutes(3))
                .pollingEvery(Duration.ofSeconds(5))
                .ignoring(Exception.class)
                .until(x -> {
                    Response getResponse = APIUtils.getAllWithAuth(baseUrl, getEndpoint, headers);
                    List<Invoices> invoicesList = null;
                    try {
                        invoicesList = mapper.readValue(getResponse.asString(),
                                new com.fasterxml.jackson.core.type.TypeReference<List<Invoices>>() {
                                });
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Polling for invoices: found " + (invoicesList != null ? invoicesList.size() : 0));
                    if (invoicesList != null && !invoicesList.isEmpty()) {
                        scenarioContextWithObject.setData(type + "_Invoices", getResponse.asString());
                        // Pick a random broker or client from the invoices list
                        int randomIndex = new Random().nextInt(invoicesList.size());
                        String randomName = invoicesList.get(randomIndex).getName();
                        if ("BROKER".equalsIgnoreCase(type)) {
                            scenarioContextWithObject.setData("RandomBrokerName", randomName);
                            System.out.println("Random Broker Name: " + randomName);
                        } else if ("CLIENT".equalsIgnoreCase(type)) {
                            scenarioContextWithObject.setData("RandomClientName", randomName);
                            System.out.println("Random Client Name: " + randomName);
                        }
                        return true;
                    }
                    return false;
                });

        if (!foundInvoices) {
            throw new RuntimeException("No invoices data received after polling for 3 minutes.");
        }
    }

    private void executeRegenerateAll() throws JsonProcessingException, InterruptedException {
        String baseUrl = PropertiesReader.getProperty("baseUrl");
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        // Get values from scenario context
        String billingMethod = (String) scenarioContextWithObject.getData("BillingMethod");
        String type = (String) scenarioContextWithObject.getData("TemplateType");
        String monthToCheck = (String) scenarioContextWithObject.getData("TriggeredBuildMonth");

        // Convert monthToCheck from "yyyy-MM-dd" to "MMMM-yyyy"
        LocalDate date = LocalDate.parse(monthToCheck);
        String formattedMonth = date.format(DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.ENGLISH));
        scenarioContextWithObject.setData("FormattedMonth", formattedMonth);
        String postEndpoint = "";
        String getEndpoint = "";
        RegenerateFixedRateRequest request = new RegenerateFixedRateRequest();
        request.setOverrideInvoiceDate(false);
        request.setOverridePaymentDueDate(false);
        request.setUponReceipt(false);
        request.setFastMonth(false);

        ObjectMapper mapper = new ObjectMapper();
        String jsonRequest = mapper.writeValueAsString(request);

        switch (type.toUpperCase()) {
            case "BROKER":
                postEndpoint = String.format("api/invoices/BROKER/%s/%s/generateAll", billingMethod, formattedMonth);
                getEndpoint = String.format("api/invoices/BROKER/%s/%s", billingMethod, formattedMonth);
                break;
            case "CLIENT":
                postEndpoint = String.format("api/invoices/CLIENT/%s/%s/generateAll", billingMethod, formattedMonth);
                getEndpoint = String.format("api/invoices/CLIENT/%s/%s", billingMethod, formattedMonth);
                break;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }

// POST to generate all invoices
        Response postResponse = APIUtils.postWithAuth(baseUrl, postEndpoint, jsonRequest, headers);
        System.out.println("POST response: " + postResponse.asString());

// Store necessary data in scenario context for GET
        scenarioContextWithObject.setData("BaseUrl", baseUrl);
        scenarioContextWithObject.setData("GetEndpoint", getEndpoint);
        scenarioContextWithObject.setData("Headers", headers);
        scenarioContextWithObject.setData("Type", type);
    }

    public void lookForCreatedTemplateRuleAndMakeItHighPriority() throws JsonProcessingException {
        TemplateRuleResponse templateRuleResponse =
                ((TemplateRuleResponse) scenarioContextWithObject.getData("TemplateRuleResponse"));
        // Build the update request with priority set to 1
        TemplateRulesReqWithMinimal updateReq = TemplateRulesReqWithMinimal.builder()
                .priority(1)
                .id((String) scenarioContextWithObject.getData("TemplateRuleId"))
                .templateName(templateRuleResponse.getTemplateName())
                .brokerName(templateRuleResponse.getBrokerName())
                .clientName(templateRuleResponse.getClientName())
                .billingMethod(templateRuleResponse.getBillingMethod())
                .build();
        ObjectMapper objectMapper1 = new ObjectMapper();
        String updateReqString = objectMapper1.writeValueAsString(updateReq);

        String baseUrl = PropertiesReader.getProperty("baseUrl");
        String billingMethod = (String) scenarioContextWithObject.getData("BillingMethod");
        String type = (String) scenarioContextWithObject.getData("TemplateType");

        String endpoint = String.format("api/template-rules/%s/%s/update", type, billingMethod);
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

// Send PUT request
        Response putResponse = APIUtils.putWithAuth(
                baseUrl,
                endpoint, updateReqString
                ,
                headers
        );

        scenarioContextWithObject.setData("UpdateTemplateRuleResponse", putResponse);
        logger.info("Template rule updated via API. Response: " + putResponse.asString());
    }

    public void validateTheGeneratedInvoiceAsPerLinkedTemplate() throws JsonProcessingException, InterruptedException {
        executeRegenerateAll();
        if ("CLIENT".equalsIgnoreCase((String) scenarioContextWithObject.getData("Type")))
            fetchInvoicesByRandomClientName();
        else {
            fetchInvoicesByRandomBrokerName();
        }
        validateInvoicesTemplateMatchingWithRetry();
    }


    private void validateInvoicesTemplateMatchingWithRetry() throws InterruptedException, JsonProcessingException {
        String expectedTemplateName = (String) scenarioContextWithObject.getData("InvoiceTemplateName");
        if (expectedTemplateName == null || expectedTemplateName.isEmpty()) {
            throw new IllegalStateException("InvoiceTemplateName is not set in scenario context.");
        }

        String type = (String) scenarioContextWithObject.getData("Type");
        String billingMethod = (String) scenarioContextWithObject.getData("BillingMethod");
        String formattedMonth = (String) scenarioContextWithObject.getData("FormattedMonth");

        if (type == null || billingMethod == null || formattedMonth == null) {
            throw new IllegalStateException("Required endpoint parameters are missing in scenario context.");
        }

        String baseUrl = "https://apac.dev.poems.refinitiv.com/billing/";
        String endpoint;
        if ("BROKER".equalsIgnoreCase(type)) {
            String brokerName = (String) scenarioContextWithObject.getData("RandomBrokerName");
            if (brokerName == null) {
                throw new IllegalStateException("RandomBrokerName is not set in scenario context for BROKER type.");
            }
            endpoint = String.format("api/invoices/%s/%s/%s?name=%s", type, billingMethod, formattedMonth, brokerName);
        } else if ("CLIENT".equalsIgnoreCase(type)) {
            String clientName = (String) scenarioContextWithObject.getData("RandomClientName");
            if (clientName == null) {
                throw new IllegalStateException("RandomClientName is not set in scenario context for CLIENT type.");
            }
            endpoint = String.format("api/invoices/%s/%s/%s?name=%s", type, billingMethod, formattedMonth, clientName);
        } else {
            throw new IllegalStateException("Unsupported Type: " + type);
        }

        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        ObjectMapper mapper = new ObjectMapper();

        // Poll for up to 2 minutes, every 2 seconds
        boolean templateNameMatches = new FluentWait<>(null)
                .withTimeout(Duration.ofMinutes(2))
                .pollingEvery(Duration.ofSeconds(2))
                .ignoring(Exception.class)
                .until(x -> {
                    Response response = APIUtils.getAllWithAuth(baseUrl, endpoint, headers);
                    List<Invoices> invoicesList = null;
                    try {
                        invoicesList = mapper.readValue(response.asString(),
                                new com.fasterxml.jackson.core.type.TypeReference<List<Invoices>>() {
                                });
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    if (invoicesList != null && !invoicesList.isEmpty()) {
                        Invoices invoice = invoicesList.get(0);
                        scenarioContextWithObject.setData("InvoiceByRandomBrokerOrClient", invoice);

                        if (invoice.getInvoiceReports() != null && !invoice.getInvoiceReports().isEmpty()) {
                            InvoiceReport firstReport = invoice.getInvoiceReports().get(0);
                            if (firstReport.getFiles() != null && !firstReport.getFiles().isEmpty()) {
                                InvoiceReportFile firstFile = firstReport.getFiles().get(0);
                                if (expectedTemplateName.equals(firstFile.getInvoiceTemplateName())) {
                                    System.out.println("Template name matches: " + expectedTemplateName);
                                    return true;
                                } else {
                                    System.out.println("Template name does not match. Expected: " +
                                            expectedTemplateName + ", Found: " + firstFile.getInvoiceTemplateName());
                                }
                            }
                        }
                    }
                    return false;
                });

        scenarioContextWithObject.setData("TemplateNameMatches", templateNameMatches);

        if (!templateNameMatches) {
            System.out.println("Template name did not match after polling for 2 minutes.");
        }
    }
}
