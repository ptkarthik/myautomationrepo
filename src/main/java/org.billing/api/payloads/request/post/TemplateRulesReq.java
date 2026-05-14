package org.billing.api.payloads.request.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-arguments constructor
@AllArgsConstructor // Generates an all-arguments constructor
@Builder // Provides a builder pattern for object creation
public class TemplateRulesReq {
    private int id;
    private int priority;
    private String billingMethod;
    private String brokerName;
    private String clientName;
    private String market;
    private String region;
    private String sourceEnvironment;
    private String synthType;
    private String templateName;
    private String tradeType;
    private String traderId;
    private String excludedAssetClassesString;
    private String orderType;
    private String product;

}
