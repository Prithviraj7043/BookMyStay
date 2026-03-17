/**
 * UseCase1HotelBookingApp
 *
 * This class represents the entry point of the Book My Stay application.
 * It demonstrates how a Java program starts execution and prints
 * a welcome message to the console.
 *
 * The purpose of this use case is to establish a predictable
 * application startup behavior.
 *
 * @author YourName
 * @version 1.0
 */
public class Main{

    /**
     * The main method is the entry point of the Java application.
     * The JVM invokes this method to start program execution.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {

        // Application Name
        String appName = "Book My Stay App";

        // Application Version
        String version = "Version 1.0";

        // Print Welcome Message
        System.out.println("=================================");
        System.out.println("       Welcome to " + appName);
        System.out.println("           " + version);
        System.out.println("=================================");

        // Informational message
        System.out.println("Hotel Booking Management System Initialized.");
        System.out.println("Application started successfully.");

        // Application termination message
        System.out.println("Thank you for using " + appName + ".");
    }
}