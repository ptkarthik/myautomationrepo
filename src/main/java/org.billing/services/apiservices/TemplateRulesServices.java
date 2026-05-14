package org.billing.services.apiservices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.api.APIUtils.APIAuthentication;
import org.billing.api.payloads.request.post.TemplateRulesReq;
import org.billing.api.payloads.request.post.TemplateRulesReqWithMinimal;
import org.billing.api.responses.get.GetAllTraders;
import org.billing.api.responses.post.BrokerResponse;
import org.billing.api.responses.post.InvoiceTemplate;
import org.billing.api.responses.post.TemplateRuleResponse;
import org.billing.api.responses.post.TemplateRuleVolumeResponse;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.services.apiservices.pojo.ClientResponse;
import org.billing.services.ui.BrokerPageServices;
import org.billing.services.ui.TraderServices;
import org.billing.utils.apiutilities.APIUtils;
import org.billing.utils.propconfig.PropertiesReader;
import org.testng.asserts.SoftAssert;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemplateRulesServices {

    private static final Logger logger = LogManager.getLogger(CapRulesService.class);
    private Response response;
    private SoftAssert softAssert;
    private ScenarioContextWithObject scenarioContextWithObject;
    private int actualPriority;
    List<BrokerResponse> broker=BrokerPageServices.fetchAllBrokers();
    List<ClientResponse> clientDetails = ClientServices.fetchAllClients();
    List<GetAllTraders> traderDetails = TraderServices.fetchAllTraders();
    SecureRandom random = new SecureRandom();

    public TemplateRulesServices(ScenarioContextWithObject scenarioContextWithObject) throws Exception {
        this.scenarioContextWithObject = scenarioContextWithObject;
        softAssert = SoftAssertContainer.getInstance();
    }


    public void getMaxPriority(String bearerToken, String apiEndPoint) {
        try {
            logger.info("Creating template-rules using the POST API endpoint.");
            List<Header> headers = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.add(new Header("Authorization", "Bearer " + bearerToken));

            Response res = APIUtils.getAllWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    apiEndPoint,
                    headers
            );
            scenarioContextWithObject.setData("maxPriority", res.getBody().asString());
        } catch (Exception e) {
            logger.error("Error occurred while fetching client details.", e);
            throw e;
        }
    }

    public void createTemplateRule(Map<String, String> templateRulesMap, String bearerToken, String apiEndPoint) throws JsonProcessingException {
        try {
            logger.info("Creating template-rules using the POST API endpoint.");
            List<Header> headers = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            headers.add(new Header("Authorization", "Bearer " + bearerToken));
            String[] directKeys = new String[0];
            TemplateRulesReq tempRuleReq = null;
            scenarioContextWithObject.setData("traderId", traderDetails.get(random.nextInt(traderDetails.size())).getId());
            scenarioContextWithObject.setData("brokerName",broker.get(random.nextInt(broker.size())).getName());
            scenarioContextWithObject.setData("clientName",clientDetails.get(random.nextInt(clientDetails.size())).getName());


            if (templateRulesMap.get("billingMethod").equalsIgnoreCase("VOLUME")) {
                directKeys = new String[]{
                        "billingMethod", "brokerName", "clientName", "excludedAssetClassesString", "market",
                        "orderType", "region", "sourceEnvironment", "synthType",
                        "templateName", "tradeType", "traderId"
                };
                tempRuleReq = TemplateRulesReq.builder()
                        .priority(Integer.parseInt(scenarioContextWithObject.getData("maxPriority").toString()) + 1)
                        .billingMethod(templateRulesMap.get("billingMethod"))
                        .brokerName((String)scenarioContextWithObject.getData("brokerName"))
                        .clientName((String)scenarioContextWithObject.getData("clientName"))
                        .traderId((String)scenarioContextWithObject.getData("traderId"))
                        .excludedAssetClassesString(templateRulesMap.get("excludedAssetClassesString"))
                        .market(templateRulesMap.get("market"))
                        .orderType(templateRulesMap.get("orderType"))
                        .region(templateRulesMap.get("region"))
                        .sourceEnvironment(templateRulesMap.get("sourceEnvironment"))
                        .synthType(templateRulesMap.get("synthType"))
                        .templateName(templateRulesMap.get("templateName"))
                        .tradeType(templateRulesMap.get("traderType"))
                        .build();
                scenarioContextWithObject.setData("templateRulesRequest", tempRuleReq);

            } else if (templateRulesMap.get("billingMethod").equalsIgnoreCase("FIXED")) {
                directKeys = new String[]{
                        "billingMethod", "brokerName", "clientName", "product",
                        "region", "sourceEnvironment", "synthType",
                        "templateName", "traderId"
                };
                tempRuleReq = TemplateRulesReq.builder()
                        .priority(Integer.parseInt(scenarioContextWithObject.getData("maxPriority").toString()) + 1)
                        .billingMethod(templateRulesMap.get("billingMethod"))
                        .brokerName((String)scenarioContextWithObject.getData("brokerName"))
                        .clientName((String)scenarioContextWithObject.getData("clientName"))
                        .traderId((String)scenarioContextWithObject.getData("traderId"))
                        .product(templateRulesMap.get("product"))
                        .region(templateRulesMap.get("region"))
                        .sourceEnvironment(templateRulesMap.get("sourceEnvironment"))
                        .synthType(templateRulesMap.get("synthType"))
                        .templateName(templateRulesMap.get("templateName"))
                        .build();
                scenarioContextWithObject.setData("templateRulesRequest", tempRuleReq);
            } else if (templateRulesMap.get("billingMethod").equalsIgnoreCase("AGENCY")) {
                directKeys = new String[]{
                        "billingMethod", "brokerName", "clientName",
                        "templateName", "region", "sourceEnvironment"
                };
                tempRuleReq = TemplateRulesReq.builder()
                        .priority(Integer.parseInt(scenarioContextWithObject.getData("maxPriority").toString()) + 1)
                        .billingMethod(templateRulesMap.get("billingMethod"))
                        .brokerName((String)scenarioContextWithObject.getData("brokerName"))
                        .clientName((String)scenarioContextWithObject.getData("clientName"))
                        .templateName(templateRulesMap.get("templateName"))
                        .region(templateRulesMap.get("region"))
                        .sourceEnvironment(templateRulesMap.get("sourceEnvironment"))
                        .build();
                scenarioContextWithObject.setData("templateRulesRequest", tempRuleReq);
            } else if (templateRulesMap.get("billingMethod").equalsIgnoreCase("CONNECTIVITY") || templateRulesMap.get("billingMethod").equalsIgnoreCase("CONSULTING")) {
                directKeys = new String[]{
                        "billingMethod", "brokerName", "clientName",
                        "templateName"
                };
                tempRuleReq = TemplateRulesReq.builder()
                        .priority(Integer.parseInt(scenarioContextWithObject.getData("maxPriority").toString()) + 1)
                        .billingMethod(templateRulesMap.get("billingMethod"))
                        .brokerName((String)scenarioContextWithObject.getData("brokerName"))
                        .clientName((String)scenarioContextWithObject.getData("clientName"))
                        .templateName(templateRulesMap.get("templateName"))
                        .build();
                scenarioContextWithObject.setData("templateRulesRequest", tempRuleReq);
            }


            for (String key : directKeys) {
                scenarioContextWithObject.setData(key, templateRulesMap.getOrDefault(key, ""));
            }

            scenarioContextWithObject.setData("Response", APIUtils.postWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    apiEndPoint,
                    tempRuleReq,
                    headers
            ));
        } catch (Exception e) {
            logger.error("Error occurred while fetching client details.", e);
            throw e;
        }
    }

    public void createTemplateRuleWithScenarioContextData() throws JsonProcessingException {
        try {
            String brokerName = null;
            String clientName = null;
            String baseUrl = PropertiesReader.getProperty("baseUrl");
            List<Header> headers = new ArrayList<>();
            String authToken = APIAuthentication.postAuthorizationCode();
            headers.add(new Header("Content-Type", "application/json"));
            headers.add(new Header("Authorization", "Bearer " + authToken));
            // Get all required fields from scenarioContextWithObject
            String type = (String) scenarioContextWithObject.getData("TemplateType");
            String billingMethod = (String) scenarioContextWithObject.getData("BillingMethod");
            if (type.equalsIgnoreCase("BROKER")) {
                brokerName = (String) scenarioContextWithObject.getData("RandomBrokerName");
            } else {
                clientName = (String) scenarioContextWithObject.getData("RandomClientName");

            }
            String templateName = (String) scenarioContextWithObject.getData("InvoiceTemplateName");
            String priorityStr = (String) scenarioContextWithObject.getData("maxPriority");
            int priority = priorityStr != null ? Integer.parseInt(priorityStr) + 1 : 1;
            // Build the endpoint dynamically
            String apiEndPoint =
                    String.format(baseUrl + "api/template-rules/%s/%s/create", type, billingMethod);

            // Build the request object using only scenarioContextWithObject data
            TemplateRulesReqWithMinimal tempRuleReq = TemplateRulesReqWithMinimal.builder()
                    .priority(priority)
                    .billingMethod(billingMethod)
                    .brokerName(brokerName)
                    .clientName(clientName)
                    .templateName(templateName)
                    .build();

            scenarioContextWithObject.setData("Response", APIUtils.postWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    apiEndPoint,
                    tempRuleReq,
                    headers
            ));
            ObjectMapper mapper = new ObjectMapper();
            TemplateRuleResponse templateRuleResponse = mapper.
                    readValue(((Response) scenarioContextWithObject.getData("Response")).asString(), TemplateRuleResponse.class);
            scenarioContextWithObject.setData("TemplateRuleResponse", templateRuleResponse);
            String templateRuleId= String.valueOf(templateRuleResponse.getId());
            scenarioContextWithObject.setData("TemplateRuleId", templateRuleId);
            logger.info("Template rule created successfully via API." +
                    ((Response) scenarioContextWithObject.getData("Response")).asString());

        } catch (Exception e) {
            logger.error("Error occurred while creating template rule.", e);
            throw e;
        }
    }


    public void validateTemplateRulesDetails() {
        TemplateRuleVolumeResponse templateVolumeRuleRes=null;
        TemplateRuleResponse templateRuleRes=null;
        try {
            response = (Response) scenarioContextWithObject.getData("Response");
            if(((TemplateRulesReq)(scenarioContextWithObject
                    .getData("templateRulesRequest"))).getBillingMethod().equalsIgnoreCase("VOLUME")){
                 templateVolumeRuleRes = response.as(TemplateRuleVolumeResponse.class);
            } else {
                templateRuleRes = response.as(TemplateRuleResponse.class);
            }
            logger.info("Template Rules Details validation started......");
            logger.info("Response Status Code: {}", response.getStatusCode());
            if(((TemplateRulesReq)(scenarioContextWithObject
                    .getData("templateRulesRequest"))).getBillingMethod().equalsIgnoreCase("VOLUME")) {
                scenarioContextWithObject.setData("id", templateVolumeRuleRes.getId());
            } else {
            scenarioContextWithObject.setData("id", templateRuleRes.getId()); }
            if (scenarioContextWithObject.getData("billingMethod").toString().equalsIgnoreCase("VOLUME")) {
                // Validate the response body
                softAssert.assertEquals(response.getStatusCode(), 200, "Response status code is not 200");
                softAssert.assertEquals(templateVolumeRuleRes.getTemplateName(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getTemplateName(), "Template Name does not match");
                softAssert.assertEquals(templateVolumeRuleRes.getClientName(),((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getClientName()
                        ,"Client Name does not match");
                softAssert.assertEquals(templateVolumeRuleRes.getBrokerName(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getBrokerName(),
                        "Broker Name does not match");
                softAssert.assertEquals(templateVolumeRuleRes.getTraderId(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getTraderId(), "Trader Name does not match");
                softAssert.assertEquals(templateVolumeRuleRes.getRegion(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getRegion(), "Security Type does not match");
                softAssert.assertEquals(templateVolumeRuleRes.getBillingMethod(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getBillingMethod(), "Exclude Country does not match");

            } else if (scenarioContextWithObject.getData("billingMethod").toString().equalsIgnoreCase("FIXED")) {
                // Validate the response body
                softAssert.assertEquals(response.getStatusCode(), 200, "Response status code is not 200");
                // Validate the response body
                softAssert.assertEquals(response.getStatusCode(), 200, "Response status code is not 200");
                softAssert.assertEquals(templateRuleRes.getTemplateName(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getTemplateName(), "Template Name does not match");
                softAssert.assertEquals(templateRuleRes.getClientName(),((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getClientName()
                        ,"Client Name does not match");
                softAssert.assertEquals(templateRuleRes.getBrokerName(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getBrokerName(),
                        "Broker Name does not match");
                softAssert.assertEquals(templateRuleRes.getTraderId(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getTraderId(), "Trader Name does not match");
                softAssert.assertEquals(templateRuleRes.getRegion(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getRegion(), "Security Type does not match");
                softAssert.assertEquals(templateRuleRes.getBillingMethod(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getBillingMethod(), "Exclude Country does not match");

            } else if (scenarioContextWithObject.getData("billingMethod").toString().equalsIgnoreCase("CONNECTIVITY") || scenarioContextWithObject.getData("billingMethod").toString().equalsIgnoreCase("CONSULTING")) {
                // Validate the response body
                // Validate the response body
                softAssert.assertEquals(response.getStatusCode(), 200, "Response status code is not 200");
                softAssert.assertEquals(templateRuleRes.getTemplateName(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getTemplateName(), "Template Name does not match");
                softAssert.assertEquals(templateRuleRes.getClientName(),((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getClientName()
                        ,"Client Name does not match");
                softAssert.assertEquals(templateRuleRes.getBrokerName(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getBrokerName(),
                        "Broker Name does not match");
                softAssert.assertEquals(templateRuleRes.getTraderId(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getTraderId(), "Trader Name does not match");
                softAssert.assertEquals(templateRuleRes.getRegion(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getRegion(), "Security Type does not match");
                softAssert.assertEquals(templateRuleRes.getBillingMethod(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getBillingMethod(), "Exclude Country does not match");

            } else if (scenarioContextWithObject.getData("billingMethod").toString().equalsIgnoreCase("AGENCY")) {
                // Validate the response body
                softAssert.assertEquals(response.getStatusCode(), 200, "Response status code is not 200");
                softAssert.assertEquals(templateRuleRes.getTemplateName(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getTemplateName(), "Template Name does not match");
                softAssert.assertEquals(templateRuleRes.getClientName(),((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getClientName()
                        ,"Client Name does not match");
                softAssert.assertEquals(templateRuleRes.getBrokerName(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getBrokerName(),
                        "Broker Name does not match");
                softAssert.assertEquals(templateRuleRes.getTraderId(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getTraderId(), "Trader Name does not match");
                softAssert.assertEquals(templateRuleRes.getRegion(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getRegion(), "Security Type does not match");
                softAssert.assertEquals(templateRuleRes.getBillingMethod(), ((TemplateRulesReq)scenarioContextWithObject.getData("templateRulesRequest")).getBillingMethod(), "Exclude Country does not match");

            }
            logger.info("Template Rules Details validation completed successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while validating Template Rules details.", e);
            throw e;
        }
    }

}
