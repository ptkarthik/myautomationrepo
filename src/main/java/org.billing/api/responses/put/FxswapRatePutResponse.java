package org.billing.api.responses.put;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO class to represent the FxSwapRate object using Lombok.
 */
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates an all-argument constructor
public class FxswapRatePutResponse {
    private int id;                     // ID of the entity
    private int lowerBoundInDays;       // Lower bound in days
    private int upperBoundInDays;       // Upper bound in days
    private String tenor;               // Tenor information
    private double revenueShare;        // Revenue share as a double
    private boolean maxUpperBound;      // Whether the maximum upper bound was reached
    private String formattedRevenueShare; // Formatted revenue share as a string
    private String dayCount;            // Day count representation as a formatted string
}