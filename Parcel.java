public abstract class Parcel {

    private final String trackingNumber;
    private final String receiverName;
    private final double weight;

    private DeliveryService deliveryService;
    private ParcelStatus status;

    public Parcel(
            String trackingNumber,
            String receiverName,
            double weight,
            DeliveryService deliveryService) {

        this.trackingNumber = trackingNumber;
        this.receiverName = receiverName;
        this.weight = weight;
        this.deliveryService = deliveryService;
        this.status = ParcelStatus.PENDING;
    }

    public abstract double getChargeableWeight();

    public abstract double getBaseFee();

    public abstract String getType();

    public double calculateDeliveryFee(
            DeliveryFeeStrategy strategy) {

        return strategy.calculateFee(
                getBaseFee(),
                getChargeableWeight()
        );
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public double getWeight() {
        return weight;
    }

    public DeliveryService getDeliveryService() {
        return deliveryService;
    }

    public void setDeliveryService(
            DeliveryService deliveryService) {

        this.deliveryService = deliveryService;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public void markAsDelivered() {
        status = ParcelStatus.DELIVERED;
    }
}