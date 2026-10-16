import java.util.regex.Pattern;

public final class Validation {

    private static final Pattern TRACKING_PATTERN =
            Pattern.compile("ABC\\d{4}");

    private Validation() {
        // Utility class - no objects allowed.
    }

    public static void validateNotBlank(String value, String fieldName)
            throws InvalidInputException {

        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(
                    fieldName + " cannot be empty!");
        }
    }

    public static void validateTrackingNumber(String trackingNumber)
            throws InvalidInputException {

        validateNotBlank(trackingNumber, "Tracking number");

        if (!TRACKING_PATTERN.matcher(
                trackingNumber.trim().toUpperCase()).matches()) {

            throw new InvalidInputException(
                    "Invalid tracking number! "
                    + "Format must be ABC followed by 4 digits.");
        }
    }

    public static void validateRange(int value, int min, int max)
            throws InvalidInputException {

        if (value < min || value > max) {
            throw new InvalidInputException(
                    "Please enter a value between "
                    + min + " and " + max + "!");
        }
    }

    public static void validateWeight(
            double weight,
            double maxWeight,
            String parcelType)
            throws InvalidInputException {

        if (Double.isNaN(weight)
                || Double.isInfinite(weight)
                || weight <= 0) {

            throw new InvalidInputException(
                    "Weight must be greater than 0!");
        }

        if (weight > maxWeight) {
            throw new InvalidInputException(String.format(
                    "%s maximum weight is %.1f kg!",
                    parcelType, maxWeight));
        }
    }

    public static void validatePages(int pages)
            throws InvalidInputException {

        validateRange(pages, 1, 100);
    }

    public static void validateDimension(int dimension, String name)
            throws InvalidInputException {

        if (dimension <= 0) {
            throw new InvalidInputException(
                    name + " must be greater than 0!");
        }
    }

    public static void validateSameDayDelivery(double chargeableWeight)
            throws InvalidInputException {

        if (chargeableWeight > 10) {
            throw new InvalidInputException(
                    "Same Day delivery is only for parcels up to 10 kg!");
        }
    }
}