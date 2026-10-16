public enum DeliveryService {

    STANDARD(new StandardDeliveryStrategy()),
    EXPRESS(new ExpressDeliveryStrategy()),
    SAME_DAY(new SameDayDeliveryStrategy());

    private final DeliveryFeeStrategy strategy;

    DeliveryService(DeliveryFeeStrategy strategy) {
        this.strategy = strategy;
    }

    public DeliveryFeeStrategy getStrategy() {
        return strategy;
    }
}