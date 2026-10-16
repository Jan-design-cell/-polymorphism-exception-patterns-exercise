package parceldelivery.exception;


public class ParcelAlreadyDeliveredException extends ParcelException {

    private static final long serialVersionUID = 1L;

    private ParcelAlreadyDeliveredException(String message) {
        super(message);
    }

    /** Used when trying to change the delivery service of a delivered parcel. */
    public static ParcelAlreadyDeliveredException forServiceChange() {
        return new ParcelAlreadyDeliveredException("Parcel already delivered!");
    }

    /** Used when trying to mark a delivered parcel as delivered again. */
    public static ParcelAlreadyDeliveredException forDelivery() {
        return new ParcelAlreadyDeliveredException("Parcel was already delivered.");
    }
}