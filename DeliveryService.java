package parceldelivery.model;

public enum DeliveryService {
    STANDARD("Standard"),
    EXPRESS("Express"),
    SAME_DAY("Same Day");

    private final String displayName;

    DeliveryService(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}