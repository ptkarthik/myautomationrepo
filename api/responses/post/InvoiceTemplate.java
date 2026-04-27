package org.billing.api.responses.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceTemplate {
    private Long id;
    private String name;
    private String companyName;
    private String attention;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String description;
    private String currencyCode;
    private Boolean deleted;
    private PaymentInstruction paymentInstruction;
    private String paymentInstructions;
    private String email;
    private Boolean emailCompression;
    private String nationalAccount;
    private String billingMethod;
    private Boolean orderCount;
    private Boolean creditAdjustment;

    private String creditAdjustmentDescription;
    private String invoiceType;
    private String legalInstruction;
    private String bcc;
    private Boolean summaryReportNeeded;
    private Boolean clientMarketReportNeeded;
    private Boolean detailsReportNeeded;
    private String billingANumber;
    private String billingALocation;
    private String billingEntity;
    private List<TemplateRule> templateRules;
    private String templateType;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentInstruction {
        private Long id;
        private String name;
        private String currency;
        private String body;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TemplateRule {
        private Long id;
        private Integer priority;
        private String market;
        private String securityType;
        private String excludedAssetClassesString;
        private String orderType;
        private String synthType;
        private String product;
        private String tradeType;
        private String internalAccount;
        private String account;
        private String region;
        private String sourceEnvironment;
        private String billingMethod;
        private String traderId;
        private String ruleType;
        private Boolean includeSpecialLLCClients;
        private String tag;
        private Broker broker;
        private Client client;
        private Trader trader;
        private String clientName;
        private String brokerName;
        private Boolean regionNotNull;
        private List<String> excludedAssetClasses;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Broker {
            private String name;
        }


        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Client {
            private String name;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Trader {
            private String name;
        }
    }
}