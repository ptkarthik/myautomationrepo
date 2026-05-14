package org.billing.api.responses.post.FixedRate;


import lombok.Data;

@Data
public class FixedRateRuleResponse {
    private Integer id;
    private String product;
    private String rateType;
    private String clientName;
    private String brokerName;
    private Object traderDetails; // Use appropriate type if known
    private String tradingVenueCode;
    private Integer atLeastOneTrade;
    private String mdataConnectionName;
    private String region;
    private String chargeMethod;
    private Integer chargeDay;
    private String assetClasses;
    private String type;
    private String endDate;
    private String startDate;
    private Integer initialAssetClassValue;
    private Integer additionalAssetClassValue;
    private Integer excelApiValue;
    private Integer fixGatewayValue;
    private Integer value;
    private String currency;
    private Boolean saveTrader;
    private Boolean saveClient;
    private Boolean saveBroker;
    private Boolean active;
}