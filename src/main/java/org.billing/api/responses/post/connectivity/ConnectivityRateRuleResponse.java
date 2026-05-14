package org.billing.api.responses.post.connectivity;

import lombok.Data;

@Data
public class ConnectivityRateRuleResponse {
    private Integer id;
    private String clientName;
    private String brokerName;
    private String description;
    private String description2;
    private String rateType;
    private String displayGroup;
    private String startDate;   // e.g. "2025-11-16T00:00:00.000+00:00"
    private String endDate;     // e.g. "2025-11-16T00:00:00.000+00:00"
    private Integer value;      // Note: in response it's an Integer
    private String currency;
    private Boolean active;
}
