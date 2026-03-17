import java.io.*;
import java.util.*;

/**
 * UseCase12DataPersistenceRecovery
 *
 * Demonstrates persistence of inventory and booking state to a file,
 * and recovery of state during application startup.
 *
 * Author: YourName
 * Version: 12.0
 */
public class UseCase12DataPersistenceRecovery {

    private static final String DATA_FILE = "hotel_state.ser";

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("       Book My Stay App - Persistence");
        System.out.println("                 Version 12.0");
        System.out.println("=====================================");

        // Attempt to load persisted state
        HotelSystem system = HotelSystem.restoreState(DATA_FILE);

        if (system == null) {
            // If no previous state exists, initialize fresh system
            System.out.println("No previous system state found. Initializing new system...");
            system = new HotelSystem();
            system.getInventory().addRoomType("Single Room", 2);
            system.getInventory().addRoomType("Double Room", 1);
        } else {
            System.out.println("System state successfully restored from " + DATA_FILE);
        }

        // Simulate new bookings
        BookingService bookingService = new BookingService(system.getInventory());
        Reservation r1 = bookingService.processReservation(new Reservation("Alice", "Single Room"));
        Reservation r2 = bookingService.processReservation(new Reservation("Bob", "Double Room"));

        system.addReservation(r1);
        system.addReservation(r2);

        System.out.println("\n--- Current Inventory ---");
        system.getInventory().displayInventory();

        // Persist system state
        system.saveState(DATA_FILE);
        System.out.println("\nSystem state saved successfully to " + DATA_FILE);

        System.out.println("\n--- All Reservations ---");
        for (Reservation r : system.getReservations()) {
            System.out.println(r.getGuestName() + " | " + r.getRoomType() + " | " + r.getRoomId());
        }
    }
}

/**
 * Serializable Reservation class.
 */
class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }
}

/**
 * Serializable inventory class.
 */
class RoomInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public boolean hasAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrement(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        if (available <= 0) throw new RuntimeException("No rooms available for " + roomType);
        inventory.put(roomType, available - 1);
    }

    public void increment(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
    }

    public void displayInventory() {
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

/**
 * Booking service (non-persistent logic).
 */
class BookingService {

    private RoomInventory inventory;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public Reservation processReservation(Reservation res) {
        if (!inventory.hasAvailability(res.getRoomType())) {
            throw new RuntimeException("No available rooms for " + res.getRoomType());
        }
        inventory.decrement(res.getRoomType());
        System.out.println("Reservation CONFIRMED for " + res.getGuestName()
                + " | Room Type: " + res.getRoomType()
                + " | Room ID: " + res.getRoomId());
        return res;
    }
}

/**
 * Serializable system state containing inventory and reservations.
 */
class HotelSystem implements Serializable {

    private static final long serialVersionUID = 1L;

    private RoomInventory inventory;
    private List<Reservation> reservations;

    public HotelSystem() {
        inventory = new RoomInventory();
        reservations = new ArrayList<>();
    }

    public RoomInventory getInventory() {
        return inventory;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void addReservation(Reservation r) {
        reservations.add(r);
    }

    /**
     * Save state to file
     */
    public void saveState(String filePath) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Failed to save system state: " + e.getMessage());
        }
    }

    /**
     * Restore state from file
     */
    public static HotelSystem restoreState(String filePath) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (HotelSystem) in.readObject();
        } catch (FileNotFoundException e) {
            return null; // no previous state exists
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to restore system state: " + e.getMessage());
            return null;
        }
    }
}