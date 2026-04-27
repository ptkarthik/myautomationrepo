package org.billing.api.responses;

import lombok.Data;

@Data
public class InvoiceReportFile {
    private int invoiceReportFileId;
    private String reportType;
    private String reportFormat;
    private String fileName;
    private boolean hasDateInFileName;
    private String invoiceTemplateName;
    private String fileSize;
}
