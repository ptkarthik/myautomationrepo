package org.billing.api.responses.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-arguments constructor
@AllArgsConstructor // Generates an all-arguments constructor
@Builder // Provides a builder pattern for object creation
@JsonIgnoreProperties(ignoreUnknown = true)
public class TraderPostResponse {
    private String id;
    private String name;
    private boolean isToraTrader;

    public boolean getIsToraTrader() {
        return isToraTrader;
    }

    public void setIsToraTrader(boolean toraTrader) {
        isToraTrader = toraTrader;
    }

}