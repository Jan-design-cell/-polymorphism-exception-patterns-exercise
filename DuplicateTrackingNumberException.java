package parceldelivery.exception;

public class DuplicateTrackingNumberException extends ParcelException {

    private static final long serialVersionUID = 1L;

    public DuplicateTrackingNumberException(String trackingNumber) {
        super("Duplicate tracking number! " + trackingNumber + " already exists.");
    }
}