package org.billing.services.apiservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.TestConstants;
import org.billing.api.payloads.request.post.CapRuleReq;
import org.billing.api.responses.get.GetAllTraders;
import org.billing.api.responses.post.BrokerResponse;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.services.apiservices.pojo.ClientResponse;
import org.billing.services.ui.BrokerPageServices;
import org.billing.services.ui.TraderServices;
import org.billing.utils.EntityUtils;
import org.billing.utils.apiutilities.APIUtils;
import org.billing.utils.propconfig.PropertiesReader;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CapRulesService {

    private static final Logger logger = LogManager.getLogger(CapRulesService.class);
    private Response response;
    private SoftAssert softAssert;
    private ScenarioContextWithObject scenarioContextWithObject;
    private int actualPriority;

    public CapRulesService(ScenarioContextWithObject scenarioContextWithObject) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        softAssert = SoftAssertContainer.getInstance();
    }


    public void validateCapRulesDetails() {
        try {
            response = (Response) scenarioContextWithObject.getData("Response");
            CapRuleReq capRuleReq = response.as(CapRuleReq.class);
            logger.info("Cap Rules Details validation started......");
            logger.info("Response Status Code: {}", response.getStatusCode());
            scenarioContextWithObject.setData("id", capRuleReq.getId());

            // Validate the response body
            softAssert.assertEquals(response.getStatusCode(), 200, "Response status code is not 200");
            softAssert.assertEquals(capRuleReq.getCapType(), scenarioContextWithObject.getData("capType").toString(), "Cap Type does not match");
            softAssert.assertEquals(capRuleReq.getClientName(), scenarioContextWithObject.getData("clientName").toString(), "Client Name does not match");
            softAssert.assertEquals(capRuleReq.getBrokerName(), scenarioContextWithObject.getData("brokerName").toString(), "Broker Name does not match");
            softAssert.assertEquals(capRuleReq.getTraderName(), scenarioContextWithObject.getData("traderName").toString(), "Trader Name does not match");
            softAssert.assertEquals(capRuleReq.getSecurityType(), scenarioContextWithObject.getData("securityType").toString(), "Security Type does not match");
            softAssert.assertEquals(capRuleReq.isExcludeCountry(), Boolean.parseBoolean(scenarioContextWithObject.getData("excludeCountry").toString()), "Exclude Country does not match");
            softAssert.assertEquals(capRuleReq.getCountriesList(), scenarioContextWithObject.getData("countriesList"), "Countries List does not match");
            softAssert.assertEquals(capRuleReq.getOrderType(), scenarioContextWithObject.getData("orderType").toString(), "Order Type does not match");
            softAssert.assertEquals(capRuleReq.getSide(), scenarioContextWithObject.getData("side").toString(), "Side does not match");
            softAssert.assertEquals(capRuleReq.getInstrument(), scenarioContextWithObject.getData("instrument").toString(), "Instrument does not match");
            logger.info("Cap Rules Details validation completed successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while validating Cap Rules details.", e);
            throw e;
        }
    }

    public void createCapRule(Map<String, String> capRulesMap, String bearerToken, String apiEndPoint) throws Exception {
        try {
            logger.info("Creating cap-rule using the POST API endpoint.");
            List<Header> headers = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.add(new Header("Authorization", "Bearer " + bearerToken));

            // Fetch and validate for broker, trader, client
            List<ClientResponse> allClients = ClientServices.fetchAllClients();
            List<BrokerResponse> allBrokers = BrokerPageServices.fetchAllBrokers();
            List<GetAllTraders> allTraders = TraderServices.fetchAllTraders();

            String clientNameToUse = EntityUtils.getValidOrRandomValue(
                    capRulesMap.get("clientName"),
                    allClients.stream().map(ClientResponse::getName).collect(Collectors.toList())
            );
            String brokerNameToUse = EntityUtils.getValidOrRandomValue(
                    capRulesMap.get("brokerName"),
                    allBrokers.stream().map(BrokerResponse::getName).collect(Collectors.toList())
            );
            String traderNameToUse = EntityUtils.getValidOrRandomValue(
                    capRulesMap.get("traderName"),
                    allTraders.stream().map(GetAllTraders::getName).collect(Collectors.toList())
            );

            // Set all datatable values in scenarioContextWithObject
            for (Map.Entry<String, String> entry : capRulesMap.entrySet()) {
                String key = entry.getKey();
                // For these three, set the actual value used (not the datatable value)
                if ("clientName".equals(key)) {
                    scenarioContextWithObject.setData("clientName", clientNameToUse);
                } else if ("brokerName".equals(key)) {
                    scenarioContextWithObject.setData("brokerName", brokerNameToUse);
                } else if ("traderName".equals(key)) {
                    scenarioContextWithObject.setData("traderName", traderNameToUse);
                } else if ("countriesList".equals(key)) {
                    // Parse and store the countries list as an object
                    scenarioContextWithObject.setData("countriesList", objectMapper.readValue(entry.getValue(), List.class));
                } else {
                    scenarioContextWithObject.setData(key, entry.getValue());
                }
            }

            // Build CapRuleReq using validated names for broker, trader, client
            CapRuleReq capRuleReq = CapRuleReq.builder()
                    .priority(Integer.parseInt(capRulesMap.get("priority")))
                    .capType(capRulesMap.get("capType"))
                    .clientName(clientNameToUse)
                    .brokerName(brokerNameToUse)
                    .traderName(traderNameToUse)
                    .securityType(capRulesMap.get("securityType"))
                    .excludeCountry(Boolean.parseBoolean(capRulesMap.get("excludeCountry")))
                    .countriesList(objectMapper.readValue(capRulesMap.get("countriesList"), List.class))
                    .orderType(capRulesMap.get("orderType"))
                    .side(capRulesMap.get("side"))
                    .instrument(capRulesMap.get("instrument"))
                    .startDate(capRulesMap.get("startDate"))
                    .endDate(capRulesMap.get("endDate"))
                    .value(Double.parseDouble(capRulesMap.get("value")))
                    .currency(capRulesMap.get("currency"))
                    .build();

            Response response = APIUtils.postWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    apiEndPoint,
                    capRuleReq,
                    headers
            );
            scenarioContextWithObject.setData("Response", response);
            System.out.println(response.asString());
        } catch (Exception e) {
            logger.error("Error occurred while creating cap rule.", e);
            throw e;
        }
    }

    public void updatePriorityOfCapRule(String apiEndPoint, String bearerToken) {
        try {
            response = (Response) scenarioContextWithObject.getData("Response");
            List<CapRuleReq> capRuleReq = response.jsonPath().getList("", CapRuleReq.class);
            actualPriority = capRuleReq.get(0).getPriority();
            String id = scenarioContextWithObject.getData("id").toString();
            logger.info("Updating priority of Cap Rule with ID: {}", id);
            Map<String, String> pathData = new HashMap<>();
            pathData.put("id", id);
            APIUtils.putWithAuthAndPath(
                    PropertiesReader.getProperty("baseUrl"),
                    pathData,
                    apiEndPoint,
                    List.of(new Header("Authorization", "Bearer " + bearerToken))
            );
        } catch (Exception e) {
            logger.error("Error occurred while updating priority of Cap Rule.", e);
            throw e;
        }
    }


    public void validatePriorityOfCapRule(String priorityType) {
        try {
            if (priorityType.equalsIgnoreCase("increased")) {
                actualPriority--;
            } else if (priorityType.equalsIgnoreCase("decreased")) {
                actualPriority++;
            }
            response = (Response) scenarioContextWithObject.getData("Response");
            List<CapRuleReq> capRuleReq = response.jsonPath().getList("", CapRuleReq.class);
            logger.info("Validating priority of Cap Rule.");
            softAssert.assertEquals(capRuleReq.get(0).getPriority(), actualPriority, "Priority does not match");
            logger.info("Priority validation completed successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while validating priority of Cap Rule.", e);
            throw e;
        }
    }

    public void fetchCapRule(String endPoint, String bearerToken) throws JsonProcessingException {
        String id = scenarioContextWithObject.getData("id").toString();
        logger.info("Fetch the Cap Rule with ID: {}", id);
        ObjectMapper objMapper = new ObjectMapper();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", id);
        String body = objMapper.writeValueAsString(requestBody);
        response = APIUtils.postWithAuth(
                PropertiesReader.getProperty("baseUrl"),
                endPoint,
                body,
                List.of(new Header("Authorization", "Bearer " + bearerToken))
        );
        scenarioContextWithObject.setData("Response", response);

    }

    public void updateCapRuleClientName(String endPoint, String updatedClientName, String bearerToken) throws JsonProcessingException {
        try {
            response = (Response) scenarioContextWithObject.getData("Response");
            CapRuleReq capRuleReq = response.as(CapRuleReq.class);
            capRuleReq.setClientName(updatedClientName);
            scenarioContextWithObject.setData("clientName", updatedClientName);
            logger.info("Updating client name of Cap Rule to: {}", updatedClientName);
            ObjectMapper objectMapper = new ObjectMapper();
            String capRuleReqJson = objectMapper.writeValueAsString(capRuleReq);
            response = APIUtils.putWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    endPoint,
                    capRuleReqJson,
                    List.of(new Header("Authorization", "Bearer " + bearerToken))
            );
            scenarioContextWithObject.setData("Response", response);
        } catch (Exception e) {
            logger.error("Error occurred while updating client name of Cap Rule.", e);
            throw e;
        }
    }

    public void uploadCapRuleFile(String endPoint, String pathParamName, String pathParamValue, String filePath, String bearerToken) {
        // Initialize headers
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", "Bearer " + bearerToken));
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put(pathParamName, pathParamValue);
        // Log the headers list
        logger.info("Headers: {}", headers);

        // Path to the CSV file to be uploaded
        File file = new File(filePath);
        response = APIUtils.uploadFileWithPathParam(PropertiesReader.getProperty("baseUrl"),
                endPoint,
                pathParams, "file", file,
                headers);
        System.out.println(response);
        scenarioContextWithObject.setData(TestConstants.UPLOAD_RESPONSE, response);

    }
}
