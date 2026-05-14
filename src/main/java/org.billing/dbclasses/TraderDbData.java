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
public class TraderDbData {
    private String id;          // Maps to the "id" column
    private String name;        // Maps to the "name" column
    private Boolean toraTrader;   // Maps to the boolean status/truth/flag column
}
