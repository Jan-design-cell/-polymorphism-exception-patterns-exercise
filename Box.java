package parceldelivery.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import parceldelivery.exception.InvalidInputException;
import parceldelivery.util.Validator;


public class Box extends Parcel {

    private static final BigInteger VOLUMETRIC_DIVISOR = BigInteger.valueOf(3500);
    private static final BigDecimal RATE_PER_KG = new BigDecimal("40");

    private final int length;
    private final int width;
    private final int height;

    public Box(String trackingNumber, String receiverName, BigDecimal weight,
               int length, int width, int height) throws InvalidInputException {
        this(trackingNumber, receiverName, weight, length, width, height, ParcelType.BOX);
    }

    /** Used by subclasses (e.g. {@link FragileParcel}) that follow the Box rules with another type. */
    protected Box(String trackingNumber, String receiverName, BigDecimal weight,
                  int length, int width, int height, ParcelType type) throws InvalidInputException {
        super(trackingNumber, receiverName, weight, type);
        this.length = Validator.validateDimension(length, "Length");
        this.width = Validator.validateDimension(width, "Width");
        this.height = Validator.validateDimension(height, "Height");
    }

    /** Volume in cubic cm. BigInteger so that very large dimensions cannot overflow. */
    private BigInteger getVolume() {
        return BigInteger.valueOf(length)
                .multiply(BigInteger.valueOf(width))
                .multiply(BigInteger.valueOf(height));
    }

    /** Volumetric weight in kg, rounded to 2 decimals for display only. */
    public BigDecimal getVolumetricWeight() {
        return new BigDecimal(getVolume()).divide(new BigDecimal(VOLUMETRIC_DIVISOR), 2, RoundingMode.HALF_UP);
    }

    /**
     * The bigger of actual and volumetric weight, rounded up to a whole kg.
     * Computed exactly (no floating point): ceil(max(a, v)) == max(ceil(a), ceil(v)).
     */
    @Override
    public BigDecimal getChargeableWeight() {
        BigInteger volumetricCeil = getVolume()
                .add(VOLUMETRIC_DIVISOR.subtract(BigInteger.ONE))
                .divide(VOLUMETRIC_DIVISOR);
        BigDecimal actualCeil = getWeight().setScale(0, RoundingMode.CEILING);
        return actualCeil.max(new BigDecimal(volumetricCeil));
    }

    @Override
    public BigDecimal getBaseFee() {
        return RATE_PER_KG.multiply(getChargeableWeight());
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}