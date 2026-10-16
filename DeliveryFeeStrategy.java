public interface DeliveryFeeStrategy {

    double calculateFee(
            double baseFee,
            double chargeableWeight
    );
}