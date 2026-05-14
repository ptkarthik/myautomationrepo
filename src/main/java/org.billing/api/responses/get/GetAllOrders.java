package org.billing.api.responses.get;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllOrders {
    private String id;
    private String buildMonth;
    private String orderDate;
    private String symbol;
    private String securityType;
    private String market;
    private String securityExchange;
    private String countryCode;
    private String currency;
    private String brokerName;
    private String realBroker;
    private String client;
    private String realClient;
    private String instrument;
    private String condition;
    private String workType;
    private String orderType;
    private String account;
    private Integer block;
    private Integer manual;
    private Integer volume;
    private BigDecimal valueRaw;
    private BigDecimal value;
    private BigDecimal averagePrice;
    private BigDecimal toraCommission;
    private BigDecimal currencyRate;
    private BigDecimal currencyRateJPYtoUSD;
    private BigDecimal toraCommissionUSD;
    private BigDecimal uncappedToraCommissionUSD;
    private BigDecimal jpyValue;
    private BigDecimal usdValue;
    private BigDecimal toraRate;
    private String toraRateType;
    private String capRuleApplied;
    private Integer quantity;
    private BigDecimal price;
    private String note;
    private String internalNote;
    private String tif;
    private String group;
    private String tradeType;
    private String orderTag;
    private String tag;
    private String tradeBook;
    private String internalAccount;
    private String externalId;
    private String synthType;
    private String brokerDest;
    private String side;
    private String originator;
    private String sender;
    private String tenor;
    private String brokerInvoiceTemplate;
    private String clientInvoiceTemplate;
    private String clientName;
    private String sourceEnvironment;
    private String caspianTradeType;
    private String inputUser;
    private String transactionType;
    private BigDecimal cumulativeSumForCaspian;
}