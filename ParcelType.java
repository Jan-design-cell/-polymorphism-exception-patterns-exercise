package parceldelivery.model;

import java.math.BigDecimal;

public enum ParcelType {
    DOCUMENT("Document", new BigDecimal("0.5")),
    BOX("Box", new BigDecimal("50")),
    FRAGILE("Fragile", new BigDecimal("30"));

    private final String label;
    private final BigDecimal maxWeight;

    ParcelType(String label, BigDecimal maxWeight) {
        this.label = label;
        this.maxWeight = maxWeight;
    }

    public String getLabel() {
        return label;
    }

    /** Maximum actual weight in kg. */
    public BigDecimal getMaxWeight() {
        return maxWeight;
    }

    @Override
    public String toString() {
        return label;
    }
}