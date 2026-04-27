package org.billing.dbclasses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A class representing the database table structure.
 */
@Data                   // Generates getter, setter, toString, equals, and hashCode methods
@Builder                // Enables the builder pattern for object creation
@NoArgsConstructor      // Generates a no-argument constructor
@AllArgsConstructor     // Generates an all-argument constructor
public class CurrencyRateDbData {
    private String id;          // Maps to the "id" column
    private String month;        // Maps to the "name" column
    private String code;
    private Double rate;// Maps to the boolean status/truth/flag column
}
