public class ExpressDeliveryStrategy
        implements DeliveryFeeStrategy {

    @Override
    public double calculateFee(
            double baseFee,
            double chargeableWeight) {

        return baseFee * 1.5;
    }
}