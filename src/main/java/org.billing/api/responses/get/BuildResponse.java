package org.billing.api.responses.get;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildResponse {
    private String month;
    private Integer ordersCount;
    private Integer fixedRatesCount;
    private Integer connectivityRatesCount;
    private Integer agencyRatesCount;
    private Integer developmentRatesCount;
    private String buildState;
    private String statusInfo;
    private String currentRebuildAction;
}