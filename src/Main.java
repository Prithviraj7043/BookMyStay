import java.util.HashMap;
import java.util.Map;

/**
 * UseCase4RoomSearch
 *
 * Demonstrates read-only room search using centralized inventory.
 * Guests can view available rooms without modifying system state.
 *
 * @author YourName
 * @version 4.1
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("          Book My Stay App");
        System.out.println("            Version 4.1");
        System.out.println("=====================================");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 10);
        inventory.addRoomType("Double Room", 5);
        inventory.addRoomType("Suite Room", 0); // unavailable example

        // Create room domain objects
        Map<String, Room> rooms = new HashMap<>();
        rooms.put("Single Room", new SingleRoom());
        rooms.put("Double Room", new DoubleRoom());
        rooms.put("Suite Room", new SuiteRoom());

        // Perform room search
        RoomSearchService searchService = new RoomSearchService();
        searchService.searchAvailableRooms(inventory, rooms);
    }
}

/**
 * Search service responsible for read-only access to room inventory.
 *
 * @version 4.0
 */
class RoomSearchService {

    public void searchAvailableRooms(RoomInventory inventory, Map<String, Room> rooms) {

        System.out.println("\n--- Available Rooms ---\n");

        for (String roomType : rooms.keySet()) {

            int availability = inventory.getAvailability(roomType);

            // Filter unavailable rooms
            if (availability > 0) {

                Room room = rooms.get(roomType);
                room.displayRoomDetails();

                System.out.println("Available Rooms : " + availability);
                System.out.println("-----------------------------------");
            }
        }
    }
}

/**
 * Centralized inventory manager.
 *
 * @version 4.0
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
}

/**
 * Abstract Room domain model.
 */
abstract class Room {

    private String roomType;
    private int beds;
    private double size;
    private double price;

    public Room(String roomType, int beds, double size, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public void displayRoomDetails() {
        System.out.println("Room Type : " + roomType);
        System.out.println("Beds      : " + beds);
        System.out.println("Size      : " + size + " sq.ft");
        System.out.println("Price     : $" + price + " per night");
    }
}

/**
 * Single Room implementation.
 */
class SingleRoom extends Room {

    public SingleRoom() {
        super("Single Room", 1, 200, 100);
    }
}

/**
 * Double Room implementation.
 */
class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double Room", 2, 350, 180);
    }
}

/**
 * Suite Room implementation.
 */
class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite Room", 3, 600, 350);
    }
}