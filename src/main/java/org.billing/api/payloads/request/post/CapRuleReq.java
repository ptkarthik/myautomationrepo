package org.billing.api.payloads.request.post;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CapRuleReq {

    private long id;
    private int priority;
    private String capType;
    private String clientName;
    private String brokerName;
    private String traderName;
    private String securityType;
    private boolean excludeCountry;
    private List<String> countriesList;
    private String orderType;
    private String side;
    private String instrument;
    private String startDate;
    private String endDate;
    private double value;
    private String currency;

}
