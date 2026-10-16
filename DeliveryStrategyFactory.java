package parceldelivery.strategy;

import java.util.EnumMap;
import java.util.Map;

import parceldelivery.model.DeliveryService;

public final class DeliveryStrategyFactory {

    private static final Map<DeliveryService, DeliveryFeeStrategy> STRATEGIES =
            new EnumMap<DeliveryService, DeliveryFeeStrategy>(DeliveryService.class);

    static {
        register(new StandardDeliveryStrategy());
        register(new ExpressDeliveryStrategy());
        register(new SameDayDeliveryStrategy());
    }

    private DeliveryStrategyFactory() {
    }

    private static void register(DeliveryFeeStrategy strategy) {
        STRATEGIES.put(strategy.getService(), strategy);
    }

    public static DeliveryFeeStrategy getStrategy(DeliveryService service) {
        DeliveryFeeStrategy strategy = STRATEGIES.get(service);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy registered for " + service);
        }
        return strategy;
    }
}