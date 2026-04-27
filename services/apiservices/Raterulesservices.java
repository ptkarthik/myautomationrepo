package org.billing.services.apiservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.BillingMethod;
import org.billing.Context.ScenarioContext;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.TestConstants;
import org.billing.api.APIUtils.APIAuthentication;
import org.billing.api.payloads.request.post.*;
import org.billing.api.responses.post.FixedRate.FixedRateRuleResponse;
import org.billing.api.responses.post.agency.AgencyRateRuleResponse;
import org.billing.api.responses.post.connectivity.ConnectivityRateRuleResponse;
import org.billing.api.responses.post.development.DevelopmentRateRuleResponse;
import org.billing.api.responses.post.volumeraterules.RateRuleResponse;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.dbconfig.TraderDBConfig;
import org.billing.services.ui.BaseServices;
import org.billing.utils.apiutilities.APIUtils;
import org.billing.utils.propconfig.PropertiesReader;
import org.testng.asserts.SoftAssert;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Raterulesservices extends BaseServices {
    private static final Logger logger = LogManager.getLogger(Raterulesservices.class);
    private final ScenarioContextWithObject scenarioContextWithObject;
    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SoftAssert softAssert;
    private TraderDBConfig traderDBConfig = new TraderDBConfig();
    public Response response;
    private JsonPath jsonPath;
    private BillingMethod billingMethod;

    public Raterulesservices(ScenarioContextWithObject scenarioContextWithObject, ScenarioContext scenarioContext) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.scenarioContext = scenarioContext;
        softAssert = SoftAssertContainer.getInstance();
    }

    public void selectTheBillingMethod(String methodString) {
        this.billingMethod = BillingMethod.fromString(methodString);
        logger.info("Selected billing method: " + this.billingMethod);
        scenarioContextWithObject.setData("billingMethod", billingMethod);

    }

    public BillingMethod getBillingMethod() {
        return (BillingMethod) scenarioContextWithObject.getData("billingMethod");
    }


    public void sendPostRequest(DataTable dataTable) {
        try {
            BillingMethod billingMethod = getBillingMethod();
            List<Object> responses = new ArrayList<>();

            switch (billingMethod) {
                case VOLUME:
                    responses = handleVolumeRateRule(dataTable);
                    break;
                case FIXED:
                    responses = handleFixedRateRule(dataTable);
                    break;
                case DEVELOPMENT:
                    responses = handleDevelopmentRateRule(dataTable);
                    break;
                case CONNECTIVITY:
                    responses = handleConnectivityRateRule(dataTable);
                    break;
                case AGENCY:
                    responses = handleAgencyRateRule(dataTable);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported billing method: " + billingMethod);
            }

            // Store all responses in Scenario Context
            scenarioContextWithObject.setData(TestConstants.RESPONSE, responses);
            logger.info(responses);

        } catch (Exception e) {
            logger.error("Error while sending POST request.", e);
            throw new RuntimeException(e);
        }
    }

    public List<DevelopmentRateRuleRequest> buildPostRequestPayloadsForDevelopment(BillingMethod billingMethod, DataTable dataTable) {
        List<DevelopmentRateRuleRequest> payloads = new ArrayList<>();
        switch (billingMethod) {
            case DEVELOPMENT:
                LocalDate now = LocalDate.now();
                LocalDate firstDay = now.withDayOfMonth(1);
                LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00.000'Z'");
                String startDate = firstDay.format(formatter);
                String endDate = lastDay.format(formatter);
                for (Map<String, String> row : dataTable.asMaps()) {
                    DevelopmentRateRuleRequest request = DevelopmentRateRuleRequest.builder()
                            .brokerName(emptyIfNull(row.get("broker")))
                            .clientName(emptyIfNull(row.get("client")))
                            .rateType(emptyIfNull("MONTHLY"))
                            .description(emptyIfNull("Description"))
                            .description2(emptyIfNull("Description"))
                            .displayGroup(emptyIfNull("LEADING"))
                            .startDate(startDate)
                            .endDate(endDate)
                            .value(emptyIfNull(row.get("rate")))
                            .currency(emptyIfNull("USD"))
                            .build();
                    payloads.add(request);
                }
                scenarioContextWithObject.setData("REQUEST_MAPS", payloads);
                break;
            default:
                throw new IllegalArgumentException("Unknown billing method: " + billingMethod);
        }
        return payloads;
    }

    public List<AgencyRateRuleRequest> buildPostRequestPayloadsForAgency(BillingMethod billingMethod, DataTable dataTable) {
        List<AgencyRateRuleRequest> payloads = new ArrayList<>();
        switch (billingMethod) {
            case AGENCY:
                LocalDate now = LocalDate.now();
                LocalDate firstDay = now.withDayOfMonth(1);
                LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00.000'Z'");
                String startDate = firstDay.format(formatter);
                String endDate = lastDay.format(formatter);
                for (Map<String, String> row : dataTable.asMaps()) {
                    AgencyRateRuleRequest request = AgencyRateRuleRequest.builder()
                            .brokerName(emptyIfNull(row.get("broker")))
                            .clientName(emptyIfNull(row.get("client")))
                            .description(emptyIfNull("Description"))
                            .region(emptyIfNull("US"))
                            .rateType(emptyIfNull("MONTHLY"))
                            .startDate(emptyIfNull(startDate))
                            .endDate(emptyIfNull(endDate))
                            .value(emptyIfNull(row.get("rate")))
                            .currency(emptyIfNull("USD"))
                            .chargeDay(row.get("chargeDay") != null ? Integer.valueOf(row.get("chargeDay")) : 15)
                            .build();
                    payloads.add(request);
                }
                scenarioContextWithObject.setData("REQUEST_MAPS", payloads);
                break;
            default:
                throw new IllegalArgumentException("Unknown billing method: " + billingMethod);
        }
        return payloads;
    }

    private List<Object> handleAgencyRateRule(DataTable dataTable) throws Exception {
        logger.info("Building Agency Rate Rule payloads.");
        List<AgencyRateRuleRequest> payloads = buildPostRequestPayloadsForAgency(BillingMethod.AGENCY, dataTable);

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        List<Object> responses = new ArrayList<>();

        for (int i = 0; i < payloads.size(); i++) {
            AgencyRateRuleRequest payload = payloads.get(i);
            String jsonPayload = objectMapper.writeValueAsString(payload);

            Response response = APIUtils.postWithAuth(PropertiesReader.getProperty("baseUrl"),
                    "api/rate-rules/agency/create", jsonPayload, headers);
            AgencyRateRuleResponse responsePojo = objectMapper.readValue(response.asString(), AgencyRateRuleResponse.class);
            responses.add(responsePojo);
            logger.info("POST request sent. Response status: {}", response.getStatusCode());
        }
        scenarioContextWithObject.setData("REQUEST_MAPS", payloads);
        return responses;
    }

    private void validateAgencyRateRuleResponses(List<Object> responses) {
        List<AgencyRateRuleRequest> requestMaps = (List<AgencyRateRuleRequest>)
                scenarioContextWithObject.getData("REQUEST_MAPS");
        for (int i = 0; i < responses.size(); i++) {
            Object resp = responses.get(i);
            AgencyRateRuleRequest requestMap = requestMaps.get(i);

            if (resp instanceof AgencyRateRuleResponse) {
                AgencyRateRuleResponse agencyRateRuleResponse = (AgencyRateRuleResponse) resp;

                // Validate request fields against response
                validateAgencyRequestAgainstResponse(requestMap, agencyRateRuleResponse);
            } else {
                logger.warn("Unexpected response type: " + resp.getClass().getName());
            }
        }
    }

    private void validateAgencyRequestAgainstResponse(AgencyRateRuleRequest request, AgencyRateRuleResponse response) {
        Field[] fields = AgencyRateRuleRequest.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object reqValue = field.get(request);
                if (reqValue == null || (reqValue instanceof String && ((String) reqValue).isEmpty())) {
                    continue; // Skip empty or null fields
                }

                String key = field.getName();
                Object respValue = null;

                switch (key) {
                    case "brokerName":
                        respValue = response.getBrokerName();
                        break;
                    case "clientName":
                        respValue = response.getClientName();
                        break;
                    case "description":
                        respValue = response.getDescription();
                        break;
                    case "region":
                        respValue = response.getRegion();
                        break;
                    case "rateType":
                        respValue = response.getRateType();
                        break;
                    case "startDate":
                        respValue = normalizeDate(response.getStartDate());
                        reqValue = normalizeDate((String) reqValue);
                        break;
                    case "endDate":
                        respValue = normalizeDate(response.getEndDate());
                        reqValue = normalizeDate((String) reqValue);
                        break;
                    case "value":
                        respValue = response.getValue();
                        reqValue = Integer.valueOf((String) reqValue);
                        break;
                    case "currency":
                        respValue = response.getCurrency();
                        break;
                    case "chargeDay":
                        respValue = response.getChargeDay();
                        break;
                    // Add more fields as needed
                    default:
                        break;
                }
                softAssert.assertEquals(respValue, reqValue, "Mismatch for field: " + key);
            } catch (IllegalAccessException e) {
                logger.error("Error accessing field: " + field.getName(), e);
            }
        }
    }

    private void validateDevelopmentRateRuleResponses(List<Object> responses) {
        List<DevelopmentRateRuleRequest> requestMaps = (List<DevelopmentRateRuleRequest>)
                scenarioContextWithObject.getData("REQUEST_MAPS");
        for (int i = 0; i < responses.size(); i++) {
            Object resp = responses.get(i);
            DevelopmentRateRuleRequest requestMap = requestMaps.get(i);

            if (resp instanceof DevelopmentRateRuleResponse) {
                DevelopmentRateRuleResponse developmentRateRuleResponse = (DevelopmentRateRuleResponse) resp;

                // Validate request fields against response
                validateDevelopmentRequestAgainstResponse(requestMap, developmentRateRuleResponse);
            } else {
                logger.warn("Unexpected response type: " + resp.getClass().getName());
            }
        }
    }

    private void validateDevelopmentRequestAgainstResponse(DevelopmentRateRuleRequest request, DevelopmentRateRuleResponse response) {
        Field[] fields = DevelopmentRateRuleRequest.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object reqValue = field.get(request);
                if (reqValue == null || (reqValue instanceof String && ((String) reqValue).isEmpty())) {
                    continue; // Skip empty or null fields
                }

                String key = field.getName();
                Object respValue = null;

                switch (key) {
                    case "brokerName":
                        respValue = response.getBrokerName();
                        break;
                    case "clientName":
                        respValue = response.getClientName();
                        break;
                    case "rateType":
                        respValue = response.getRateType();
                        break;
                    case "description":
                        respValue = response.getDescription();
                        break;
                    case "description2":
                        respValue = response.getDescription2();
                        break;
                    case "displayGroup":
                        respValue = response.getDisplayGroup();
                        break;
                    case "startDate":
                        respValue = normalizeDate(response.getStartDate());
                        reqValue = normalizeDate((String) reqValue);
                        break;
                    case "endDate":
                        respValue = normalizeDate(response.getEndDate());
                        reqValue = normalizeDate((String) reqValue);
                        break;
                    case "value":
                        respValue = response.getValue();
                        reqValue = Integer.valueOf((String) reqValue);
                        break;
                    case "currency":
                        respValue = response.getCurrency();
                        break;
                    // Add more fields as needed
                    default:
                        break;
                }
                softAssert.assertEquals(respValue, reqValue, "Mismatch for field: " + key);
            } catch (IllegalAccessException e) {
                logger.error("Error accessing field: " + field.getName(), e);
            }
        }
    }

    private List<Object> handleDevelopmentRateRule(DataTable dataTable) throws Exception {
        logger.info("Building Development Rate Rule payloads.");
        List<DevelopmentRateRuleRequest> payloads = buildPostRequestPayloadsForDevelopment(BillingMethod.DEVELOPMENT, dataTable);

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        List<Object> responses = new ArrayList<>();

        for (int i = 0; i < payloads.size(); i++) {
            DevelopmentRateRuleRequest payload = payloads.get(i);
            String jsonPayload = objectMapper.writeValueAsString(payload);

            Response response = APIUtils.postWithAuth(PropertiesReader.getProperty("baseUrl"),
                    "api/rate-rules/consulting/create", jsonPayload, headers);
            DevelopmentRateRuleResponse responsePojo = objectMapper.readValue(response.asString(), DevelopmentRateRuleResponse.class);
            responses.add(responsePojo);
            logger.info("POST request sent. Response status: {}", response.getStatusCode());
        }
        scenarioContextWithObject.setData("REQUEST_MAPS", payloads);
        return responses;
    }

    private List<Object> handleVolumeRateRule(DataTable dataTable) throws Exception {
        logger.info("Building Volume Rate Rule payloads.");
        List<VolumeRateRuleRequest> payloads = buildVolumePostRequestPayloads(BillingMethod.VOLUME, dataTable);

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        List<Object> responses = new ArrayList<>();

        for (int i = 0; i < payloads.size(); i++) {
            VolumeRateRuleRequest payload = payloads.get(i);
            String jsonPayload = objectMapper.writeValueAsString(payload);

            Response response = APIUtils.postWithAuth(PropertiesReader.getProperty("baseUrl"),
                    "api/rate-rules/volume/create", jsonPayload, headers);
            RateRuleResponse responsePojo = objectMapper.readValue(response.asString(), RateRuleResponse.class);
            responses.add(responsePojo);
            logger.info("POST request sent. Response status: {}", response.getStatusCode());
        }
        return responses;
    }

    public void handleSingleVolumeRateRule() throws Exception {
        logger.info("Building Volume Rate Rule payloads.");
        VolumeRateRuleRequest volumePayLoad = buildVolumePostRequestPayloads();

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        String jsonPayload = objectMapper.writeValueAsString(volumePayLoad);
        Response response = APIUtils.postWithAuth(PropertiesReader.getProperty("baseUrl"),
                "api/rate-rules/volume/create", jsonPayload, headers);
        RateRuleResponse responsePojo = objectMapper.readValue(response.asString(), RateRuleResponse.class);
        scenarioContextWithObject.setData("Response", responsePojo);
        logger.info("POST request sent. Response status: {}", response.getStatusCode());
        logger.info(response.asString());

        logger.info(response.asString());
    }

    private List<Object> handleFixedRateRule(DataTable dataTable) throws Exception {
        logger.info("Building Fixed Rate Rule payloads.");
        List<FixedRateRuleRequest> payloads = buildPostRequestPayloadsForFixed(BillingMethod.FIXED, dataTable);

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        List<Object> responses = new ArrayList<>();

        for (int i = 0; i < payloads.size(); i++) {
            FixedRateRuleRequest payload = payloads.get(i);
            String jsonPayload = objectMapper.writeValueAsString(payload);

            Response response = APIUtils.postWithAuth(PropertiesReader.getProperty("baseUrl"),
                    "api/rate-rules/fixed/create", jsonPayload, headers);
            FixedRateRuleResponse responsePojo = objectMapper.readValue(response.asString(), FixedRateRuleResponse.class);
            responses.add(responsePojo);
            logger.info("POST request sent. Response status: {}", response.getStatusCode());
        }
        scenarioContextWithObject.setData("REQUEST_MAPS", payloads);
        return responses;
    }

    public List<FixedRateRuleRequest> buildPostRequestPayloadsForFixed(BillingMethod billingMethod, DataTable dataTable) {
        List<FixedRateRuleRequest> payloads = new ArrayList<>();
        switch (billingMethod) {
            case FIXED:
                LocalDate now = LocalDate.now();
                LocalDate firstDay = now.withDayOfMonth(1);
                LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00.000'Z'");
                String startDate = firstDay.format(formatter);
                String endDate = lastDay.format(formatter);
                for (Map<String, String> row : dataTable.asMaps()) {
                    FixedRateRuleRequest request = FixedRateRuleRequest.builder()
                            .product(emptyIfNull("EMS"))
                            .type(emptyIfNull("CLIENT"))
                            .rateType(emptyIfNull("MONTHLY"))
                            .chargeMethod("PRO_RATE")
                            .clientName(emptyIfNull(row.get("client")))
                            .brokerName(emptyIfNull(row.get("broker")))
                            .startDate(emptyIfNull(startDate))
                            .endDate(emptyIfNull(endDate))
                            .value(row.get("rate") != null ? Integer.valueOf(row.get("rate")) : null)
                            .currency(emptyIfNull("USD"))
                            .atLeastOneTrade(row.get("atLeastOneTrade") != null ? Integer.valueOf(row.get("atLeastOneTrade")) : 0)
                            .build();
                    payloads.add(request);
                }
                scenarioContextWithObject.setData("REQUEST_MAPS", payloads);
                break;
            default:
                throw new IllegalArgumentException("Unknown billing method: " + billingMethod);
        }
        return payloads;
    }

    public List<VolumeRateRuleRequest> buildVolumePostRequestPayloads(BillingMethod billingMethod, DataTable dataTable) {
        List<VolumeRateRuleRequest> payloads = new ArrayList<>();
        switch (billingMethod) {
            case VOLUME:
                LocalDate now = LocalDate.now();
                LocalDate firstDay = now.withDayOfMonth(1);
                LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00.000'Z'");
                String startDate = firstDay.format(formatter);
                String endDate = lastDay.format(formatter);

                for (Map<String, String> row : dataTable.asMaps()) {
                    String client = row.get("client");
                    String broker = row.get("broker");
                    String rate = row.get("rate");
                    VolumeRateRuleRequest request = VolumeRateRuleRequest.builder()
                            .priority(emptyIfNull("100"))
                            .priorityMatch(row.get("priorityMatch") != null ?
                                    Boolean.valueOf(row.get("priorityMatch")) : false)
                            .clientName(emptyIfNull(client))
                            .account(emptyIfNull(row.get("account")))
                            .brokerName(emptyIfNull(broker))
                            .securityType(emptyIfNull(row.get("securityType")))
                            .market(emptyIfNull(row.get("market")))
                            .securityExchange(emptyIfNull(row.get("securityExchange")))
                            .orderType(emptyIfNull(row.get("orderType")))
                            .block(row.get("block") != null ? row.get("block") : "0")
                            .manual(row.get("manual") != null ? row.get("manual") : "0")
                            .tradeType(emptyIfNull(row.get("tradeType")))
                            .caspianTradeType(emptyIfNull(row.get("caspianTradeType")))
                            .tag(emptyIfNull(row.get("tag")))
                            .synth(emptyIfNull(row.get("synth")))
                            .internalAccount(emptyIfNull(row.get("internalAccount")))
                            .startDate(emptyIfNull(startDate))
                            .endDate(emptyIfNull(endDate))
                            .value(rate != null ? Integer.valueOf(rate) : 0)
                            .valueType("BPS")
                            .currency(emptyIfNull(row.get("currency")))
                            .build();
                    payloads.add(request);

                }
                scenarioContextWithObject.setData("REQUEST_MAPS", payloads);
                break;

            default:
                throw new IllegalArgumentException("Unknown billing method: " + billingMethod);
        }
        return payloads;
    }

    public VolumeRateRuleRequest buildVolumePostRequestPayloads() {
        Map<String,
                String> rateRulesData = (Map<String, String>) scenarioContextWithObject.getData("RateRulesData");
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00.000'Z'");
        String startDate = firstDay.format(formatter);
        String endDate = lastDay.format(formatter);

        String client = rateRulesData.get("TopClient");
        String broker = rateRulesData.get("TopBroker");
        String securityType = rateRulesData.get("SecurityType");
        String market = rateRulesData.get("Market");
        String currentToraRate = rateRulesData.get("CurrentToraRate");
        BigDecimal toraRate = new BigDecimal(currentToraRate);
        Integer bpsValue = toraRateToBps(toraRate);
        Integer rate = bpsValue += 100;
        Integer priority = getMaxPriorityForVolumeRateRules();
        VolumeRateRuleRequest request = VolumeRateRuleRequest.builder()
                .priority(emptyIfNull(String.valueOf(priority != null ? priority + 1 : 100)))
                .priorityMatch(rateRulesData.get("priorityMatch") != null ?
                        Boolean.valueOf(rateRulesData.get("priorityMatch")) : false)
                .clientName(emptyIfNull(client))
                .account(emptyIfNull(rateRulesData.get("account")))
                .brokerName(emptyIfNull(broker))
                .securityType(emptyIfNull(securityType))
                .market(emptyIfNull(market))
                .securityExchange(emptyIfNull(rateRulesData.get("securityExchange")))
                .orderType(emptyIfNull(rateRulesData.get("orderType")))
                .block(rateRulesData.get("block") != null ? rateRulesData.get("block") : "0")
                .manual(rateRulesData.get("manual") != null ? rateRulesData.get("manual") : "0")
                .tradeType(emptyIfNull(rateRulesData.get("tradeType")))
                .caspianTradeType(emptyIfNull(rateRulesData.get("caspianTradeType")))
                .tag(emptyIfNull(rateRulesData.get("tag")))
                .synth(emptyIfNull(rateRulesData.get("synth")))
                .internalAccount(emptyIfNull(rateRulesData.get("internalAccount")))
                .startDate(emptyIfNull(startDate))
                .endDate(emptyIfNull(endDate))
                .value(rate != null ? Integer.valueOf(rate) : 0)
                .valueType("BPS")
                .currency(emptyIfNull(rateRulesData.get("currency")))
                .build();
        scenarioContextWithObject.setData("REQUEST_MAPS", request);
        return request;

    }

    public Integer getMaxPriorityForVolumeRateRules() {
        String baseUrl = PropertiesReader.getProperty("baseUrl");
        String endpoint = "/api/rate-rules/volume/get-max-priority";
        String fullUrl = baseUrl + endpoint;

        // Prepare headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        // Send GET request
        Response response = APIUtils.getAllWithAuth(baseUrl, endpoint, headers);
        System.out.println(response.asString());

        try {
            String responseBody = response.asString().trim();
            if (!responseBody.isEmpty()) {
                return Integer.valueOf(responseBody);
            }
        } catch (Exception e) {
            logger.error("Failed to parse max priority from response", e);
        }
        return null;
    }

    public static Integer toraRateToBps(BigDecimal toraRate) {
        if (toraRate == null) return 0;
        // 1 BPS = 0.0001, so 0.01 = 100 BPS
        return toraRate.multiply(BigDecimal.valueOf(10000)).intValue();
    }

    public static BigDecimal bpsToToraRate(int bps) {
        // 100 BPS = 0.01
        return BigDecimal.valueOf(bps).divide(BigDecimal.valueOf(10000));
    }

    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }

    private void validateConnectivityRateRuleResponses(List<Object> responses) {
        List<ConnectivityRateRuleRequest> requestMaps = (List<ConnectivityRateRuleRequest>)
                scenarioContextWithObject.getData("REQUEST_MAPS");
        for (int i = 0; i < responses.size(); i++) {
            Object resp = responses.get(i);
            ConnectivityRateRuleRequest requestMap = requestMaps.get(i);

            if (resp instanceof ConnectivityRateRuleResponse) {
                ConnectivityRateRuleResponse connectivityRateRuleResponse = (ConnectivityRateRuleResponse) resp;

                // Validate request fields against response
                validateConnectivityRequestAgainstResponse(requestMap, connectivityRateRuleResponse);
            } else {
                logger.warn("Unexpected response type: " + resp.getClass().getName());
            }
        }
    }

    private void validateConnectivityRequestAgainstResponse(ConnectivityRateRuleRequest request, ConnectivityRateRuleResponse response) {
        Field[] fields = ConnectivityRateRuleRequest.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object reqValue = field.get(request);
                if (reqValue == null || (reqValue instanceof String && ((String) reqValue).isEmpty())) {
                    continue; // Skip empty or null fields
                }

                String key = field.getName();
                Object respValue = null;

                switch (key) {
                    case "brokerName":
                        respValue = response.getBrokerName();
                        break;
                    case "clientName":
                        respValue = response.getClientName();
                        break;
                    case "rateType":
                        respValue = response.getRateType();
                        break;
                    case "description":
                        respValue = response.getDescription();
                        break;
                    case "description2":
                        respValue = response.getDescription2();
                        break;
                    case "displayGroup":
                        respValue = response.getDisplayGroup();
                        break;
                    case "startDate":
                        respValue = normalizeDate(response.getStartDate());
                        reqValue = normalizeDate((String) reqValue);
                        break;
                    case "endDate":
                        respValue = normalizeDate(response.getEndDate());
                        reqValue = normalizeDate((String) reqValue);
                        break;
                    case "value":
                        // Convert request value to Integer for comparison
                        respValue = response.getValue();
                        reqValue = Integer.valueOf((String) reqValue);
                        break;
                    case "currency":
                        respValue = response.getCurrency();
                        break;
                    // Add more fields as needed
                    default:
                        break;
                }
                softAssert.assertEquals(respValue, reqValue, "Mismatch for field: " + key);
            } catch (IllegalAccessException e) {
                logger.error("Error accessing field: " + field.getName(), e);
            }
        }
    }

    public void validateTheResponse(String billingMethod) {
        List<Object> responses = (List<Object>) scenarioContextWithObject.getData(TestConstants.RESPONSE);
        switch (getBillingMethod()) {
            case VOLUME:
                validateVolumeRateRuleResponses(responses);
                break;
            case FIXED:
                validateFixedRateRuleResponses(responses);
                break;
            case DEVELOPMENT:
                validateDevelopmentRateRuleResponses(responses);
                break;
            case CONNECTIVITY:
                validateConnectivityRateRuleResponses(responses);
                break;
            case AGENCY:
                validateAgencyRateRuleResponses(responses);
                break;
            default:
                throw new IllegalArgumentException("Unsupported billing method: " + getBillingMethod());
        }

    }

    private List<Object> handleConnectivityRateRule(DataTable dataTable) throws Exception {
        logger.info("Building Connectivity Rate Rule payloads.");
        List<ConnectivityRateRuleRequest> payloads = buildPostRequestPayloadsForConnectivity(BillingMethod.CONNECTIVITY, dataTable);

        // Initialize headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        List<Object> responses = new ArrayList<>();

        for (int i = 0; i < payloads.size(); i++) {
            ConnectivityRateRuleRequest payload = payloads.get(i);
            String jsonPayload = objectMapper.writeValueAsString(payload);

            Response response = APIUtils.postWithAuth(PropertiesReader.getProperty("baseUrl"),
                    "api/rate-rules/connectivity/create", jsonPayload, headers);
            ConnectivityRateRuleResponse responsePojo = objectMapper.readValue(response.asString(), ConnectivityRateRuleResponse.class);
            responses.add(responsePojo);
            logger.info("POST request sent. Response status: {}", response.getStatusCode());
        }
        scenarioContextWithObject.setData("REQUEST_MAPS", payloads);
        return responses;
    }

    public List<ConnectivityRateRuleRequest> buildPostRequestPayloadsForConnectivity(BillingMethod billingMethod, DataTable dataTable) {
        List<ConnectivityRateRuleRequest> payloads = new ArrayList<>();
        switch (billingMethod) {
            case CONNECTIVITY:
                LocalDate now = LocalDate.now();
                LocalDate firstDay = now.withDayOfMonth(1);
                LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00.000'Z'");
                String startDate = firstDay.format(formatter);
                String endDate = lastDay.format(formatter);
                for (Map<String, String> row : dataTable.asMaps()) {
                    ConnectivityRateRuleRequest request = ConnectivityRateRuleRequest.builder()
                            .brokerName(emptyIfNull(row.get("broker")))
                            .clientName(emptyIfNull(row.get("client")))
                            .rateType(emptyIfNull("MONTHLY"))
                            .description(emptyIfNull("Description"))
                            .description2(emptyIfNull("Description"))
                            .displayGroup(emptyIfNull("LEADING"))
                            .startDate(startDate)
                            .endDate(endDate)
                            .value(emptyIfNull(row.get("rate")))
                            .currency(emptyIfNull("USD"))
                            .build();
                    payloads.add(request);
                }
                scenarioContextWithObject.setData("REQUEST_MAPS", payloads);
                break;
            default:
                throw new IllegalArgumentException("Unknown billing method: " + billingMethod);
        }
        return payloads;
    }

    private void validateVolumeRateRuleResponses(List<Object> responses) {
        List<VolumeRateRuleRequest> requestMaps = (List<VolumeRateRuleRequest>)
                scenarioContextWithObject.getData("REQUEST_MAPS");
        for (int i = 0; i < responses.size(); i++) {
            Object resp = responses.get(i);
            VolumeRateRuleRequest requestMap = requestMaps.get(i);

            if (resp instanceof RateRuleResponse) {
                RateRuleResponse rateRuleResponse = (RateRuleResponse) resp;

                // Validate request fields against response
                validateVolumeRequestAgainstResponse(requestMap, rateRuleResponse);
            } else {
                logger.warn("Unexpected response type: " + resp.getClass().getName());
            }
        }
    }


    private void validateVolumeRequestAgainstResponse(VolumeRateRuleRequest request, RateRuleResponse response) {
        Field[] fields = VolumeRateRuleRequest.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object reqValue = field.get(request);
                if (reqValue == null || (reqValue instanceof String && ((String) reqValue).isEmpty())) {
                    continue; // Skip empty or null fields
                }

                String key = field.getName();
                Object respValue = null;
                switch (key) {
                    case "priority":
                        respValue = String.valueOf(response.getPriority());
                        break;
                    case "priorityMatch":
                        respValue = response.getPriorityMatch();
                        break;
                    case "client":
                        respValue = response.getClient() != null ? response.getClient().getName() : response.getClientName();
                        break;
                    case "broker":
                        respValue = response.getBroker() != null ? response.getBroker().getName() : response.getBrokerName();
                        break;
                    case "clientName":
                        respValue = response.getClientName();
                        break;
                    case "brokerName":
                        respValue = response.getBrokerName();
                        break;
                    case "block":
                        respValue = String.valueOf(response.getBlock());
                        break;
                    case "manual":
                        respValue = String.valueOf(response.getManual());
                        break;
                    case "startDate":
                        respValue = normalizeDate(response.getStartDate());
                        reqValue = normalizeDate((String) reqValue);
                        break;
                    case "endDate":
                        respValue = normalizeDate(response.getEndDate());
                        reqValue = normalizeDate((String) reqValue);
                        break;
                    case "value":
                        respValue = response.getValue();
                        break;
                    case "valueType":
                        respValue = response.getValueType();
                        break;
                    case "currency":
                        respValue = response.getCurrency();
                        break;
                    // Add more fields as needed
                    default:
                        // If the response POJO has a matching getter, you can use reflection here too
                        break;
                }
                softAssert.assertEquals(respValue, reqValue, "Mismatch for field: " + key);
            } catch (IllegalAccessException e) {
                logger.error("Error accessing field: " + field.getName(), e);
            }
        }
    }

    private void validateFixedRateRuleResponses(List<Object> responses) {
        List<FixedRateRuleRequest> requestMaps = (List<FixedRateRuleRequest>)
                scenarioContextWithObject.getData("REQUEST_MAPS");
        for (int i = 0; i < responses.size(); i++) {
            Object resp = responses.get(i);
            FixedRateRuleRequest requestMap = requestMaps.get(i);

            if (resp instanceof FixedRateRuleResponse) {
                FixedRateRuleResponse fixedRateRuleResponse = (FixedRateRuleResponse) resp;

                // Validate request fields against response
                validateFixedRequestAgainstResponse(requestMap, fixedRateRuleResponse);
            } else {
                logger.warn("Unexpected response type: " + resp.getClass().getName());
            }
        }
    }

    private void validateFixedRequestAgainstResponse(FixedRateRuleRequest request, FixedRateRuleResponse response) {
        Field[] fields = FixedRateRuleRequest.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object reqValue = field.get(request);
                if (reqValue == null || (reqValue instanceof String && ((String) reqValue).isEmpty())) {
                    continue; // Skip empty or null fields
                }

                String key = field.getName();
                Object respValue = null;

                switch (key) {
                    case "product":
                        respValue = response.getProduct();
                        break;
                    case "type":
                        respValue = response.getType();
                        break;
                    case "rateType":
                        respValue = response.getRateType();
                        break;
                    case "chargeMethod":
                        respValue = response.getChargeMethod();
                        break;
                    case "clientName":
                        respValue = response.getClientName();
                        break;
                    case "brokerName":
                        respValue = response.getBrokerName();
                        break;
                    case "startDate":
                        respValue = normalizeDate(response.getStartDate());
                        reqValue = normalizeDate((String) reqValue);
                        break;
                    case "endDate":
                        respValue = normalizeDate(response.getEndDate());
                        reqValue = normalizeDate((String) reqValue);
                        break;
                    case "value":
                        respValue = response.getValue();
                        break;
                    case "currency":
                        respValue = response.getCurrency();
                        break;
                    case "atLeastOneTrade":
                        respValue = response.getAtLeastOneTrade();
                        break;
                    // Add more fields as needed
                    default:
                        // Optionally handle other fields
                        break;
                }
                softAssert.assertEquals(respValue, reqValue, "Mismatch for field: " + key);
            } catch (IllegalAccessException e) {
                logger.error("Error accessing field: " + field.getName(), e);
            }
        }
    }


    private String normalizeDate(String date) {
        if (date == null) return "";
        return date.replace("+00:00", "Z");
    }

    public void getTheBillingMethodAndId(DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);
        scenarioContextWithObject.setData("billingMethod", data.get("billingMethod"));
    }

    public void sendDeleteRequest() {
        List<Object> responses = (List<Object>) scenarioContextWithObject.getData(TestConstants.RESPONSE);
        String billingMethod = scenarioContextWithObject.getData("billingMethod").toString().toLowerCase();

        Object responseObj = responses.get(0);
        Integer id = null;

        switch (billingMethod) {
            case "agency":
                id = ((AgencyRateRuleResponse) responseObj).getId();
                break;
            case "connectivity":
                id = ((ConnectivityRateRuleResponse) responseObj).getId();
                break;
            case "development":
                id = ((DevelopmentRateRuleResponse) responseObj).getId();
                break;
            case "fixedrate":
                id = ((FixedRateRuleResponse) responseObj).getId();
                break;
            case "volume":
                id = ((RateRuleResponse) responseObj).getId();
                break;
            // Add more cases as needed
            default:
                throw new IllegalArgumentException("Unknown billing method: " + billingMethod);
        }

        scenarioContextWithObject.setData("RaterulesID", id);

        // Build endpoint dynamically
        if (billingMethod.equalsIgnoreCase("development")) {
            billingMethod = "consulting";
        }
        String endpoint = String.format("api/rate-rules/%s/%s", billingMethod, id);

        // Prepare headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));
        // Send the DELETE request
        Response response = APIUtils.
                deleteWithAuth(PropertiesReader.getProperty("baseUrl"),
                        endpoint, headers);
        scenarioContextWithObject.setData("DELETE_RESPONSE", response);
        logger.info("DELETE request sent to endpoint: {}. Response status: {}",
                endpoint, response.getStatusCode()
        );
    }

    public void validateTheDeleteResponseStatus(int expectedStatus) {
        Response response = (Response) scenarioContextWithObject.getData("DELETE_RESPONSE");
        softAssert.assertEquals(response.getStatusCode(), expectedStatus, "Unexpected status code for DELETE");
    }

    public void validateTheGetResponseStatus(int expectedStatus) {
        Response response = (Response) scenarioContextWithObject.getData("GET_RESPONSE");
        softAssert.assertEquals(response.getStatusCode(), expectedStatus, "Unexpected status code for GET");
    }

    public void sendGetRequest() {
        String billingMethod = scenarioContextWithObject.getData("billingMethod").toString().toLowerCase();

        // Build endpoint dynamically
        String endpoint = String.format("api/rate-rules/%s", billingMethod);
        String baseUrl = PropertiesReader.getProperty("baseUrl");
        String fullUrl = baseUrl + endpoint;
        String body = "{}";

// Log the full URL
        logger.info("Hitting endpoint: {}", fullUrl);

        // Prepare headers
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        // Send GET request
        Response response = APIUtils.postWithAuth(PropertiesReader.getProperty("baseUrl"),
                endpoint, body, headers);
        System.out.println(response.asString());
        scenarioContextWithObject.setData("Response", response);
    }
}
