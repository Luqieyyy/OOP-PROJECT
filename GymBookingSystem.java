import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

// Admin class
class Admin {
    private String adminId;
    private String password;

    public Admin(String adminId, String password) {
        this.adminId = adminId;
        this.password = password;
    }

    public boolean login(String inputId, String inputPassword) {
        return adminId.equals(inputId) && password.equals(inputPassword);
    }
}

// User class
class User {
    private String name;
    private String icPass;
    private String phone;

    public User(String name, String icPass, String phone) {
        this.name = name;
        this.icPass = icPass;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getIcPass() {
        return icPass;
    }

    public String getPhone() {
        return phone;
    }
}

// Booking class extending User
class Booking extends User {
    private String date;
    private String time;
    private double amount;
    private String membershipType;
    private String paymentMethod;
    private boolean paymentStatus;

    public Booking(String name, String icPass, String phone, String date, String time, double amount,
                   String membershipType, String paymentMethod, boolean paymentStatus) {
        super(name, icPass, phone);
        this.date = date;
        this.time = time;
        this.amount = amount;
        this.membershipType = membershipType;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getAmount() {
        return amount;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public void setPaymentStatus(boolean status) {
        this.paymentStatus = status;
    }

    public String getDetails() {
        return "User: " + getName() + ", Date: " + date + ", Time: " + time + ", Amount: RM" + amount
                + ", Membership Type: " + membershipType + ", Payment Method: " + paymentMethod
                + ", Payment Status: " + (paymentStatus ? "Yes" : "No");
    }

    public String toFileString() {
        return getName() + "|" + getIcPass() + "|" + getPhone() + "|" + date + "|" + time + "|"
                + amount + "|" + membershipType + "|" + paymentMethod + "|" + paymentStatus;
    }

    public static Booking fromFileString(String fileString) {
        try {
            String[] parts = fileString.split("\\|");
            if (parts.length != 9) {
                throw new IllegalArgumentException("Invalid data format");
            }
            return new Booking(parts[0], parts[1], parts[2], parts[3], parts[4],
                               Double.parseDouble(parts[5]), parts[6], parts[7], Boolean.parseBoolean(parts[8]));
        } catch (Exception e) {
            System.out.println("Error parsing booking data: " + e.getMessage());
            return null;
        }
    }
}

// Booking Manager
class BookingManager {
    private ArrayList<Booking> bookings = new ArrayList<>();
    private static final String FILENAME = "gym_bookings.txt";
    private static final int MAX_CAPACITY_PER_SLOT = 5;

    public BookingManager() {
        loadBookingsFromFile();
    }

    public void addBooking(Booking booking) {
        if (checkCapacity(booking.getDate(), booking.getTime())) {
            bookings.add(booking);
            saveBookingsToFile();
            System.out.println("Booking added successfully!");
        } else {
            System.out.println("Cannot add booking. Time slot is full.");
        }
    }

    public void deleteBooking(int bookingIndex) {
        if (bookingIndex >= 0 && bookingIndex < bookings.size()) {
            bookings.remove(bookingIndex);
            saveBookingsToFile();
            System.out.println("Booking deleted successfully.");
        } else {
            System.out.println("Invalid booking index.");
        }
    }

    public void listBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings available.");
        } else {
            for (int i = 0; i < bookings.size(); i++) {
                System.out.println((i + 1) + ". " + bookings.get(i).getDetails());
            }
        }
    }

    public void updatePaymentStatus(int bookingIndex, boolean status) {
        if (bookingIndex >= 0 && bookingIndex < bookings.size()) {
            bookings.get(bookingIndex).setPaymentStatus(status);
            saveBookingsToFile();
            System.out.println("Payment status updated.");
        } else {
            System.out.println("Invalid booking index.");
        }
    }

    public void rescheduleBooking(int bookingIndex, Scanner scanner) {
        if (bookingIndex >= 0 && bookingIndex < bookings.size()) {
            Booking booking = bookings.get(bookingIndex);
            System.out.println("Current Booking Details:");
            System.out.println(booking.getDetails());

            System.out.print("Enter new date (dd/mm/yyyy): ");
            String newDate = scanner.nextLine();
            System.out.print("Enter new time (e.g., 10:00 AM or 02:30 PM): ");
            String newTime = scanner.nextLine();

            if (!checkCapacity(newDate, newTime)) {
                System.out.println("Cannot reschedule. Time slot is full.");
                return;
            }

            booking.setDate(newDate);
            booking.setTime(newTime);
            saveBookingsToFile();
            System.out.println("Booking rescheduled successfully!");
        } else {
            System.out.println("Invalid booking index.");
        }
    }

    public void searchBookingsByDate(String date) {
        boolean found = false;
        for (Booking booking : bookings) {
            if (booking.getDate().equals(date)) {
                System.out.println(booking.getDetails());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No bookings found for the date " + date);
        }
    }

    private boolean checkCapacity(String date, String time) {
        int count = 0;
        for (Booking booking : bookings) {
            if (booking.getDate().equals(date) && booking.getTime().equals(time)) {
                count++;
            }
        }
        return count < MAX_CAPACITY_PER_SLOT;
    }

    private void saveBookingsToFile() {
        try (FileWriter writer = new FileWriter(FILENAME, false)) {
            for (Booking booking : bookings) {
                writer.write(booking.toFileString() + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    private void loadBookingsFromFile() {
        File file = new File(FILENAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Booking booking = Booking.fromFileString(line);
                if (booking != null) bookings.add(booking);
            }
        } catch (IOException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
    }
}

// Main system
public class GymBookingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Admin admin = new Admin("admin123", "password123");
        BookingManager bookingManager = new BookingManager();

        System.out.println("Welcome to Gym Booking System!\n");
        System.out.print("Enter Admin ID: ");
        String adminId = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (admin.login(adminId, password)) {
            System.out.println("\nLogin successful!");

            while (true) {
                System.out.println("\n1. Add Booking");
                System.out.println("2. List Bookings");
                System.out.println("3. Update Payment Status");
                System.out.println("4. Delete Booking");
                System.out.println("5. Reschedule Booking");
                System.out.println("6. Search Bookings by Date");
                System.out.println("7. Exit");
                System.out.print("\nChoose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();
                System.out.print("\n");

                switch (choice) {
                    case 1 -> addBooking(scanner, bookingManager);
                    case 2 -> bookingManager.listBookings();
                    case 3 -> updatePaymentStatus(scanner, bookingManager);
                    case 4 -> deleteBooking(scanner, bookingManager);
                    case 5 -> rescheduleBooking(scanner, bookingManager);
                    case 6 -> searchBookingsByDate(scanner, bookingManager);
                    case 7 -> {
                        System.out.println("Exiting. Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }
        } else {
            System.out.println("Login failed! Exiting system.");
        }
    }

    private static void addBooking(Scanner scanner, BookingManager bookingManager) {
        System.out.print("Enter user name: ");
        String name = scanner.nextLine();
        System.out.print("Enter IC/passport: ");
        String icPass = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();

        System.out.print("Enter date (dd/mm/yyyy): ");
        String date = scanner.nextLine();
        System.out.print("Enter time (e.g., 10:00 AM or 02:30 PM): ");
        String time = scanner.nextLine();

        System.out.println("Choose membership type:");
        System.out.println("1. Pay Per Day (RM10/day)");
        System.out.println("2. Membership");
        int membershipChoice = scanner.nextInt();
        scanner.nextLine();

        String membershipType;
        double amount = 0;
        if (membershipChoice == 1) {
            membershipType = "Pay Per Day";
            amount = 10;
        } else {
            System.out.println("Choose Membership Plan:");
            System.out.println("1. Monthly (RM100)");
            System.out.println("2. Annually (RM1000)");
            int planChoice = scanner.nextInt();
            scanner.nextLine();

            if (planChoice == 1) {
                membershipType = "Monthly";
                amount = 100;
            } else {
                membershipType = "Annually";
                amount = 1000;
            }
        }

        System.out.println("Choose payment method:");
        System.out.println("1. Cash");
        System.out.println("2. QR");
        System.out.println("3. Transfer");
        int paymentChoice = scanner.nextInt();
        scanner.nextLine();

        String paymentMethod = switch (paymentChoice) {
            case 1 -> "Cash";
            case 2 -> "QR";
            case 3 -> "Transfer";
            default -> "Cash";
        };

        System.out.print("Is the payment completed? (Yes/No): ");
        String paymentStatusInput = scanner.nextLine().trim().toLowerCase();
        boolean paymentStatus = paymentStatusInput.equals("yes");

        Booking booking = new Booking(name, icPass, phone, date, time, amount, membershipType, paymentMethod, paymentStatus);
        bookingManager.addBooking(booking);
    }

    private static void updatePaymentStatus(Scanner scanner, BookingManager bookingManager) {
        bookingManager.listBookings();
        System.out.print("Enter booking number to update payment status: ");
        int bookingIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        System.out.print("Set payment status to (true/false): ");
        boolean status = scanner.nextBoolean();
        scanner.nextLine();

        bookingManager.updatePaymentStatus(bookingIndex, status);
    }

    private static void deleteBooking(Scanner scanner, BookingManager bookingManager) {
        bookingManager.listBookings();
        System.out.print("Enter booking number to delete: ");
        int bookingIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        bookingManager.deleteBooking(bookingIndex);
    }

    private static void rescheduleBooking(Scanner scanner, BookingManager bookingManager) {
        bookingManager.listBookings();
        System.out.print("Enter booking number to reschedule: ");
        int bookingIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        bookingManager.rescheduleBooking(bookingIndex, scanner);
    }

    private static void searchBookingsByDate(Scanner scanner, BookingManager bookingManager) {
        System.out.print("Enter date to search (dd/mm/yyyy): ");
        String date = scanner.nextLine();

        bookingManager.searchBookingsByDate(date);
    }
}
