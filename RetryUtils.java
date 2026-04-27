package org.billing;

public class RetryUtils {

    public static void executeWithRetry(Runnable action, int maxRetries, int waitTimeInMillis) {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                action.run();
                return; // Exit if no exception
            } catch (Exception e) {
                attempt++;
                if (attempt == maxRetries) {
                    throw new RuntimeException("Max retry attempts reached. Failing operation.", e);
                }
                try {
                    Thread.sleep(waitTimeInMillis);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted.", ie);
                }
            }
        }
    }
}