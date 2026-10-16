package parceldelivery.strategy;

import java.math.BigDecimal;

import parceldelivery.model.DeliveryService;

public interface DeliveryFeeStrategy {

    DeliveryService getService();

    /** Delivery fee in Php for the given base fee, rounded to 2 decimal places. */
    BigDecimal calculateFee(BigDecimal baseFee);

    /** Whether this service may be used for a parcel with this chargeable weight (kg). */
    default boolean isAllowedFor(BigDecimal chargeableWeight) {
        return true;
    }

    /** Message shown when {@link #isAllowedFor(BigDecimal)} is false. */
    default String getRestrictionMessage() {
        return "";
    }
}