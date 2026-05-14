package org.billing.assertcontainer;

import org.testng.asserts.SoftAssert;

public class SoftAssertContainer {
    private static final ThreadLocal<SoftAssert> softAssertInstance = new ThreadLocal<>();

    // Initialize SoftAssert (call this at the beginning of the test)
    public static void initialize() {
        softAssertInstance.set(new SoftAssert());
    }

    // Get the current instance
    public static SoftAssert getInstance() {
        SoftAssert softAssert = softAssertInstance.get();
        if (softAssert == null) {
            throw new IllegalStateException("SoftAssert has not been initialized. Call initialize() first.");
        }
        return softAssert;
    }

    // Cleanup after test execution (optional)
    public static void cleanup() {
        softAssertInstance.remove();
    }
}
