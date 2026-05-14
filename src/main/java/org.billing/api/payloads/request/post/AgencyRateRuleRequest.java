package org.billing.api.payloads.request.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgencyRateRuleRequest {
    private String brokerName;
    private String clientName;
    private String rateType;
    private String description;
    private String region;
    private String startDate;   // e.g. "2025-11-16"
    private String endDate;     // e.g. "2025-11-16"
    private String value;       // String in request
    private String currency;
    private Integer chargeDay;
}