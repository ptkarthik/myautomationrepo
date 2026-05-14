package org.billing.mail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailAttachment {

    public byte[] attachmentContent;
    public String type;
    public String name;

    public MailAttachment(byte[] attachmentContent, String type, String name) {
        this.attachmentContent = attachmentContent;
        this.name = name;
        this.type = type;
    }
}

