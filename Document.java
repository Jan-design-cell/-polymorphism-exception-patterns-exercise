package parceldelivery.model;

import java.math.BigDecimal;

import parceldelivery.exception.InvalidInputException;
import parceldelivery.util.Validator;

public class Document extends Parcel {

    private static final BigDecimal FLAT_FEE = new BigDecimal("50");

    private final int numberOfPages;

    public Document(String trackingNumber, String receiverName, BigDecimal weight, int numberOfPages)
            throws InvalidInputException {
        super(trackingNumber, receiverName, weight, ParcelType.DOCUMENT);
        this.numberOfPages = Validator.validatePages(numberOfPages);
    }

    @Override
    public BigDecimal getChargeableWeight() {
        return getWeight();
    }

    @Override
    public BigDecimal getBaseFee() {
        return FLAT_FEE;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }
}