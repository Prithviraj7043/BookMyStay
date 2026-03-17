import java.util.HashMap;
import java.util.Map;

/**
 * UseCase3InventorySetup
 *
 * Demonstrates centralized room inventory management using HashMap.
 * Room characteristics remain part of the Room domain model while
 * availability is managed by the RoomInventory component.
 *
 * @author YourName
 * @version 3.1
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("          Book My Stay App");
        System.out.println("            Version 3.1");
        System.out.println("=====================================");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Register room types with initial availability
        inventory.addRoomType("Single Room", 10);
        inventory.addRoomType("Double Room", 7);
        inventory.addRoomType("Suite Room", 3);

        // Display current inventory
        System.out.println("\n--- Current Room Inventory ---");
        inventory.displayInventory();

        // Example update
        System.out.println("\nUpdating availability after a booking...");
        inventory.updateAvailability("Single Room", 9);

        // Display updated inventory
        System.out.println("\n--- Updated Room Inventory ---");
        inventory.displayInventory();
    }
}

/**
 * Manages room availability across the system.
 * Acts as the centralized inventory component.
 *
 * @version 3.0
 */
class RoomInventory {

    private Map<String, Integer> inventory;

    /**
     * Constructor initializes the inventory structure.
     */
    public RoomInventory() {
        inventory = new HashMap<>();
    }

    /**
     * Registers a new room type with its availability.
     */
    public void addRoomType(String roomType, int count) {
        inventory.put(roomType, count);
    }

    /**
     * Retrieves availability for a specific room type.
     */
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    /**
     * Updates availability for a room type.
     */
    public void updateAvailability(String roomType, int newCount) {
        if (inventory.containsKey(roomType)) {
            inventory.put(roomType, newCount);
        } else {
            System.out.println("Room type not found: " + roomType);
        }
    }

    /**
     * Displays the entire inventory state.
     */
    public void displayInventory() {
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println("Room Type: " + entry.getKey() +
                    " | Available Rooms: " + entry.getValue());
        }
    }
}