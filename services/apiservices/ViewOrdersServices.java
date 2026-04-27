package org.billing.services.apiservices;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.billing.api.responses.get.GetAllOrders;
import org.billing.assertcontainer.SoftAssertContainer;
import org.billing.dbconfig.TraderDBConfig;
import org.billing.services.ui.BaseServices;
import org.billing.utils.apiutilities.APIUtils;
import org.billing.utils.propconfig.PropertiesReader;
import org.testng.asserts.SoftAssert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ViewOrdersServices extends BaseServices {
    private static final Logger logger = LogManager.getLogger(ViewOrdersServices.class);
    private final ScenarioContextWithObject scenarioContextWithObject;
    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SoftAssert softAssert;
    private TraderDBConfig traderDBConfig = new TraderDBConfig();
    public Response response;
    private JsonPath jsonPath;
    private BillingMethod billingMethod;
    List<GetAllOrders> allOrders;
    Map<String, List<GetAllOrders>> groupedOrdersByBrokerList = new HashMap<>();
    Map<String, List<GetAllOrders>> groupedOrdersByClientList = new HashMap<>();
    Map<String, List<GetAllOrders>> groupedBySecurityType = new HashMap<>();
    Map<String, List<GetAllOrders>> groupedBySomeOtherField = new HashMap<>();
    List<GetAllOrders> ordersForTopClient;
    List<GetAllOrders> ordersForTopBroker;
    List<GetAllOrders> ordersForTopSecurityType;
    List<GetAllOrders> ordersForTopMarket;
    public Map<String,String> dataOfRaterules=new HashMap<>();


    public ViewOrdersServices(ScenarioContextWithObject scenarioContextWithObject, ScenarioContext scenarioContext) {
        this.scenarioContextWithObject = scenarioContextWithObject;
        this.scenarioContext = scenarioContext;
        softAssert = SoftAssertContainer.getInstance();
    }

    public List<GetAllOrders> getAllPagedOrdersFromAPI() {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.withDayOfMonth(1);
        LocalDate lastDay = now.withDayOfMonth(now.lengthOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = firstDay.format(formatter);
        String endDate = lastDay.format(formatter);

        String baseUrl = PropertiesReader.getProperty("baseUrl");
        String endpointTemplate = "api/orders?page=%d&size=20&startDate=%s&endDate=%s";
        List<Header> headers = new ArrayList<>();
        String authToken = APIAuthentication.postAuthorizationCode();
        headers.add(new Header("Content-Type", "application/json"));
        headers.add(new Header("Authorization", "Bearer " + authToken));

        List<GetAllOrders> allOrders = new ArrayList<>();
        int page = 0; // Start with zero
        boolean hasMore = true;

        while (hasMore) {
            String endpoint = String.format(endpointTemplate, page, startDate, endDate);
            Response response = APIUtils.getAllWithAuth(baseUrl, endpoint, headers);
            List<GetAllOrders> ordersList;
            try {
                ordersList = objectMapper.readValue(response.asString(), new TypeReference<List<GetAllOrders>>() {
                });
            } catch (Exception e) {
                logger.error("Failed to parse orders JSON on page " + page, e);
                throw new RuntimeException("Failed to parse orders JSON", e);
            }
            allOrders.addAll(ordersList);
            if (ordersList.size() < 20) {
                hasMore = false; // Last page reached
            } else {
                page++;
            }
        }
        scenarioContextWithObject.setData("OrdersList", allOrders);
        System.out.println("Total orders fetched: " + allOrders.size());
        return allOrders;
    }

    public static Map<String, List<GetAllOrders>> groupOrdersByBroker(List<GetAllOrders> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(order -> safe(order.getRealBroker())));
    }

    public static Map<String, List<GetAllOrders>> groupOrdersByClient(List<GetAllOrders> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(order -> safe(order.getClient())));
    }



    /**
     * Groups orders by a specific broker and then by a specific client under that broker.
     * Returns the list of orders for the given broker and client.
     *
     * @param orders The list of all orders.
     * @return List of orders for the specified broker and client, or empty list if not found.
     */
    public List<GetAllOrders> getOrdersByBrokerAndClient(List<GetAllOrders> orders) {
        String brokerName = dataOfRaterules.get("TopBroker");
        String clientName = dataOfRaterules.get("TopClient");
        if (orders == null || brokerName == null || clientName == null) {
            scenarioContextWithObject.setData("FinalOrders", Collections.emptyList());
            return Collections.emptyList();
        }
        Map<String, List<GetAllOrders>> byBroker = orders.stream()
                .collect(Collectors.groupingBy(order -> safe(order.getRealBroker())));
        List<GetAllOrders> brokerOrders = byBroker.getOrDefault(brokerName, Collections.emptyList());

        Map<String, List<GetAllOrders>> byClient = brokerOrders.stream()
                .collect(Collectors.groupingBy(order -> safe(order.getClient())));
        List<GetAllOrders> finalOrders = byClient.getOrDefault(clientName, Collections.emptyList());

        scenarioContextWithObject.setData("FinalOrders", finalOrders);
        return finalOrders;
    }

    private static String safe(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public void groupTheOrderByBrokerAndClient() {
        groupedOrdersByBrokerList = groupOrdersByBroker((List<GetAllOrders>) scenarioContextWithObject.
                getData("OrdersList"));
        String topBroker = getBrokerWithMostOrders(groupedOrdersByBrokerList);
        dataOfRaterules.put("TopBroker",topBroker);
        if (topBroker != null) {
            ordersForTopBroker = groupedOrdersByBrokerList.get(topBroker);
            scenarioContextWithObject.setData("OrdersForTopBroker", ordersForTopBroker);
            setGroupedByBroker(true);
            System.out.println("Top broker: " + topBroker + " with " + ordersForTopBroker.size() + " orders.");
        } else {
            System.out.println("No brokers found.");
        }
        groupedOrdersByClientList = groupOrdersByClient((List<GetAllOrders>) scenarioContextWithObject.
                getData("OrdersForTopBroker"));
        String topClient = getClientWithMostOrders(groupedOrdersByClientList);
        dataOfRaterules.put("TopClient",topClient);
        if (topClient != null) {
            ordersForTopClient = groupedOrdersByClientList.get(topClient);
            scenarioContextWithObject.setData("OrdersForTopClient", ordersForTopClient);
            setGroupedByClient(true);
            System.out.println("Top client: " + topClient + " with " + ordersForTopClient.size() + " orders.");
        } else {
            System.out.println("No clients found.");
        }
        scenarioContextWithObject.setData("RateRulesData", dataOfRaterules);
    }

    public static String getBrokerWithMostOrders(Map<String, List<GetAllOrders>> groupedOrdersByBroker) {
        return groupedOrdersByBroker.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().size()))
                .map(Map.Entry::getKey)
                .orElse(null); // returns null if map is empty
    }

    public static String getClientWithMostOrders(Map<String, List<GetAllOrders>> groupedOrdersByClient) {
        return groupedOrdersByClient.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().size()))
                .map(Map.Entry::getKey)
                .orElse(null); // returns null if map is empty
    }

    public void GroupingBySecurityType() {
        groupedBySecurityType = groupOrdersBySecurityType(ordersForTopClient);
        String topSecurityType = getSecurityTypeWithMostOrders(groupedBySecurityType);
        dataOfRaterules.put("TopSecurityType",topSecurityType);
        if (topSecurityType != null) {
            ordersForTopSecurityType = groupedBySecurityType.get(topSecurityType);
            scenarioContextWithObject.setData("OrdersForTopSecurityType", ordersForTopSecurityType);
            setGroupedBySecurityCode(true);
            System.out.println("Top security type: " + topSecurityType + " with " + ordersForTopSecurityType.size() + " orders.");
        } else {
            System.out.println("No security types found.");
        }
        scenarioContextWithObject.setData("RateRulesData", dataOfRaterules);
    }


    public static Map<String, List<GetAllOrders>> groupOrdersBySecurityType(List<GetAllOrders> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(order -> safe(order.getSecurityType())));
    }

    public static String getSecurityTypeWithMostOrders(Map<String, List<GetAllOrders>> groupedBySecurityType) {
        return groupedBySecurityType.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().size()))
                .map(Map.Entry::getKey)
                .orElse(null); // returns null if map is empty
    }


    public boolean checkIfTheToraRateIsAllSame(List<GetAllOrders> orders) {
        if (orders == null || orders.isEmpty()) {
            scenarioContextWithObject.setData("ToraRateAllSame", null);
            scenarioContextWithObject.setData("currentToraRate", null);
            return false;
        }

        // Get the first non-null toraRate as reference
        BigDecimal referenceToraRate = orders.stream()
                .map(GetAllOrders::getToraRate)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (referenceToraRate == null) {
            scenarioContextWithObject.setData("ToraRateAllSame", null);
            scenarioContextWithObject.setData("currentToraRate", null);
            return false;
        }

        boolean allSame = orders.stream()
                .map(GetAllOrders::getToraRate)
                .allMatch(rate -> Objects.equals(rate, referenceToraRate));

        if (allSame) {
            scenarioContextWithObject.setData("ToraRateAllSame", referenceToraRate);
            scenarioContextWithObject.setData("currentToraRate", referenceToraRate);
        } else {
            scenarioContextWithObject.setData("ToraRateAllSame", null);
            scenarioContextWithObject.setData("currentToraRate", null);
        }
        dataOfRaterules.put("CurrentToraRate", String.valueOf(referenceToraRate));
        return allSame;
    }

    public Boolean checkIftheToraRateIsAllSame() {
        List<GetAllOrders> ordersToCheckOnToraRate = (List<GetAllOrders>)
                scenarioContextWithObject.getData("OrdersForTopSecurityType");
        Boolean check = checkIfTheToraRateIsAllSame(ordersToCheckOnToraRate);
        System.out.println("Checked Tora Rate consistency." + check);
        return check;
    }

    public void furtherGroupingWithOtherField() {
        GroupingByMarket();
        scenarioContextWithObject.setData("RateRulesData", dataOfRaterules);
    }

    public void GroupingByMarket() {
        // Get the list of orders for the top security type
        List<GetAllOrders> ordersForTopSecurityType = (List<GetAllOrders>) scenarioContextWithObject.getData("OrdersForTopSecurityType");

        // Group by Market
        Map<String, List<GetAllOrders>> groupedByMarket = groupOrdersByMarket(ordersForTopSecurityType);

        // Find the market with the most orders
        String topMarket = getMarketWithMostOrders(groupedByMarket);
        dataOfRaterules.put("TopMarket",topMarket);

        if (topMarket != null) {
            ordersForTopMarket = groupedByMarket.get(topMarket);
            scenarioContextWithObject.setData("OrdersForTopMarket", ordersForTopMarket);
            setGroupedByMarket(true);
            System.out.println("Top market: " + topMarket + " with " + ordersForTopMarket.size() + " orders.");
        } else {
            System.out.println("No markets found.");
        }
    }

    public static Map<String, List<GetAllOrders>> groupOrdersByMarket(List<GetAllOrders> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(order -> safe(order.getMarket())));
    }

    public static String getMarketWithMostOrders(Map<String, List<GetAllOrders>> groupedByMarket) {
        return groupedByMarket.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().size()))
                .map(Map.Entry::getKey)
                .orElse(null); // returns null if map is empty
    }

    public void setGroupedByBroker(boolean value) {
        scenarioContextWithObject.setData("GroupedByBroker", value);
    }

    public void setGroupedByClient(boolean value) {
        scenarioContextWithObject.setData("GroupedByClient", value);
    }

    public void setGroupedBySecurityCode(boolean value) {
        scenarioContextWithObject.setData("GroupedBySecurityCode", value);
    }

    public boolean isGroupedByBroker() {
        Object flag = scenarioContextWithObject.getData("GroupedByBroker");
        return flag instanceof Boolean && (Boolean) flag;
    }

    public boolean isGroupedByClient() {
        Object flag = scenarioContextWithObject.getData("GroupedByClient");
        return flag instanceof Boolean && (Boolean) flag;
    }

    public boolean isGroupedBySecurityCode() {
        Object flag = scenarioContextWithObject.getData("GroupedBySecurityCode");
        return flag instanceof Boolean && (Boolean) flag;
    }
    public void setGroupedByMarket(boolean value) {
        scenarioContextWithObject.setData("GroupedByMarket", value);
    }

    public boolean isGroupedByMarket() {
        Object flag = scenarioContextWithObject.getData("GroupedByMarket");
        return flag instanceof Boolean && (Boolean) flag;
    }

    /**
     * Returns the list of orders for the specified security type from the given orders.
     *
     * @param orders The list of orders (typically already filtered by broker and client).
     * @return List of orders for the specified security type, or empty list if not found.
     */
    public List<GetAllOrders> getOrdersBySecurityType(List<GetAllOrders> orders) {
        String securityType = dataOfRaterules.get("TopSecurityType");
        if (orders == null || securityType == null) {
            scenarioContextWithObject.setData("FinalOrders", Collections.emptyList());
            return Collections.emptyList();
        }
        Map<String, List<GetAllOrders>> bySecurityType = orders.stream()
                .collect(Collectors.groupingBy(order -> safe(order.getSecurityType())));
        List<GetAllOrders> finalOrders = bySecurityType.getOrDefault(securityType, Collections.emptyList());

        scenarioContextWithObject.setData("FinalOrders", finalOrders);
        return finalOrders;
    }

    /**
     * Returns the list of orders for the specified market from the given orders.
     *
     * @param orders The list of orders (typically already filtered by broker, client, and security type).
     * @return List of orders for the specified market, or empty list if not found.
     */
    public List<GetAllOrders> getOrdersByMarket(List<GetAllOrders> orders) {
        String market = dataOfRaterules.get("TopMarket");
        if (orders == null || market == null) {
            scenarioContextWithObject.setData("FinalOrders", Collections.emptyList());
            return Collections.emptyList();
        }
        Map<String, List<GetAllOrders>> byMarket = orders.stream()
                .collect(Collectors.groupingBy(order -> safe(order.getMarket())));
        List<GetAllOrders> finalOrders = byMarket.getOrDefault(market, Collections.emptyList());

        scenarioContextWithObject.setData("FinalOrders", finalOrders);
        return finalOrders;
    }

    public void validateTheCommissionAfterRateRuleApplication() {

    }
}

