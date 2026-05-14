package org.billing.services.apiservices;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Header;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.billing.Context.ScenarioContextWithObject;
import org.billing.api.APIUtils.APIAuthentication;
import org.billing.api.endpoints.ClientEndPoints;
import org.billing.api.payloads.request.post.ClientReq;
import org.billing.services.apiservices.pojo.ClientResponse;
import org.billing.utils.apiutilities.APIUtils;
import org.billing.utils.propconfig.PropertiesReader;
import org.testng.asserts.SoftAssert;

import java.util.*;
import java.util.stream.Stream;

public class ClientServices {
    private static final Logger logger = LogManager.getLogger(ClientServices.class);
    private Response response;
    private SoftAssert softAssert;
    private ScenarioContextWithObject scenarioContextWithObject;

    public ClientServices(ScenarioContextWithObject scenarioContextWithObject) {
        this.scenarioContextWithObject = scenarioContextWithObject;
    }

    public void validateClientDetails() {
        try {
            response = (Response) scenarioContextWithObject.getData("Response");
            ClientResponse responseClientName = null;
            try {
                List<ClientResponse> clientResponses = response.jsonPath().getList("", ClientResponse.class);
                responseClientName = clientResponses.get(0);
            } catch (ClassCastException e) {
                responseClientName = response.as(ClientResponse.class);
            }
            logger.info("Client Details validation started......");
            softAssert = new SoftAssert();
            softAssert.assertEquals(responseClientName.getName(),
                    scenarioContextWithObject.getData("name"), "Client name does not match.");
            softAssert.assertEquals(responseClientName.isCaspianClient(),
                    Boolean.parseBoolean(String.valueOf(scenarioContextWithObject.getData("caspianClient"))), "Caspian Client does not match.");
            softAssert.assertEquals(responseClientName.getTraderColumn(),
                    scenarioContextWithObject.getData("traderColumn"), "Trader Column does not match.");
            softAssert.assertEquals(responseClientName.getBillingMethodsString(),
                    scenarioContextWithObject.getData("billingMethodsString"), "Billing Methods does not match.");
//            softAssert.assertEquals(responseClientName.getUncommissionedVolumeTypesAsString(),
//                    scenarioContextWithObject.getData("uncommissionedVolumeTypes"), "UnCommissioned Volume Types does not match.");
            softAssert.assertEquals(responseClientName.isActive(),
                    Boolean.parseBoolean(String.valueOf(scenarioContextWithObject.getData("active"))), "Active flag does not match.");

            logger.info("Client Details validation completed successfully.");
        } catch (Exception e) {
            logger.error("Error occurred while validating client details.", e);
            throw e;
        } finally {
            softAssert.assertAll();
        }
    }

    public static List<ClientResponse> fetchAllClients() throws Exception {
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Authorization", "Bearer " + APIAuthentication.postAuthorizationCode()));
        Response response = APIUtils.getAllWithAuth(
                PropertiesReader.getProperty("baseUrl"),
                ClientEndPoints.GET_ALL_CLIENTS_NEW,
                headers
        );
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.asString(),
                new TypeReference<List<ClientResponse>>() {});
    }

    public static ClientResponse getValidClient(String requestedName, List<ClientResponse> allClients) {
        if (requestedName != null) {
            for (ClientResponse client : allClients) {
                if (requestedName.equalsIgnoreCase(client.getName())) {
                    return client;
                }
            }
        }
        // Pick a random client if requestedName is not present
        if (!allClients.isEmpty()) {
            return allClients.get(new Random().nextInt(allClients.size()));
        }
        throw new RuntimeException("No clients available in the system!");
    }

    public void fetch_ClientDetails(Map<String, String> clientDetailsMap, String bearerToken) {
        try {
            logger.info("Fetching Client details using the GET API endpoint.");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", "Bearer " + bearerToken));
            scenarioContextWithObject.setData("name", clientDetailsMap.get("name"));
            scenarioContextWithObject.setData("traderColumn", clientDetailsMap.get("traderColumn"));//
             scenarioContextWithObject.setData("uncommissionedVolumeTypes", clientDetailsMap.get("uncommissionedVolumeTypes"));
            scenarioContextWithObject.setData("billingMethodsString", clientDetailsMap.get("billingMethodsString"));
            scenarioContextWithObject.setData("active", clientDetailsMap.get("active"));
            scenarioContextWithObject.setData("caspianClient", clientDetailsMap.get("caspianClient"));
            scenarioContextWithObject.setData("Response", APIUtils.postWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    ClientEndPoints.GET_CLIENT,
                    clientDetailsMap,
                    headers
            ));
        } catch (Exception e) {
            logger.error("Error occurred while fetching client details.", e);
            throw e;
        }
    }

    public void createClient(Map<String, String> clientDetailsMap, String bearerToken) {
        try {
            logger.info("Fetching Client details using the GET API endpoint.");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", "Bearer " + bearerToken));
            scenarioContextWithObject.setData("name", clientDetailsMap.get("name"));
            scenarioContextWithObject.setData("id", clientDetailsMap.get("name"));
            scenarioContextWithObject.setData("traderColumn", clientDetailsMap.get("traderColumn"));//
            scenarioContextWithObject.setData("uncommissionedVolumeTypes", Arrays.asList(clientDetailsMap.get("uncommissionedVolumeTypes").split(",")));
            scenarioContextWithObject.setData("billingMethodsString", clientDetailsMap.get("billingMethodsString"));
            scenarioContextWithObject.setData("active", clientDetailsMap.get("active"));
            scenarioContextWithObject.setData("caspianClient", clientDetailsMap.get("caspianClient"));
            ClientReq req = ClientReq.builder().name(clientDetailsMap.get("name"))
                            .active( Boolean.parseBoolean(clientDetailsMap.get("active")))
                                    .caspianClient(Boolean.parseBoolean(clientDetailsMap.get("caspianClient")))
                                            .traderColumn(clientDetailsMap.get("traderColumn"))
                                                    .billingMethodsString(clientDetailsMap.get("billingMethodsString"))
                                                            .uncommissionedVolumeTypes(Collections.singletonList(clientDetailsMap.get("uncommissionedVolumeTypes"))).build();

            scenarioContextWithObject.setData("Response", APIUtils.postWithAuth(
                    PropertiesReader.getProperty("baseUrl"),
                    ClientEndPoints.POST_CLIENT,
                    req,
                    headers
            ));
        } catch (Exception e) {
            logger.error("Error occurred while fetching client details.", e);
            throw e;
        }
    }
}
