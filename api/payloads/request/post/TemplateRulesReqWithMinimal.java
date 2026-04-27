package org.billing.api.payloads.request.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-arguments constructor
@AllArgsConstructor // Generates an all-arguments constructor
@Builder // Provides a builder pattern for object creation
public class TemplateRulesReqWithMinimal {
    private int priority;
    private String id;
    private String billingMethod;
    private String brokerName;
    private String clientName;
    private String templateName;
}
