package org.billing.api.payloads.request.post;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates a no-args constructor
@AllArgsConstructor // Generates an all-args constructor
public class FxSwapRatePostRequest {
    private String lowerBoundInDays;
    private String upperBoundInDays;
    private String tenor;
    private String revenueShare;
    private String maxUpperBound;
}