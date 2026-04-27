package org.billing.api.responses.put;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TraderPutErrorResponse {
    @JsonProperty("message") // Links JSON "message" to this field
    private String message;

    @JsonProperty("field") // Links JSON "field" to this field
    private String field;

    @JsonProperty("objectName") // Links JSON "objectName" to this field
    private String objectName;
}
