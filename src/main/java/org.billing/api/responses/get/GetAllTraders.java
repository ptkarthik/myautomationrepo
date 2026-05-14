    package org.billing.api.responses.get;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data                      // Generates getters, setters, `toString`, `equals`, and `hashCode`
    @NoArgsConstructor         // Generates a no-argument constructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)// Generates a constructor with all fields
    public class GetAllTraders {
        private String id;            // Represents the unique trader ID
        private String name;

        public boolean getIsToraTrader() {
            return isToraTrader;
        }

        public void setIsToraTrader(boolean toraTrader) {
            isToraTrader = toraTrader;
        }

        @JsonProperty("isToraTrader")// Represents the trader's name
        private boolean isToraTrader; // Indicates if the trader is a ToraTrader (true/false)
    }
