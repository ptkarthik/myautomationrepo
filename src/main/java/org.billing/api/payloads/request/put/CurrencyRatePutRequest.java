package org.billing.api.payloads.request.put;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRatePutRequest {
    private Long id;
    private String monthYear;
    private String code;
    private Double rate;
}

