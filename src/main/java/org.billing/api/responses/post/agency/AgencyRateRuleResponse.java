package org.billing.api.responses.post.agency;

import lombok.Data;

@Data
public class AgencyRateRuleResponse {
    private Integer id;
    private String clientName;
    private String brokerName;
    private String description;
    private String region;
    private String rateType;
    private String startDate;   // e.g. "2025-11-16T00:00:00.000+00:00"
    private String endDate;     // e.g. "2025-11-16T00:00:00.000+00:00"
    private Integer chargeDay;
    private Integer value;
    private String currency;
    private Boolean active;
}