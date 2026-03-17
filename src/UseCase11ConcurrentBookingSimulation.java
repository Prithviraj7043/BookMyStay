import java.util.*;
import java.util.concurrent.*;

/**
 * UseCase11ConcurrentBookingSimulation
 *
 * Simulates concurrent booking requests with thread-safe allocation
 * to prevent double-booking and maintain consistent inventory.
 *
 * Author: YourName
 * Version: 11.0
 */
public class UseCase11ConcurrentBookingSimulation {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=====================================");
        System.out.println("      Book My Stay App - Concurrent Simulation");
        System.out.println("                 Version 11.0");
        System.out.println("=====================================");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 3);
        inventory.addRoomType("Double Room", 2);

        // Shared booking service
        BookingService bookingService = new BookingService(inventory);

        // Simulated guests submitting requests
        List<Reservation> reservations = Arrays.asList(
                new Reservation("Alice", "Single Room"),
                new Reservation("Bob", "Double Room"),
                new Reservation("Charlie", "Single Room"),
                new Reservation("David", "Double Room"),
                new Reservation("Eve", "Single Room")
        );

        // Thread pool to simulate concurrent booking requests
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Submit booking tasks concurrently
        for (Reservation res : reservations) {
            executor.submit(() -> {
                try {
                    bookingService.processReservationThreadSafe(res);
                } catch (RuntimeException e) {
                    System.out.println("Booking FAILED for " + res.getGuestName() + " | " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Display final inventory
        System.out.println("\n--- Final Inventory After Concurrent Bookings ---");
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
 * Centralized inventory with synchronized access.
 */
class RoomInventory {

    private final Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public synchronized void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public synchronized boolean hasAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public synchronized void decrement(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        if (available <= 0) {
            throw new RuntimeException("No rooms available for " + roomType);
        }
        inventory.put(roomType, available - 1);
    }

    public synchronized void increment(String roomType) {
        int available = inventory.getOrDefault(roomType, 0);
        inventory.put(roomType, available + 1);
    }

    public synchronized void displayInventory() {
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

/**
 * Thread-safe booking service.
 */
class BookingService {

    private RoomInventory inventory;
    private Set<String> allocatedRoomIds = Collections.synchronizedSet(new HashSet<>());

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Thread-safe reservation processing.
     */
    public void processReservationThreadSafe(Reservation res) {
        synchronized (inventory) { // critical section for inventory check + allocation
            String roomType = res.getRoomType();
            if (!inventory.hasAvailability(roomType)) {
                throw new RuntimeException("No available rooms");
            }

            // Generate unique room ID
            String roomId = generateRoomId(roomType);
            res.setRoomId(roomId);
            allocatedRoomIds.add(roomId);

            // Decrement inventory
            inventory.decrement(roomType);

            System.out.println("Reservation CONFIRMED for " + res.getGuestName()
                    + " | Room Type: " + roomType + " | Room ID: " + roomId);
        }
    }

    private String generateRoomId(String roomType) {
        String prefix = roomType.replace(" ", "").substring(0, 2).toUpperCase();
        String roomId;
        Random rand = new Random();
        synchronized (allocatedRoomIds) {
            do {
                int random = rand.nextInt(900) + 100;
                roomId = prefix + random;
            } while (allocatedRoomIds.contains(roomId));
        }
        return roomId;
    }
}