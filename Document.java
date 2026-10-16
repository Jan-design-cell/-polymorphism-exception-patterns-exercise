public class Document extends Parcel {

    private final int numberOfPages;

    public Document(
            String trackingNumber,
            String receiverName,
            double weight,
            DeliveryService deliveryService,
            int numberOfPages) {

        super(
                trackingNumber,
                receiverName,
                weight,
                deliveryService
        );

        this.numberOfPages = numberOfPages;
    }

    @Override
    public double getChargeableWeight() {

        return getWeight();
    }

    @Override
    public double getBaseFee() {

        return 50.00;
    }

    @Override
    public String getType() {

        return "Document";
    }

    public int getNumberOfPages() {

        return numberOfPages;
    }
}