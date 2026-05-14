package org.billing.api.responses.post;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates a no-args constructor
@AllArgsConstructor // Generates an all-args constructor
public class FxSwapRatePostResponse {
    private int id;
    private int lowerBoundInDays;
    private int upperBoundInDays;
    private String tenor;
    private double revenueShare;
    private boolean maxUpperBound;
    private String formattedRevenueShare;
    private String dayCount;
}