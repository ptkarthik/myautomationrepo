package org.billing.utils;

import java.util.List;
import java.util.Random;

public class EntityUtils {
    // For Broker
        public static String getValidBrokerName(String requestedName, List<String> allBrokerNames) {
            if (requestedName != null && allBrokerNames.contains(requestedName)) {
                return requestedName;
            }
            // Pick a random broker if requestedName is not present
            if (!allBrokerNames.isEmpty()) {
                return allBrokerNames.get(new Random().nextInt(allBrokerNames.size()));
            }
            throw new RuntimeException("No brokers available in the system!");
        }


// For Client
public static String getValidClientName(String requestedName, List<String> allClientNames) {
    if (requestedName != null && allClientNames.contains(requestedName)) {
        return requestedName;
    }
    // Pick a random client if requestedName is not present
    if (!allClientNames.isEmpty()) {
        return allClientNames.get(new Random().nextInt(allClientNames.size()));
    }
    throw new RuntimeException("No clients available in the system!");
}

    // For Trader
    public static String getValidTraderName(String requestedName, List<String> allTraderNames) {
        if (requestedName != null && allTraderNames.contains(requestedName)) {
            return requestedName;
        }
        // Pick a random trader if requestedName is not present
        if (!allTraderNames.isEmpty()) {
            return allTraderNames.get(new Random().nextInt(allTraderNames.size()));
        }
        throw new RuntimeException("No traders available in the system!");
    }

    /**
     * Returns the requested value if it exists in the list, otherwise returns a random value from the list.
     * Throws an exception if the list is empty.
     *
     * @param requestedValue The value from the datatable (may be null)
     * @param allValues      The list of all valid values fetched from the API
     * @return A valid value to use (either the requested one or a random one)
     */
    public static String getValidOrRandomValue(String requestedValue, List<String> allValues) {
        if (requestedValue != null && allValues.contains(requestedValue)) {
            return requestedValue;
        }
        if (!allValues.isEmpty()) {
            return allValues.get(new Random().nextInt(allValues.size()));
        }
        throw new RuntimeException("No valid values available in the system!");
    }
}



