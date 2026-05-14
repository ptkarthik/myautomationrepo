package org.billing.api.payloads.request.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FixedRateRuleRequest {
    private String product;
    private String type;
    private String rateType;
    private String chargeMethod;
    private String clientName;
    private String brokerName;
    private String startDate;      // ISO date string, e.g. "2025-10-31T18:30:00.000Z"
    private Integer value;
    private String currency;
    private String endDate;        // ISO date string, e.g. "2025-11-14T18:30:00.000Z"
    private Integer atLeastOneTrade;
}