package parceldelivery.exception;

public class InputClosedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InputClosedException() {
        super("Input stream closed.");
    }
}