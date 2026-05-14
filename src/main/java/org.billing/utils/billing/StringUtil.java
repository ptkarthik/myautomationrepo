package org.billing.utils.billing;

/**
 * Utility class that provides several string validation methods.
 *
 * @author rares.oltean
 * @author dragos.sas
 */
public class StringUtil {


    /**
     * The default e-mail address separator
     */
    private static final String DEFAULT_EMAIL_ADDRESS_SEPARATOR = ",";
    /**
     * Regular expression for e-mail address validation
     */
    private static final String EMAIL_ADDRESS_VALIDATION_REGEX = "^[A-Za-z0-9.%_+\\-']+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,30}$";
    /**
     * Format expression for generating LABEL (VALUE) string values
     */
    private static final String LABEL_VALUE_FORMAT = "%s (%s)";
    /**
     * Pattern for matching LABEL (VALUE) string values and extracting the label and value from it
     */
    private static final String LABEL_VALUE_PATTERN = "\\s*([^\\s\\(]*)\\s*\\((.*)\\)";

    /**
     * Checks whether the provided data parameter is a non-null, non empty string, valid e-mail address
     *
     * @param data The data to be checked
     * @return <code>true</code> if the provided data is a non-null, non empty string, valid e-mail address,
     * <code>false</code> otherwise.
     */
    public static boolean isEmailAddress(String data) {
        if (isNullOrEmpty(data)) {
            return false;
        }
        return data.trim().matches(EMAIL_ADDRESS_VALIDATION_REGEX);
    }

    /**
     * Checks whether the provided data parameter is a non-null, non empty string, valid comma separated e-mail address
     * list.
     *
     * @param data The data to be checked
     * @return <code>true</code> if the provided data is a non-null, non empty string, comma separated e-mail address
     * list,
     * <code>false</code> otherwise.
     */
    public static boolean isEmailAddressList(String data) {
        return isEmailAddressList(data, DEFAULT_EMAIL_ADDRESS_SEPARATOR);
    }

    /**
     * Checks whether the provided data parameter is a valid e-mail address list.
     *
     * @param data      The data to be checked
     * @param separator The e-mail address separator for the e-mail address list
     * @return <code>true</code> if the provided data is a non-null, non empty string, e-mail address list,
     * <code>false</code> otherwise.
     */
    public static boolean isEmailAddressList(String data, String separator) {
        for (String token : data.trim().split(separator)) {
            if (!token.trim().matches(EMAIL_ADDRESS_VALIDATION_REGEX)) {
                return false;
            }
        }
        return true;
    }
    public static boolean isNullOrEmpty(String data) {
        return (data == null || data.trim().isEmpty());
    }


}
