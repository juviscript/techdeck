package dev.juviscript.techdeck.util;

/**
 * Utility class for string normalization and formatting
 */
public class StringUtils {

    private StringUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Trim whitespace from a string
     */
    public static String trim(String value) {
        return value != null ? value.trim() : null;
    }

    /**
     * Capitalize the first letter, lowercase the rest
     * "jOHN" → "John"
     */
    public static String capitalizeFirst(String value) {
        if (value == null || value.isEmpty()) return value;
        value = value.trim();
        if (value.length() == 1) return value.toUpperCase();
        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }

    /**
     * Capitalize the first letter of each word
     * "jOHN dOE" → "John Doe"
     */
    public static String capitalizeWords(String value) {
        if (value == null || value.isEmpty()) return value;
        String[] words = value.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!result.isEmpty()) result.append(" ");
            if (word.length() == 1) {
                result.append(word.toUpperCase());
            } else {
                result.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * Normalize email to lowercase and trim
     * "  JOHN@EMAIL.COM  " → "john@email.com"
     */
    public static String normalizeEmail(String email) {
        return email != null ? email.toLowerCase().trim() : null;
    }

    /**
     * Normalize phone number - remove all non-digits
     * "(555) 123-4567" → "5551234567"
     */
    public static String normalizePhone(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? null : digits;
    }

    /**
     * Normalize address line - capitalize words, trim
     * "  123 main STREET  " → "123 Main Street"
     */
    public static String normalizeAddress(String address) {
        if (address == null || address.isEmpty()) return address;
        return capitalizeWords(address);
    }

    /**
     * Normalize city - capitalize words, trim
     * "new york" → "New York"
     */
    public static String normalizeCity(String city) {
        return capitalizeWords(city);
    }

    /**
     * Normalize state - uppercase, trim
     * "fl" → "FL"
     */
    public static String normalizeState(String state) {
        return state != null ? state.trim().toUpperCase() : null;
    }

    /**
     * Normalize zip code - trim, keep only digits and hyphen
     * " 32063-1234 " → "32063-1234"
     */
    public static String normalizeZipCode(String zipCode) {
        if (zipCode == null) return null;
        return zipCode.trim().replaceAll("[^0-9-]", "");
    }
}
