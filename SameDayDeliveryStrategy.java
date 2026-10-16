public class SameDayDeliveryStrategy
        implements DeliveryFeeStrategy {

    @Override
    public double calculateFee(
            double baseFee,
            double chargeableWeight) {

        if (chargeableWeight > 10) {

            throw new IllegalArgumentException(
                    "Same Day delivery is only for parcels up to 10 kg!"
            );
        }

        return (baseFee * 2) + 100.00;
    }
}