package org.billing.api.responses.put;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class FieldErrorResponse {
    @JsonProperty("fieldErrors") // Links JSON "fieldErrors" to this field
    private List<TraderPutErrorResponse> fieldErrors;
}
