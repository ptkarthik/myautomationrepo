package org.billing.api.responses.post;

import lombok.Data;

@Data
public class TemplateRuleResponse {
    private Long id;
    private Integer priority;
    private String templateName;
    private String brokerName;
    private String clientName;
    private String billingEntity;
    private String billingMethod;
    private String product;
    private String region;
    private String synthType;
    private String internalAccount;
    private String sourceEnvironment;
    private String traderId;
    private String traderName;
}