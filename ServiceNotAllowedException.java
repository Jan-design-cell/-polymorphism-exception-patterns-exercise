package parceldelivery.exception;

/** Thrown when a delivery service cannot be used for a parcel (e.g. Same Day over 10 kg). */
public class ServiceNotAllowedException extends ParcelException {

    private static final long serialVersionUID = 1L;

    public ServiceNotAllowedException(String message) {
        super(message);
    }
}