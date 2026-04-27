package org.billing.api.payloads.request.post;

import lombok.Data;

@Data
public class RegenerateFixedRateRequest  {
    private boolean overrideInvoiceDate;
    private boolean overridePaymentDueDate;
    private boolean uponReceipt;
    private boolean fastMonth;
}