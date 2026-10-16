package parceldelivery.model;

import java.math.BigDecimal;
import java.util.Objects;

import parceldelivery.exception.InvalidInputException;
import parceldelivery.exception.ParcelAlreadyDeliveredException;
import parceldelivery.exception.ServiceNotAllowedException;
import parceldelivery.strategy.DeliveryFeeStrategy;
import parceldelivery.strategy.DeliveryStrategyFactory;
import parceldelivery.util.Validator;

/**
 * Base class of every parcel. Holds the details common to all parcel types
 * and declares the two calculations that each concrete type answers
 * differently (POLYMORPHISM): {@link #getChargeableWeight()} and
 * {@link #getBaseFee()}.
 *
 * The delivery fee is delegated to a {@link DeliveryFeeStrategy}
 * (STRATEGY pattern), which can be swapped when the service changes.
 */
public abstract class Parcel {

    private final String trackingNumber;
    private final String receiverName;
    private final BigDecimal weight;
    private final ParcelType type;

    private DeliveryService deliveryService;
    private DeliveryFeeStrategy feeStrategy;
    private ParcelStatus status;

    /**
     * Validates the common fields. A new parcel is Pending and starts on the
     * Standard service, which is allowed for every parcel; use
     * {@link #changeDeliveryService(DeliveryService)} to pick another one.
     */
    protected Parcel(String trackingNumber, String receiverName, BigDecimal weight, ParcelType type)
            throws InvalidInputException {
        if (type == null) {
            throw new InvalidInputException("Parcel type must not be null.");
        }
        this.type = type;
        this.trackingNumber = Validator.validateTrackingNumber(trackingNumber);
        this.receiverName = Validator.validateReceiverName(receiverName);
        this.weight = Validator.validateWeight(weight, type.getMaxWeight(), type.getLabel());

        this.deliveryService = DeliveryService.STANDARD;
        this.feeStrategy = DeliveryStrategyFactory.getStrategy(DeliveryService.STANDARD);
        this.status = ParcelStatus.PENDING;
    }

    // ----- Polymorphic behaviour: each parcel type implements these differently -----

    /** Weight in kg that the fee is based on. */
    public abstract BigDecimal getChargeableWeight();

    /** Fee in Php before the delivery service is applied. */
    public abstract BigDecimal getBaseFee();

    // ----- Delivery fee (delegated to the current strategy) -----

    /** Final delivery fee in Php, rounded to 2 decimal places. */
    public final BigDecimal getDeliveryFee() {
        return feeStrategy.calculateFee(getBaseFee());
    }

    /**
     * Switches the delivery service.
     *
     * @throws ParcelAlreadyDeliveredException if the parcel was already delivered
     * @throws ServiceNotAllowedException       if the service is not allowed for this parcel;
     *                                          the parcel then keeps its old service
     */
    public final void changeDeliveryService(DeliveryService newService)
            throws ParcelAlreadyDeliveredException, ServiceNotAllowedException {
        Objects.requireNonNull(newService, "newService must not be null");
        if (status == ParcelStatus.DELIVERED) {
            throw ParcelAlreadyDeliveredException.forServiceChange();
        }
        DeliveryFeeStrategy candidate = DeliveryStrategyFactory.getStrategy(newService);
        if (!candidate.isAllowedFor(getChargeableWeight())) {
            throw new ServiceNotAllowedException(candidate.getRestrictionMessage());
        }
        this.deliveryService = newService;
        this.feeStrategy = candidate;
    }

    /** Marks a pending parcel as delivered. */
    public final void markAsDelivered() throws ParcelAlreadyDeliveredException {
        if (status == ParcelStatus.DELIVERED) {
            throw ParcelAlreadyDeliveredException.forDelivery();
        }
        status = ParcelStatus.DELIVERED;
    }

    // ----- Getters -----

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getReceiverName() {
        return receiverName;
    }

    /** Actual weight in kg. */
    public BigDecimal getWeight() {
        return weight;
    }

    public ParcelType getType() {
        return type;
    }

    public DeliveryService getDeliveryService() {
        return deliveryService;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public boolean isDelivered() {
        return status == ParcelStatus.DELIVERED;
    }

    @Override
    public String toString() {
        return type.getLabel() + "[" + trackingNumber + ", " + receiverName + ", "
                + deliveryService + ", " + status + "]";
    }
}