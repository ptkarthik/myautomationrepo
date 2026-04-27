    package org.billing.api.responses.get;

    import com.fasterxml.jackson.annotation.JsonProperty;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data                      // Generates getters, setters, `toString`, `equals`, and `hashCode`
    @NoArgsConstructor         // Generates a no-argument constructor
    @AllArgsConstructor// Generates a constructor with all fields
    public class GetAllCurrencyRate {
        private String id;            // Represents the unique trader ID
        private String monthYear;
        private String code;
        private String rate;
    }
