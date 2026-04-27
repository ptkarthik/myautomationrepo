package org.billing.api.payloads.request.put;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a data model for the provided JSON object.
 */
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates an all-argument constructor
public class FxSwapRatePutRequest {
    private Integer id;                       // Unique identifier
    private String lowerBoundInDays;         // Lower bound in days
    private String upperBoundInDays;         // Upper bound in days
    private String tenor;                 // Tenor information
    private String revenueShare;          // Revenue share value
}