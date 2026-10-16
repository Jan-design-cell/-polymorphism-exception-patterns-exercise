package parceldelivery.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import parceldelivery.model.DeliveryService;

/** Standard: delivery fee = base fee. */
public class StandardDeliveryStrategy implements DeliveryFeeStrategy {

    @Override
    public DeliveryService getService() {
        return DeliveryService.STANDARD;
    }

    @Override
    public BigDecimal calculateFee(BigDecimal baseFee) {
        return baseFee.setScale(2, RoundingMode.HALF_UP);
    }
}