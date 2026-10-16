public class Box extends Parcel {

    private final int length;
    private final int width;
    private final int height;

    public Box(
            String trackingNumber,
            String receiverName,
            double weight,
            DeliveryService deliveryService,
            int length,
            int width,
            int height) {

        super(
                trackingNumber,
                receiverName,
                weight,
                deliveryService
        );

        this.length = length;
        this.width = width;
        this.height = height;
    }

    public double getVolumetricWeight() {

    return ((double) length * width * height) / 3500.0;
    
    }

    @Override
    public double getChargeableWeight() {

        double chargeableWeight =
                Math.max(
                        getWeight(),
                        getVolumetricWeight()
                );

        return Math.ceil(chargeableWeight);
    }

    @Override
    public double getBaseFee() {

        return 40.00 * getChargeableWeight();
    }

    @Override
    public String getType() {

        return "Box";
    }

    public int getLength() {

        return length;
    }

    public int getWidth() {

        return width;
    }

    public int getHeight() {

        return height;
    }
}