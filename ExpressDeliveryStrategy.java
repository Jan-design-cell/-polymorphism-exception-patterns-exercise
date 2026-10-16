package parceldelivery.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import parceldelivery.model.DeliveryService;

public class ExpressDeliveryStrategy implements DeliveryFeeStrategy {

    private static final BigDecimal MULTIPLIER = new BigDecimal("1.5");

    @Override
    public DeliveryService getService() {
        return DeliveryService.EXPRESS;
    }

    @Override
    public BigDecimal calculateFee(BigDecimal baseFee) {
        return baseFee.multiply(MULTIPLIER).setScale(2, RoundingMode.HALF_UP);
    }
}