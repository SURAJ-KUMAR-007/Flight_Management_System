import service.impl.*;
import java.util.Scanner;

public class AirlineApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserServiceImpl userService = new UserServiceImpl();
        ViewServiceImpl viewService = new ViewServiceImpl();
        BookingServiceImpl bookingService = new BookingServiceImpl();
        CancelServiceImpl cancelService = new CancelServiceImpl();

        System.out.println("=== Welcome to Flight Management System ===");
        int currentUser = -1;

        while (true) {
            if (currentUser == -1) {
                System.out.println("\n1. Register\n2. Login\n3. Exit");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    userService.register(scanner);
                } else if (choice == 2) {
                    currentUser = userService.login(scanner);
                } else if (choice == 3) {
                    break;
                }
            } else {
                System.out.println("\n1. View Flights");
                System.out.println("2. Book Ticket");
                System.out.println("3. Cancel Ticket");
                System.out.println("4. Check Flight Status");
                System.out.println("5. Logout");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> viewService.viewFlights(); // now includes status column
                    case 2 -> bookingService.bookTicket(scanner, currentUser);
                    case 3 -> cancelService.cancelBooking(scanner, currentUser);
                    case 4 -> viewService.checkFlightStatus(scanner); // ✅ new option
                    case 5 -> currentUser = -1;
                    default -> System.out.println("Invalid choice, try again.");
                }
            }
        }
        System.out.println("Goodbye!");
    }
}
