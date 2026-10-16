public class StandardDeliveryStrategy
        implements DeliveryFeeStrategy {

    @Override
    public double calculateFee(
            double baseFee,
            double chargeableWeight) {

        return baseFee;
    }
}