package org.billing.api.payloads.request.post;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientReq {

    private boolean active;
    private List<String> uncommissionedVolumeTypes;
    private String billingMethodsString;
    private boolean caspianClient;
    private String name;
    private String traderColumn;

}
