package org.billing.api.payloads.request.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VolumeRateRuleRequest {
    private String priority;
    private Boolean priorityMatch;
    private String clientName;
    private String account;
    private String brokerName;
    private String securityType;
    private String market;
    private String securityExchange;
    private String orderType;
    private String block;
    private String manual;
    private String tradeType;
    private String caspianTradeType;
    private String tag;
    private String synth;
    private String internalAccount;
    private String startDate;      // ISO date string, e.g. "2025-11-01T00:00:00.000Z"
    private String endDate;        // ISO date string, e.g. "2025-11-14T00:00:00.000Z"
    private Integer value;
    private String valueType;
    private String currency;
}

