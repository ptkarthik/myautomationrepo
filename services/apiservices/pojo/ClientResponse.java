package org.billing.services.apiservices.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NonNull
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private String name;
    private boolean active;
    private String traderColumn;
    private List<String> uncommissionedVolumeTypes;
    private String billingMethodsString;
    private boolean caspianClient;
    private String uncommissionedVolumeTypesAsString;
}


