import java.util.*;

/**
 * UseCase9ErrorHandlingValidation
 *
 * Demonstrates structured validation, custom exceptions, and
 * fail-fast error handling for booking inputs and inventory management.
 *
 * Author: YourName
 * Version: 9.1
 */
public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("          Book My Stay App");
        System.out.println("            Version 9.1");
        System.out.println("=====================================");

        // Initialize inventory with valid room types
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);

        // Initialize booking queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Add valid and invalid requests
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Suite Room")); // Invalid
        bookingQueue.addRequest(new Reservation("David", "Single Room"));
        bookingQueue.addRequest(new Reservation("Eve", "Single Room")); // Exceeds inventory

        // Initialize booking service with validation
        BookingService bookingService = new BookingService(inventory);

        System.out.println("\n--- Processing Booking Requests ---\n");

        while (!bookingQueue.isEmpty()) {
            Reservation res = bookingQueue.getNextRequest();
            try {
                bookingService.processReservation(res);
            } catch (InvalidBookingException e) {
                System.out.println("Booking FAILED for " + res.getGuestName()
                        + ": " + e.getMessage());
            }
        }

        System.out.println("\n--- Final Inventory State ---");
        inventory.displayInventory();
    }
}

/**
 * Custom exception for invalid bookings.
 */
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * Reservation domain model.
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
 * FIFO booking queue.
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
 * Centralized inventory with validation.
 */
class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public int getAvailability(String roomType) throws InvalidBookingException {
        if (!inventory.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }
        return inventory.get(roomType);
    }

    public void decrementAvailability(String roomType) throws InvalidBookingException {
        int available = getAvailability(roomType);
        if (available <= 0) {
            throw new InvalidBookingException("No rooms available for " + roomType);
        }
        inventory.put(roomType, available - 1);
    }

    public void displayInventory() {
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

/**
 * Booking service with validation and error handling.
 */
class BookingService {

    private RoomInventory inventory;
    private Set<String> allocatedRoomIds = new HashSet<>();
    private Map<String, Set<String>> roomAllocations = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void processReservation(Reservation res) throws InvalidBookingException {

        String roomType = res.getRoomType();

        // Validate availability
        int available = inventory.getAvailability(roomType);
        if (available <= 0) {
            throw new InvalidBookingException("Room not available: " + roomType);
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
                + res.getGuestName()
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