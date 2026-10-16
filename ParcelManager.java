import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParcelManager {

    private static ParcelManager instance;

    private final List<Parcel> parcels;

    private ParcelManager() {
        parcels = new ArrayList<>();
    }

    public static ParcelManager getInstance() {

        if (instance == null) {
            instance = new ParcelManager();
        }

        return instance;
    }

    public void addParcel(Parcel parcel) {
        parcels.add(parcel);
    }

    public List<Parcel> getParcels() {
        return Collections.unmodifiableList(parcels);
    }

    public boolean trackingNumberExists(String trackingNumber) {

        return parcels.stream()
                .anyMatch(p -> p.getTrackingNumber()
                        .equalsIgnoreCase(trackingNumber));
    }

    public Parcel findParcel(String trackingNumber)
            throws ParcelNotFoundException {

        return parcels.stream()
                .filter(p -> p.getTrackingNumber()
                        .equalsIgnoreCase(trackingNumber))
                .findFirst()
                .orElseThrow(() ->
                        new ParcelNotFoundException("Parcel not found!"));
    }
}