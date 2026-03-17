import java.util.*;

/**
 * UseCase10BookingCancellation
 *
 * Demonstrates safe cancellation of confirmed bookings, inventory rollback,
 * and LIFO tracking of released room IDs using a Stack.
 *
 * Author: YourName
 * Version: 10.2
 */
public class UseCase10BookingCancellation {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("          Book My Stay App");
        System.out.println("            Version 10.2");
        System.out.println("=====================================");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);

        // Initialize booking service
        BookingService bookingService = new BookingService(inventory);

        // Confirm some bookings
        Reservation res1 = bookingService.processReservation(new Reservation("Alice", "Single Room"));
        Reservation res2 = bookingService.processReservation(new Reservation("Bob", "Double Room"));
        Reservation res3 = bookingService.processReservation(new Reservation("Charlie", "Single Room"));

        // Display current inventory
        System.out.println("\n--- Inventory After Bookings ---");
        inventory.displayInventory();

        // Initialize cancellation service
        CancellationService cancellationService = new CancellationService(inventory, bookingService);

        System.out.println("\n--- Performing Cancellations ---");

        // Cancel a valid booking
        cancellationService.cancelReservation(res2.getRoomId());

        // Attempt to cancel the same reservation again (should fail)
        cancellationService.cancelReservation(res2.getRoomId());

        // Cancel another booking
        cancellationService.cancelReservation(res3.getRoomId());

        // Display final inventory
        System.out.println("\n--- Inventory After Cancellations ---");
        inventory.displayInventory();
    }
}

/**
 * Reservation domain model with confirmed room ID.
 */
class Reservation {

    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public Reservation(String guestName, String roomType, String roomId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
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

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}

/**
 * Centralized inventory with room counts.
 */
class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public void decrement(String roomType) {
        inventory.put(roomType, getAvailability(roomType) - 1);
    }

    public void increment(String roomType) {
        inventory.put(roomType, getAvailability(roomType) + 1);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void displayInventory() {
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

/**
 * Booking service: confirms reservations and tracks allocations.
 */
class BookingService {

    private RoomInventory inventory;
    private Set<String> allocatedRoomIds = new HashSet<>();
    private Map<String, Reservation> reservationsByRoomId = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Processes a reservation and returns the confirmed reservation with room ID.
     */
    public Reservation processReservation(Reservation res) {

        String roomType = res.getRoomType();
        if (inventory.getAvailability(roomType) <= 0) {
            throw new RuntimeException("Cannot process reservation: No available rooms for " + roomType);
        }

        String roomId = generateRoomId(roomType);
        res.setRoomId(roomId);

        allocatedRoomIds.add(roomId);
        reservationsByRoomId.put(roomId, res);

        // Update inventory
        inventory.decrement(roomType);

        System.out.println("Reservation CONFIRMED for " + res.getGuestName()
                + " | Room Type: " + roomType + " | Room ID: " + roomId);

        return res;
    }

    private String generateRoomId(String roomType) {
        String prefix = roomType.replace(" ", "").substring(0, 2).toUpperCase();
        String roomId;
        do {
            int random = new Random().nextInt(900) + 100;
            roomId = prefix + random;
        } while (allocatedRoomIds.contains(roomId));
        return roomId;
    }

    public Reservation getReservation(String roomId) {
        return reservationsByRoomId.get(roomId);
    }

    public void removeReservation(String roomId) {
        reservationsByRoomId.remove(roomId);
        allocatedRoomIds.remove(roomId);
    }
}

/**
 * Handles booking cancellations safely with inventory rollback.
 */
class CancellationService {

    private RoomInventory inventory;
    private BookingService bookingService;
    private Stack<String> releasedRoomIds;

    public CancellationService(RoomInventory inventory, BookingService bookingService) {
        this.inventory = inventory;
        this.bookingService = bookingService;
        this.releasedRoomIds = new Stack<>();
    }

    public void cancelReservation(String roomId) {
        Reservation res = bookingService.getReservation(roomId);

        if (res == null) {
            System.out.println("Cancellation FAILED: Room ID " + roomId + " not found or already cancelled.");
            return;
        }

        // Rollback inventory
        inventory.increment(res.getRoomType());

        // Track released room
        releasedRoomIds.push(roomId);

        // Remove reservation
        bookingService.removeReservation(roomId);

        System.out.println("Cancellation SUCCESS for " + res.getGuestName()
                + " | Room ID: " + roomId);
    }
}