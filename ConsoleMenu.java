package parceldelivery.ui;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import parceldelivery.exception.DuplicateTrackingNumberException;
import parceldelivery.exception.ParcelException;
import parceldelivery.exception.ServiceNotAllowedException;
import parceldelivery.model.Box;
import parceldelivery.model.DeliveryService;
import parceldelivery.model.Document;
import parceldelivery.model.FragileParcel;
import parceldelivery.model.Parcel;
import parceldelivery.model.ParcelType;
import parceldelivery.service.ParcelManager;
import parceldelivery.util.Validator;

/** The text menu. Talks to the user and delegates all the real work to ParcelManager and the model. */
public class ConsoleMenu {

    private final ConsoleInput input;
    private final PrintStream out;
    private final ParcelManager manager = ParcelManager.getInstance();

    public ConsoleMenu(ConsoleInput input, PrintStream out) {
        this.input = input;
        this.out = out;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = input.ask("Enter choice: ", raw -> Validator.parseChoice(raw, 1, 5));
            try {
                switch (choice) {
                    case 1:
                        addParcel();
                        break;
                    case 2:
                        viewAllParcels();
                        break;
                    case 3:
                        changeDeliveryService();
                        break;
                    case 4:
                        markAsDelivered();
                        break;
                    case 5:
                        running = false;
                        break;
                    default:
                        break;
                }
            } catch (ParcelException e) {
                // Not found, already delivered, etc. The exception carries the message to show.
                out.println(e.getMessage());
            }
        }
        out.println("Thank you for using the Parcel Delivery System. Goodbye!");
    }

    private void printMenu() {
        out.println();
        out.println("===== PARCEL DELIVERY SYSTEM =====");
        out.println("1. Add Parcel");
        out.println("2. View All Parcels");
        out.println("3. Change Delivery Service");
        out.println("4. Mark as Delivered");
        out.println("5. Exit");
    }

    // ------------------------------------------------------------------
    // 1. Add Parcel
    // ------------------------------------------------------------------

    private void addParcel() throws ParcelException {
        out.println();
        out.println("--- Add Parcel ---");
        final ParcelType type = chooseParcelType();

        String trackingNumber = input.ask("Tracking number (ABC + 4 digits, e.g. ABC0001): ", raw -> {
            String value = Validator.validateTrackingNumber(raw);
            if (manager.exists(value)) {
                throw new DuplicateTrackingNumberException(value);
            }
            return value;
        });
        String receiver = input.ask("Receiver name: ", Validator::validateReceiverName);
        BigDecimal weight = input.ask(
                "Weight in kg (max " + type.getMaxWeight().toPlainString() + "): ",
                raw -> Validator.validateWeight(Validator.parseWeight(raw), type.getMaxWeight(), type.getLabel()));

        Parcel parcel;
        switch (type) {
            case DOCUMENT: {
                int pages = input.ask("Number of pages (" + Validator.MIN_PAGES + "-" + Validator.MAX_PAGES + "): ",
                        raw -> Validator.validatePages(Validator.parseInteger(raw, "Number of pages")));
                parcel = new Document(trackingNumber, receiver, weight, pages);
                break;
            }
            case BOX: {
                int length = askDimension("Length");
                int width = askDimension("Width");
                int height = askDimension("Height");
                parcel = new Box(trackingNumber, receiver, weight, length, width, height);
                break;
            }
            case FRAGILE: {
                int length = askDimension("Length");
                int width = askDimension("Width");
                int height = askDimension("Height");
                String note = input.ask("Handling note: ", Validator::validateHandlingNote);
                parcel = new FragileParcel(trackingNumber, receiver, weight, length, width, height, note);
                break;
            }
            default:
                throw new IllegalStateException("Unhandled parcel type: " + type);
        }

        // Keep asking until the chosen service is allowed for this parcel.
        while (true) {
            DeliveryService service = chooseDeliveryService();
            try {
                parcel.changeDeliveryService(service);
                break;
            } catch (ServiceNotAllowedException e) {
                out.println(e.getMessage());
            }
        }

        manager.addParcel(parcel);
        out.println("Parcel added successfully! Delivery fee: Php " + peso(parcel.getDeliveryFee()));
    }

    private int askDimension(String name) {
        return input.ask(name + " in cm: ",
                raw -> Validator.validateDimension(Validator.parseInteger(raw, name), name));
    }

    private ParcelType chooseParcelType() {
        out.println("Select parcel type:");
        out.println("  1. Document");
        out.println("  2. Box");
        out.println("  3. Fragile");
        int choice = input.ask("Choice: ", raw -> Validator.parseChoice(raw, 1, 3));
        return ParcelType.values()[choice - 1];
    }

    private DeliveryService chooseDeliveryService() {
        final DeliveryService[] services = DeliveryService.values();
        out.println("Select delivery service:");
        for (int i = 0; i < services.length; i++) {
            out.println("  " + (i + 1) + ". " + services[i].getDisplayName());
        }
        int choice = input.ask("Choice: ", raw -> Validator.parseChoice(raw, 1, services.length));
        return services[choice - 1];
    }

    // ------------------------------------------------------------------
    // 2. View All Parcels
    // ------------------------------------------------------------------

    private void viewAllParcels() {
        out.println();
        out.println("--- All Parcels ---");
        List<Parcel> parcels = manager.getAllParcels();
        if (parcels.isEmpty()) {
            out.println("No parcels found.");
            return;
        }

        String[] headers = {"Tracking No.", "Type", "Receiver", "Chargeable (kg)", "Service", "Fee (Php)", "Status"};
        boolean[] rightAligned = {false, false, false, true, false, true, false};

        List<String[]> rows = new ArrayList<String[]>();
        for (Parcel p : parcels) {
            rows.add(new String[] {
                    p.getTrackingNumber(),
                    p.getType().getLabel(),
                    p.getReceiverName(),
                    p.getChargeableWeight().stripTrailingZeros().toPlainString(),
                    p.getDeliveryService().getDisplayName(),
                    p.getDeliveryFee().toPlainString(),
                    p.getStatus().getDisplayName()
            });
        }

        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                widths[i] = Math.max(widths[i], row[i].length());
            }
        }

        String border = buildBorder(widths);
        out.println(border);
        out.println(formatRow(headers, widths, rightAligned));
        out.println(border);
        for (String[] row : rows) {
            out.println(formatRow(row, widths, rightAligned));
        }
        out.println(border);
        out.println("Total parcels: " + parcels.size());
    }

    private static String buildBorder(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int width : widths) {
            for (int i = 0; i < width + 2; i++) {
                sb.append('-');
            }
            sb.append('+');
        }
        return sb.toString();
    }

    private static String formatRow(String[] cells, int[] widths, boolean[] rightAligned) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < cells.length; i++) {
            String format = rightAligned[i] ? " %" + widths[i] + "s |" : " %-" + widths[i] + "s |";
            sb.append(String.format(format, cells[i]));
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // 3. Change Delivery Service
    // ------------------------------------------------------------------

    private void changeDeliveryService() throws ParcelException {
        out.println();
        out.println("--- Change Delivery Service ---");
        String trackingNumber = input.readLine("Enter tracking number: ").trim();

        // Throws "Parcel not found!" or "Parcel already delivered!" before asking for a service.
        manager.requirePendingParcel(trackingNumber);

        DeliveryService service = chooseDeliveryService();
        try {
            Parcel parcel = manager.changeDeliveryService(trackingNumber, service);
            out.println("Delivery service changed! New fee: Php " + peso(parcel.getDeliveryFee()));
        } catch (ServiceNotAllowedException e) {
            // The parcel keeps its old service.
            out.println(e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // 4. Mark as Delivered
    // ------------------------------------------------------------------

    private void markAsDelivered() throws ParcelException {
        out.println();
        out.println("--- Mark as Delivered ---");
        String trackingNumber = input.readLine("Enter tracking number: ").trim();
        manager.markAsDelivered(trackingNumber);
        out.println("Parcel delivered successfully!");
    }

    private static String peso(BigDecimal amount) {
        return amount.toPlainString();
    }
}