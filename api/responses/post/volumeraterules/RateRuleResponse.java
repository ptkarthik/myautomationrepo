package org.billing.api.responses.post.volumeraterules;

import lombok.Data;
import java.util.List;

@Data
public class RateRuleResponse {
    private Integer id;
    private Integer priority;
    private Boolean priorityMatch;
    private String account;
    private String internalAccount;
    private String brokerName;
    private String clientName;
    private String traderId;
    private String securityType;
    private String market;
    private String securityExchange;
    private String orderType;
    private Integer block;
    private Integer manual;
    private String tradeType;
    private String caspianTradeType;
    private String tag;
    private String synth;
    private String currency;
    private String endDate;
    private String startDate;
    private Integer startVolume;
    private Integer endVolume;
    private Integer value;
    private String valueType;
    private Integer jpyValue;
    private Broker broker;
    private Client client;
    private String trader;

    @Data
    public static class Broker {
        private String name;
    }

    @Data
    public static class Client {
        private String name;
        private String billingMethodsString;
        private String traderColumn;
        private Boolean active;
        private Boolean caspianClient;
        private List<String> billingMethods;
        private String uncommissionedVolumeTypes;
        private String uncommissionedVolumeTypesAsString;
        private String traderColumnString;
        private String activeClient;
        private String caspianClientFormatted;
    }
}