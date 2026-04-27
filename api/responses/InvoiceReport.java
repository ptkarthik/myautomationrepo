package org.billing.api.responses;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceReport {
    private long reportId;
    private int version;
    private String generateDate;
    private String buildMonth;
    private String user;
    private String reportState;
    private String billingMethod;
    private List<InvoiceReportFile> files;
}
