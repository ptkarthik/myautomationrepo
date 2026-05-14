package org.billing.services.apiservices;

import java.security.SecureRandom;

public class BaseApiServices {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // Allowed characters
    private static final int STRING_LENGTH = 5; // Length of the random string

    public static String generateRandomString() {
        SecureRandom secureRandom = new SecureRandom();

        StringBuilder randomString = new StringBuilder(STRING_LENGTH);
        for (int i = 0; i < STRING_LENGTH; i++) {
            // Randomly select a character from the CHARACTERS string
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            randomString.append(CHARACTERS.charAt(randomIndex));
        }

        return randomString.toString();
    }


}
