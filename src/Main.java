import java.util.*;

/**
 * UseCase7AddOnServiceSelection
 *
 * Demonstrates how optional services can be attached to
 * an existing reservation without modifying booking or
 * inventory logic.
 *
 * @author YourName
 * @version 7.1
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("          Book My Stay App");
        System.out.println("            Version 7.1");
        System.out.println("=====================================");

        // Example reservation IDs (already confirmed in previous use case)
        String reservation1 = "RES101";
        String reservation2 = "RES102";

        // Create Add-On Service Manager
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Create services
        AddOnService breakfast = new AddOnService("Breakfast", 20);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 40);
        AddOnService spa = new AddOnService("Spa Access", 60);

        // Guest selects services
        serviceManager.addService(reservation1, breakfast);
        serviceManager.addService(reservation1, spa);

        serviceManager.addService(reservation2, breakfast);
        serviceManager.addService(reservation2, airportPickup);

        // Display services for reservations
        System.out.println("\n--- Reservation Add-On Services ---\n");

        serviceManager.displayServices(reservation1);
        serviceManager.displayServices(reservation2);
    }
}

/**
 * Represents an optional add-on service.
 */
class AddOnService {

    private String serviceName;
    private double price;

    public AddOnService(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getPrice() {
        return price;
    }
}

/**
 * Manages services attached to reservations.
 *
 * Map<String, List<AddOnService>>
 * ReservationID -> List of Services
 */
class AddOnServiceManager {

    private Map<String, List<AddOnService>> reservationServices;

    public AddOnServiceManager() {
        reservationServices = new HashMap<>();
    }

    /**
     * Adds a service to a reservation.
     */
    public void addService(String reservationId, AddOnService service) {

        reservationServices
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);

        System.out.println("Service added: "
                + service.getServiceName()
                + " for Reservation " + reservationId);
    }

    /**
     * Displays services and total additional cost.
     */
    public void displayServices(String reservationId) {

        List<AddOnService> services = reservationServices.get(reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("Reservation " + reservationId + " has no add-on services.");
            return;
        }

        double totalCost = 0;

        System.out.println("Reservation ID: " + reservationId);

        for (AddOnService service : services) {

            System.out.println("Service: "
                    + service.getServiceName()
                    + " | Cost: $" + service.getPrice());

            totalCost += service.getPrice();
        }

        System.out.println("Total Add-On Cost: $" + totalCost);
        System.out.println("-----------------------------------");
    }
}