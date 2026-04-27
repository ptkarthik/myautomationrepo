package org.billing.api.responses.post;

import lombok.Data;

@Data
public class TemplateRuleVolumeResponse {
    private Long id;
    private Integer priority;
    private String templateName;
    private String brokerName;
    private String clientName;
    private String billingEntity;
    private String billingMethod;
    private String market;
    private String securityType;
    private String orderType;
    private String region;
    private String synthType;
    private String tradeType;
    private String tag;
    private String internalAccount;
    private String account;
    private String sourceEnvironment;
    private Boolean includeSpecialLLCClients;
    private String traderId;
    private String excludedAssetClassesString;
}