package org.billing.api.payloads.request.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRatePostRequest {
    private String monthYear;
    private String code;
    private Double rate;
}

