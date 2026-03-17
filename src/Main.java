/**
 * UseCase2RoomInitialization
 *
 * Demonstrates object modeling using abstraction,
 * inheritance, encapsulation, and polymorphism
 * before introducing data structures.
 *
 * @author YourName
 * @version 2.1
 */

public class Main {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("          Book My Stay App");
        System.out.println("            Version 2.1");
        System.out.println("=====================================");

        // Creating room objects (Polymorphism)
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // Static availability variables
        int singleRoomAvailability = 10;
        int doubleRoomAvailability = 7;
        int suiteRoomAvailability = 3;

        System.out.println("\n--- Room Details & Availability ---\n");

        singleRoom.displayRoomDetails();
        System.out.println("Available Rooms : " + singleRoomAvailability);
        System.out.println("-----------------------------------");

        doubleRoom.displayRoomDetails();
        System.out.println("Available Rooms : " + doubleRoomAvailability);
        System.out.println("-----------------------------------");

        suiteRoom.displayRoomDetails();
        System.out.println("Available Rooms : " + suiteRoomAvailability);
        System.out.println("-----------------------------------");

        System.out.println("\nApplication execution completed.");
    }
}

/**
 * Abstract class representing a generic Room.
 * Defines common attributes shared by all room types.
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

    public String getRoomType() {
        return roomType;
    }

    public int getBeds() {
        return beds;
    }

    public double getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    public void displayRoomDetails() {
        System.out.println("Room Type : " + roomType);
        System.out.println("Beds      : " + beds);
        System.out.println("Size      : " + size + " sq.ft");
        System.out.println("Price     : $" + price + " per night");
    }
}

/**
 * Represents a Single Room type.
 */
class SingleRoom extends Room {

    public SingleRoom() {
        super("Single Room", 1, 200, 100);
    }
}

/**
 * Represents a Double Room type.
 */
class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double Room", 2, 350, 180);
    }
}

/**
 * Represents a Suite Room type.
 */
class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite Room", 3, 600, 350);
    }
}