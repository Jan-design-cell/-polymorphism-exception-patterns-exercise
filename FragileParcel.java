package parceldelivery.model;

import java.math.BigDecimal;

import parceldelivery.exception.InvalidInputException;
import parceldelivery.util.Validator;

public class FragileParcel extends Box {

    private static final BigDecimal HANDLING_FEE = new BigDecimal("100");

    private final String handlingNote;

    public FragileParcel(String trackingNumber, String receiverName, BigDecimal weight,
                         int length, int width, int height, String handlingNote)
            throws InvalidInputException {
        super(trackingNumber, receiverName, weight, length, width, height, ParcelType.FRAGILE);
        this.handlingNote = Validator.validateHandlingNote(handlingNote);
    }

    /** Box base fee plus the Php 100 handling fee. */
    @Override
    public BigDecimal getBaseFee() {
        return super.getBaseFee().add(HANDLING_FEE);
    }

    public String getHandlingNote() {
        return handlingNote;
    }
}