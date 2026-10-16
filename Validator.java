package parceldelivery.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import parceldelivery.exception.InvalidInputException;

/**
 * All input validation in one place.
 *
 * <ul>
 *   <li><b>parseXxx</b> methods turn raw console text into a typed value
 *       (rejecting text that is not the right shape).</li>
 *   <li><b>validateXxx</b> methods check a typed value against the business
 *       rules and return the cleaned value. The model classes call these in
 *       their constructors, so an invalid parcel can never be created.</li>
 * </ul>
 * Every failure throws {@link InvalidInputException} with a readable message.
 */
public final class Validator {

    public static final int MIN_PAGES = 1;
    public static final int MAX_PAGES = 100;
    public static final int MAX_NAME_LENGTH = 50;
    public static final int MAX_NOTE_LENGTH = 100;

    private static final Pattern TRACKING_NUMBER = Pattern.compile("ABC\\d{4}");
    private static final Pattern DECIMAL = Pattern.compile("\\d+(\\.\\d+)?|\\.\\d+");
    private static final Pattern WHOLE_NUMBER = Pattern.compile("\\d+");

    private Validator() {
    }

    // ------------------------------------------------------------------
    // Raw text -> typed value
    // ------------------------------------------------------------------

    /** Parses a positive-looking decimal such as "1.8" or "2". Range rules are checked by validateWeight. */
    public static BigDecimal parseWeight(String raw) throws InvalidInputException {
        String text = trimmed(raw);
        if (text.isEmpty()) {
            throw new InvalidInputException("Weight must not be empty.");
        }
        if (!DECIMAL.matcher(text).matches()) {
            throw new InvalidInputException("Weight must be a valid number, e.g. 1.5 (digits and one decimal point only).");
        }
        return new BigDecimal(text);
    }

    /** Parses a whole number made of digits only. Range rules are checked by the validateXxx methods. */
    public static int parseInteger(String raw, String fieldName) throws InvalidInputException {
        String text = trimmed(raw);
        if (text.isEmpty()) {
            throw new InvalidInputException(fieldName + " must not be empty.");
        }
        if (!WHOLE_NUMBER.matcher(text).matches()) {
            throw new InvalidInputException(fieldName + " must be a whole number (digits only).");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(fieldName + " is too large.");
        }
    }

    /** Parses a menu choice between min and max (inclusive). */
    public static int parseChoice(String raw, int min, int max) throws InvalidInputException {
        String text = trimmed(raw);
        String error = "Invalid choice! Enter a number from " + min + " to " + max + ".";
        if (!WHOLE_NUMBER.matcher(text).matches() || text.length() > 9) {
            throw new InvalidInputException(error);
        }
        int value = Integer.parseInt(text);
        if (value < min || value > max) {
            throw new InvalidInputException(error);
        }
        return value;
    }

    // ------------------------------------------------------------------
    // Business-rule validation of typed values
    // ------------------------------------------------------------------

    /** Tracking number: "ABC" followed by exactly 4 digits. Uniqueness is checked by ParcelManager. */
    public static String validateTrackingNumber(String value) throws InvalidInputException {
        String text = trimmed(value);
        if (text.isEmpty()) {
            throw new InvalidInputException("Tracking number must not be empty.");
        }
        if (!TRACKING_NUMBER.matcher(text).matches()) {
            throw new InvalidInputException("Invalid tracking number! Use \"ABC\" followed by 4 digits, e.g. ABC0001.");
        }
        return text;
    }

    public static String validateReceiverName(String value) throws InvalidInputException {
        String text = trimmed(value);
        if (text.isEmpty()) {
            throw new InvalidInputException("Receiver name must not be empty.");
        }
        if (text.length() > MAX_NAME_LENGTH) {
            throw new InvalidInputException("Receiver name must not exceed " + MAX_NAME_LENGTH + " characters.");
        }
        return text;
    }

    /** Weight must be greater than 0 and not above the maximum for the parcel type. */
    public static BigDecimal validateWeight(BigDecimal weight, BigDecimal maxWeight, String typeLabel)
            throws InvalidInputException {
        if (weight == null) {
            throw new InvalidInputException("Weight must not be empty.");
        }
        if (weight.signum() <= 0) {
            throw new InvalidInputException("Weight must be greater than 0 kg.");
        }
        if (weight.compareTo(maxWeight) > 0) {
            throw new InvalidInputException(typeLabel + " weight must not exceed " + maxWeight.toPlainString() + " kg.");
        }
        return weight;
    }

    public static int validatePages(int pages) throws InvalidInputException {
        if (pages < MIN_PAGES || pages > MAX_PAGES) {
            throw new InvalidInputException("Number of pages must be between " + MIN_PAGES + " and " + MAX_PAGES + ".");
        }
        return pages;
    }

    public static int validateDimension(int value, String fieldName) throws InvalidInputException {
        if (value <= 0) {
            throw new InvalidInputException(fieldName + " must be greater than 0 cm.");
        }
        return value;
    }

    public static String validateHandlingNote(String value) throws InvalidInputException {
        String text = trimmed(value);
        if (text.isEmpty()) {
            throw new InvalidInputException("Handling note must not be empty.");
        }
        if (text.length() > MAX_NOTE_LENGTH) {
            throw new InvalidInputException("Handling note must not exceed " + MAX_NOTE_LENGTH + " characters.");
        }
        return text;
    }

    private static String trimmed(String value) {
        return value == null ? "" : value.trim();
    }
}