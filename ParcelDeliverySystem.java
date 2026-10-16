import java.math.BigDecimal;
import java.util.Scanner;

public class ParcelDeliverySystem {

    private static final int EXIT = 5;

    @FunctionalInterface
    private interface Validator<T> {
        void validate(T value) throws InvalidInputException;
    }

    @FunctionalInterface
    private interface Parser<T> {
        T parse(String input) throws Exception;
    }

    private final Scanner scanner;
    private final ParcelManager manager;

    public ParcelDeliverySystem() {
        scanner = new Scanner(System.in);
        manager = ParcelManager.getInstance();
    }

    public void run() {

        int choice = 0;

        while (choice != EXIT) {

            displayMenu();

            choice = readInt("Enter choice: ",
                    v -> Validation.validateRange(v, 1, 5));

            switch (choice) {
                case 1 -> addParcel();
                case 2 -> viewAllParcels();
                case 3 -> changeDeliveryService();
                case 4 -> markAsDelivered();
                case 5 -> System.out.println("Exiting program...");
            }
        }

        scanner.close();
    }

    private void displayMenu() {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("       PARCEL DELIVERY SYSTEM");
        System.out.println("==========================================");
        System.out.println("1. Add Parcel");
        System.out.println("2. View All Parcels");
        System.out.println("3. Change Delivery Service");
        System.out.println("4. Mark as Delivered");
        System.out.println("5. Exit");
        System.out.println("==========================================");
    }

    private void addParcel() {

        System.out.println();
        System.out.println("--- ADD PARCEL ---");

        String trackingNumber = readString(
                "Enter tracking number: ",
                this::validateNewTrackingNumber).toUpperCase();

        String receiverName = readString(
                "Enter receiver name: ",
                v -> Validation.validateNotBlank(v, "Receiver name"));

        System.out.println();
        System.out.println("Select Parcel Type:");
        System.out.println("1. Document");
        System.out.println("2. Box");
        System.out.println("3. Fragile");

        int type = readInt("Enter parcel type: ",
                v -> Validation.validateRange(v, 1, 3));

        Parcel parcel = switch (type) {
            case 1 -> createDocument(trackingNumber, receiverName);
            case 2 -> createBox(trackingNumber, receiverName);
            default -> createFragile(trackingNumber, receiverName);
        };

        parcel.setDeliveryService(
                selectDeliveryService(parcel.getChargeableWeight()));

        manager.addParcel(parcel);

        System.out.printf(
                "Parcel added successfully! Delivery fee: Php %.2f%n",
                calculateFee(parcel));
    }

    private void validateNewTrackingNumber(String trackingNumber)
            throws InvalidInputException {

        Validation.validateTrackingNumber(trackingNumber);

        if (manager.trackingNumberExists(trackingNumber)) {
            throw new InvalidInputException(
                    "Tracking number already exists!");
        }
    }

    private Document createDocument(
            String trackingNumber,
            String receiverName) {

        double weight = readWeight(0.5, "Document");

        int pages = readInt("Enter number of pages: ",
                Validation::validatePages);

        return new Document(trackingNumber, receiverName,
                weight, DeliveryService.STANDARD, pages);
    }

    private Box createBox(
            String trackingNumber,
            String receiverName) {

        double weight = readWeight(50, "Box");

        int length = readDimension("Length");
        int width = readDimension("Width");
        int height = readDimension("Height");

        return new Box(trackingNumber, receiverName, weight,
                DeliveryService.STANDARD, length, width, height);
    }

    private Fragile createFragile(
            String trackingNumber,
            String receiverName) {

        double weight = readWeight(30, "Fragile");

        int length = readDimension("Length");
        int width = readDimension("Width");
        int height = readDimension("Height");

        String handlingNote = readString(
                "Enter handling note: ",
                v -> Validation.validateNotBlank(v, "Handling note"));

        return new Fragile(trackingNumber, receiverName, weight,
                DeliveryService.STANDARD, length, width, height,
                handlingNote);
    }

    private DeliveryService selectDeliveryService(
            double chargeableWeight) {

        System.out.println();
        System.out.println("Select Delivery Service:");
        System.out.println("1. Standard");
        System.out.println("2. Express");
        System.out.println("3. Same Day");

        int choice = readInt("Enter service: ", v -> {
            Validation.validateRange(v, 1, 3);

            if (v == 3) {
                Validation.validateSameDayDelivery(chargeableWeight);
            }
        });

        return DeliveryService.values()[choice - 1];
    }

    private void viewAllParcels() {

        System.out.println();
        System.out.println("--- ALL PARCELS ---");

        if (manager.getParcels().isEmpty()) {
            System.out.println("No parcels available.");
            return;
        }

        System.out.printf(
                "%-10s %-10s %-20s %-12s %-12s %-12s %-12s%n",
                "Tracking", "Type", "Receiver", "Chg. Weight",
                "Service", "Fee", "Status");

        System.out.println("-".repeat(100));

            for (Parcel parcel : manager.getParcels()) {

    System.out.printf(
            "%-10s %-10s %-20s %-12s %-12s Php %-8.2f %-12s%n",
            parcel.getTrackingNumber(),
            parcel.getType(),
            parcel.getReceiverName(),
            formatWeight(parcel.getChargeableWeight()),
            parcel.getDeliveryService(),
            calculateFee(parcel),
            parcel.getStatus());
        }
    }

    private void changeDeliveryService() {

        System.out.println();
        System.out.println("--- CHANGE DELIVERY SERVICE ---");

        Parcel parcel = readPendingParcel();

        parcel.setDeliveryService(
                selectDeliveryService(parcel.getChargeableWeight()));

        System.out.printf(
                "Delivery service changed! New fee: Php %.2f%n",
                calculateFee(parcel));
    }

    private void markAsDelivered() {

        System.out.println();
        System.out.println("--- MARK AS DELIVERED ---");

        Parcel parcel = readPendingParcel();

        parcel.markAsDelivered();

        System.out.println("Parcel delivered successfully!");
    }

    // Keeps asking until the tracking number belongs to an existing,
    // not-yet-delivered parcel.
    private Parcel readPendingParcel() {

        return prompt("Enter tracking number: ", input -> {

            Validation.validateNotBlank(input, "Tracking number");

            Parcel parcel = manager.findParcel(input);

            if (parcel.getStatus() == ParcelStatus.DELIVERED) {
                throw new InvalidInputException(
                        "Parcel already delivered! "
                        + "Enter a different tracking number.");
            }

            return parcel;
        });
    }
    private String formatWeight(double weight) {

    return BigDecimal.valueOf(weight)
            .stripTrailingZeros()
            .toPlainString();
}
    private double calculateFee(Parcel parcel) {

        return parcel.calculateDeliveryFee(
                parcel.getDeliveryService().getStrategy());
    }

    // ---------- Input helpers ----------

    private <T> T prompt(String message, Parser<T> parser) {

        T result = null;

        while (result == null) {

            System.out.print(message);

            String input = scanner.nextLine().trim();

            try {
                result = parser.parse(input);

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        return result;
    }

    private String readString(String message, Validator<String> validator) {

        return prompt(message, input -> {
            validator.validate(input);
            return input;
        });
    }

    private int readInt(String message, Validator<Integer> validator) {

        return prompt(message, input -> {
            int value = Integer.parseInt(input);
            validator.validate(value);
            return value;
        });
    }

    private double readDouble(String message, Validator<Double> validator) {

        return prompt(message, input -> {
            double value = Double.parseDouble(input);
            validator.validate(value);
            return value;
        });
    }

    private double readWeight(double maxWeight, String parcelType) {

        return readDouble("Enter weight (kg): ",
                w -> Validation.validateWeight(w, maxWeight, parcelType));
    }

    private int readDimension(String name) {

        return readInt(
                "Enter " + name.toLowerCase() + " (cm): ",
                v -> Validation.validateDimension(v, name));
    }
}