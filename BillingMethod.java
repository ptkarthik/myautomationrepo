package org.billing;


public enum BillingMethod {
    VOLUME,
    FIXED,
    AGENCY,
    DEVELOPMENT,
    CONNECTIVITY;


    public static BillingMethod fromString(String method) {
        for (BillingMethod bm : BillingMethod.values()) {
            if (bm.name().equalsIgnoreCase(method)) {
                return bm;
            }
        }
        throw new IllegalArgumentException("Unknown billing method: " + method);
    }
}