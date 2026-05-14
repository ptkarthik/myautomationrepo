package org.billing.api.payloads.request.put;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-arguments constructor
@AllArgsConstructor // Generates an all-arguments constructor
@Builder // Provides a builder pattern for object creation
public class TraderPutRequest {
    private String id;
    private String name;

    public boolean getIsToraTrader() {
        return isToraTrader;
    }

    public void setIsToraTrader(boolean toraTrader) {
        isToraTrader = toraTrader;
    }

    private boolean isToraTrader;
}