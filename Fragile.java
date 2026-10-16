public class Fragile extends Box {

    private final String handlingNote;

    public Fragile(
            String trackingNumber,
            String receiverName,
            double weight,
            DeliveryService deliveryService,
            int length,
            int width,
            int height,
            String handlingNote) {

        super(
                trackingNumber,
                receiverName,
                weight,
                deliveryService,
                length,
                width,
                height
        );

        this.handlingNote = handlingNote;
    }

    @Override
    public double getBaseFee() {

        return super.getBaseFee() + 100.00;
    }

    @Override
    public String getType() {

        return "Fragile";
    }

    public String getHandlingNote() {

        return handlingNote;
    }
}