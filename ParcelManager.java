package parceldelivery.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import parceldelivery.exception.DuplicateTrackingNumberException;
import parceldelivery.exception.ParcelAlreadyDeliveredException;
import parceldelivery.exception.ParcelNotFoundException;
import parceldelivery.exception.ServiceNotAllowedException;
import parceldelivery.model.DeliveryService;
import parceldelivery.model.Parcel;

public final class ParcelManager {

    private static final class Holder {
        private static final ParcelManager INSTANCE = new ParcelManager();
    }

    /** Keyed by tracking number; LinkedHashMap keeps insertion order for display. */
    private final Map<String, Parcel> parcels = new LinkedHashMap<String, Parcel>();

    private ParcelManager() {
    }

    public static ParcelManager getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized boolean exists(String trackingNumber) {
        return parcels.containsKey(normalize(trackingNumber));
    }

    public synchronized void addParcel(Parcel parcel) throws DuplicateTrackingNumberException {
        Objects.requireNonNull(parcel, "parcel must not be null");
        if (parcels.containsKey(parcel.getTrackingNumber())) {
            throw new DuplicateTrackingNumberException(parcel.getTrackingNumber());
        }
        parcels.put(parcel.getTrackingNumber(), parcel);
    }

    public synchronized Parcel findParcel(String trackingNumber) throws ParcelNotFoundException {
        Parcel parcel = parcels.get(normalize(trackingNumber));
        if (parcel == null) {
            throw new ParcelNotFoundException();
        }
        return parcel;
    }

    /**
     * Finds a parcel and makes sure it can still be modified.
     *
     * @throws ParcelNotFoundException         if there is no such parcel
     * @throws ParcelAlreadyDeliveredException if it was already delivered
     */
    public synchronized Parcel requirePendingParcel(String trackingNumber)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException {
        Parcel parcel = findParcel(trackingNumber);
        if (parcel.isDelivered()) {
            throw ParcelAlreadyDeliveredException.forServiceChange();
        }
        return parcel;
    }

    /** Changes the delivery service of a pending parcel; the parcel is left untouched if the change fails. */
    public synchronized Parcel changeDeliveryService(String trackingNumber, DeliveryService service)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException, ServiceNotAllowedException {
        Parcel parcel = findParcel(trackingNumber);
        parcel.changeDeliveryService(service);
        return parcel;
    }

    public synchronized Parcel markAsDelivered(String trackingNumber)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException {
        Parcel parcel = findParcel(trackingNumber);
        parcel.markAsDelivered();
        return parcel;
    }

    /** Read-only snapshot of all parcels in the order they were added. */
    public synchronized List<Parcel> getAllParcels() {
        return Collections.unmodifiableList(new ArrayList<Parcel>(parcels.values()));
    }

    private static String normalize(String trackingNumber) {
        return trackingNumber == null ? "" : trackingNumber.trim();
    }
}