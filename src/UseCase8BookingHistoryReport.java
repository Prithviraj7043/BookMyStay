import java.util.*;

/**
 * UseCase8BookingHistoryReport
 *
 * Demonstrates booking history tracking and reporting.
 * Confirmed reservations are stored chronologically and
 * can be retrieved for operational visibility or reporting.
 *
 * Author: YourName
 * Version: 8.1
 */
public class UseCase8BookingHistoryReport {

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("          Book My Stay App");
        System.out.println("            Version 8.1");
        System.out.println("=====================================");

        // Initialize booking history
        BookingHistory history = new BookingHistory();

        // Sample confirmed reservations
        Reservation res1 = new Reservation("Alice", "Single Room", "SI101");
        Reservation res2 = new Reservation("Bob", "Double Room", "DO202");
        Reservation res3 = new Reservation("Charlie", "Suite Room", "SU303");

        // Add to history
        history.addReservation(res1);
        history.addReservation(res2);
        history.addReservation(res3);

        // Display all reservations
        System.out.println("\n--- All Confirmed Reservations ---\n");
        history.displayAllReservations();

        // Generate a report summary
        BookingReportService reportService = new BookingReportService(history);
        System.out.println("\n--- Reservation Summary Report ---\n");
        reportService.generateReport();
    }
}

/**
 * Reservation domain model with confirmed room ID.
 */
class Reservation {

    private String guestName;
    private String roomType;
    private String roomId;

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

    public void displayReservation() {
        System.out.println("Guest: " + guestName
                + " | Room Type: " + roomType
                + " | Room ID: " + roomId);
    }
}

/**
 * Maintains confirmed reservation history in chronological order.
 */
class BookingHistory {

    private List<Reservation> history;

    public BookingHistory() {
        history = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        history.add(reservation);
        System.out.println("Reservation recorded for " + reservation.getGuestName());
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(history);
    }

    public void displayAllReservations() {
        if (history.isEmpty()) {
            System.out.println("No confirmed reservations.");
            return;
        }
        for (Reservation res : history) {
            res.displayReservation();
        }
    }
}

/**
 * Generates reports from booking history.
 */
class BookingReportService {

    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    /**
     * Generates a simple summary report: counts per room type.
     */
    public void generateReport() {
        List<Reservation> reservations = history.getAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No reservations to report.");
            return;
        }

        Map<String, Integer> summary = new HashMap<>();

        for (Reservation res : reservations) {
            summary.put(res.getRoomType(),
                    summary.getOrDefault(res.getRoomType(), 0) + 1);
        }

        System.out.println("Total Reservations: " + reservations.size());
        System.out.println("Breakdown by Room Type:");
        for (Map.Entry<String, Integer> entry : summary.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}