package org.billing.api.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Invoices {
    private String name;
    private List<InvoiceReport> invoiceReports;
}
