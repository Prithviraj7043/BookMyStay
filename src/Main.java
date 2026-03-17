import java.util.*;

/**
 * UseCase6RoomAllocationService
 *
 * Demonstrates reservation confirmation and safe room allocation.
 * Booking requests are processed from a queue and rooms are assigned
 * uniquely while updating inventory immediately.
 *
 * @author YourName
 * @version 6.1
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("          Book My Stay App");
        System.out.println("            Version 6.1");
        System.out.println("=====================================");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        // Initialize booking queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Single Room"));
        bookingQueue.addRequest(new Reservation("David", "Suite Room"));

        // Initialize booking service
        BookingService bookingService = new BookingService(inventory);

        System.out.println("\n--- Processing Booking Requests ---\n");

        while (!bookingQueue.isEmpty()) {
            Reservation reservation = bookingQueue.getNextRequest();
            bookingService.processReservation(reservation);
        }

        System.out.println("\n--- Final Inventory State ---");
        inventory.displayInventory();
    }
}

/**
 * Represents a guest booking request.
 */
class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

/**
 * FIFO booking request queue.
 */
class BookingRequestQueue {

    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Request received from " + reservation.getGuestName());
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

/**
 * Centralized room inventory.
 */
class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void decrementAvailability(String roomType) {
        int current = inventory.get(roomType);
        inventory.put(roomType, current - 1);
    }

    public void displayInventory() {
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

/**
 * Booking service responsible for room allocation.
 */
class BookingService {

    private RoomInventory inventory;

    // Tracks all allocated room IDs to prevent duplicates
    private Set<String> allocatedRoomIds = new HashSet<>();

    // Maps room types to assigned room IDs
    private Map<String, Set<String>> roomAllocations = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void processReservation(Reservation reservation) {

        String roomType = reservation.getRoomType();

        int available = inventory.getAvailability(roomType);

        if (available <= 0) {
            System.out.println("Reservation FAILED for "
                    + reservation.getGuestName()
                    + " (No rooms available for " + roomType + ")");
            return;
        }

        // Generate unique room ID
        String roomId = generateRoomId(roomType);

        // Record allocation
        allocatedRoomIds.add(roomId);

        roomAllocations
                .computeIfAbsent(roomType, k -> new HashSet<>())
                .add(roomId);

        // Update inventory
        inventory.decrementAvailability(roomType);

        System.out.println("Reservation CONFIRMED for "
                + reservation.getGuestName()
                + " | Room Type: " + roomType
                + " | Room ID: " + roomId);
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
}