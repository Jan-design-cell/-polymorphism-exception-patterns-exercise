package parceldelivery.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import parceldelivery.model.DeliveryService;

public class SameDayDeliveryStrategy implements DeliveryFeeStrategy {

    private static final BigDecimal MULTIPLIER = new BigDecimal("2");
    private static final BigDecimal SURCHARGE = new BigDecimal("100");
    private static final BigDecimal MAX_CHARGEABLE_WEIGHT = new BigDecimal("10");

    @Override
    public DeliveryService getService() {
        return DeliveryService.SAME_DAY;
    }

    @Override
    public BigDecimal calculateFee(BigDecimal baseFee) {
        return baseFee.multiply(MULTIPLIER).add(SURCHARGE).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean isAllowedFor(BigDecimal chargeableWeight) {
        return chargeableWeight.compareTo(MAX_CHARGEABLE_WEIGHT) <= 0;
    }

    @Override
    public String getRestrictionMessage() {
        return "Same Day delivery is only for parcels up to 10 kg!";
    }
}