import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

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

    public String getPaymentMethod() {
        return paymentMethod;
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

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public void setPaymentStatus(boolean status) {
        this.paymentStatus = status;
    }

    public String getDetails() {
	    return String.format(
	        "+-------------------+--------------------------+\n" +
	        "| Field             | Value                    |\n" +
	        "+-------------------+--------------------------+\n" +
	        "| Name              | %-24s |\n" +
	        "| IC/Passport       | %-24s |\n" +
	        "| Phone             | %-24s |\n" +
	        "| Date              | %-24s |\n" +
	        "| Time              | %-24s |\n" +
	        "| Amount            | RM%-22.2f |\n" +
	        "| Membership Type   | %-24s |\n" +
	        "| Payment Method    | %-24s |\n" +
	        "| Payment Status    | %-24s |\n" +
	        "+-------------------+--------------------------+",
	        getName(),
	        getIcPass(),
	        getPhone(),
	        getDate(),
	        getTime(),
	        getAmount(),
	        getMembershipType(),
	        getPaymentMethod(),
	        getPaymentStatus() ? "Paid" : "Not Paid"
	    );
	}


    public String toFileString() {
        return getName() + "|" + getIcPass() + "|" + getPhone() + "|" + date + "|" + time + "|"
                + amount + "|" + membershipType + "|" + paymentMethod + "|" + (paymentStatus ? "yes" : "no");
    }
    

    public static Booking fromFileString(String fileString) {
        try {
            String[] parts = fileString.split("\\|");
            if (parts.length != 9) {
                throw new IllegalArgumentException("Invalid data format");
            }
            double amount = Double.parseDouble(parts[5]); // Parse amount
            boolean paymentStatus = parts[8].trim().equalsIgnoreCase("yes"); // Parse payment status
            return new Booking(parts[0], parts[1], parts[2], parts[3], parts[4], amount, parts[6], parts[7], paymentStatus);
        } catch (Exception e) {
            System.out.println("Error parsing booking data: " + e.getMessage());
            return null;
        }
    }
    
}

class BookingManager {
    private ArrayList<Booking> bookings = new ArrayList<>();
    private static final String FILENAME = "gym_bookings.txt";
    private static final int MAX_CAPACITY_PER_SLOT = 10;

    public BookingManager() {
        loadBookingsFromFile();
    }

    public void addBooking(Booking booking) {
        if (checkCapacity(booking.getDate(), booking.getTime())) {
            bookings.add(booking);
            saveBookingsToFile();
            System.out.println("\nBooking added successfully!\n");
        } else {
            System.out.println("Cannot add booking. Time slot is full.");
        }
    }

    public void deleteBooking(int bookingIndex) {
        if (bookingIndex >= 0 && bookingIndex < bookings.size()) {
            bookings.remove(bookingIndex);
            saveBookingsToFile();
            System.out.println("\nBooking deleted successfully.\n");
        } else {
            System.out.println("\nInvalid booking index.\n");
        }
    }

    public void listBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings available.\n");
            return;
        }
    
        // Print table header
        System.out.printf("%-5s %-20s %-15s %-15s %-12s %-10s %-12s %-15s %-15s %-8s\n",
                "No.", "Name", "IC/Passport", "Phone", "Date", "Time", "Amount (RM)", "Membership", "Payment Method", "Paid?");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
    
        // Print each booking in the table
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            System.out.printf("%-5d %-20s %-15s %-15s %-12s %-10s %-12.2f %-15s %-15s %-8s\n",
                    i + 1,
                    b.getName(),
                    b.getIcPass(),
                    b.getPhone(),
                    b.getDate(),
                    b.getTime(),
                    b.getAmount(),
                    b.getMembershipType(),
                    b.getPaymentMethod(),
                    b.getPaymentStatus() ? "Yes" : "No");
        }
        System.out.println("\n");
    }
    

    public void updatePaymentStatus(int bookingIndex, boolean status) {
        if (bookingIndex >= 0 && bookingIndex < bookings.size()) {
            bookings.get(bookingIndex).setPaymentStatus(status);
            saveBookingsToFile();
            System.out.println("\nPayment status updated.\n");
        } else {
            System.out.println("\nInvalid booking index.\n");
        }
    }

    public void rescheduleBooking(int bookingIndex, Scanner scanner) {
        if (bookingIndex >= 0 && bookingIndex < bookings.size()) {
            Booking booking = bookings.get(bookingIndex);
            System.out.println("\nCurrent Booking Details:\n");
            System.out.println(booking.getDetails());

            System.out.print("\nEnter new date (dd/mm/yyyy): ");
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
            System.out.println("\nBooking rescheduled successfully!\n");
        } else {
            System.out.println("\nInvalid booking index.\n");
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

        // Welcome section
		System.out.println("+-------------------------------------------+");
		System.out.println("|                                           |");
		System.out.println("|          Welcome to Gym Booking           |");
		System.out.println("|              Management System            |");
		System.out.println("|                                           |");
        System.out.println("+-------------------------------------------+\n");
		System.out.print("Enter Admin ID: ");
		String adminId = scanner.nextLine();
		System.out.print("Enter Password: ");
		String password = scanner.nextLine();

		// Login check
		if (admin.login(adminId, password)) {
		    System.out.println("\n+-------------------------------+");
		    System.out.println("|         Login successful!     |");
		    System.out.println("+-------------------------------+");

		    while (true) {
		        // Main menu
		        System.out.println("+-------------------------------------------+");
				System.out.println("|                Main Menu                  |");
				System.out.println("+-------------------------------------------+");
				System.out.println("| 1. Add Booking                            |");
				System.out.println("| 2. List Bookings                          |");
				System.out.println("| 3. Update Payment Status                  |");
				System.out.println("| 4. Delete Booking                         |");
				System.out.println("| 5. Reschedule Booking                     |");
				System.out.println("| 6. Search Bookings by Date                |");
				System.out.println("| 7. Exit                                   |");
        		System.out.println("+-------------------------------------------+");
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
		                System.out.println("\n+-------------------------------+");
		                System.out.println("|         Exiting. Goodbye!     |");
		                System.out.println("+-------------------------------+\n");
		                return;
		            }
		            default -> System.out.println("Invalid choice. Try again.");
		        }
		    }
		} else {
		    // Login failed section
			System.out.println("\n+-------------------------------+");
			System.out.println("|         Login failed!         |");
			System.out.println("+-------------------------------+");
			System.out.println("Try again? Press Enter to retry or any other key to exit.");

			String retryInput = scanner.nextLine();

			while (true) {
			    if (retryInput.equals("")) {
			        // Ask for Admin ID and Password again
			        System.out.print("Enter Admin ID: ");
			        adminId = scanner.nextLine();
			        System.out.print("Enter Password: ");
			        password = scanner.nextLine();

			        if (admin.login(adminId, password)) {
			            System.out.println("\n+-------------------------------+");
			            System.out.println("|         Login successful!     |");
			            System.out.println("+-------------------------------+");

			            while (true) {
								        // Main menu
								        System.out.println("+-------------------------------------------+");
										System.out.println("|                Main Menu                  |");
										System.out.println("+-------------------------------------------+");
										System.out.println("| 1. Add Booking                            |");
										System.out.println("| 2. List Bookings                          |");
										System.out.println("| 3. Update Payment Status                  |");
										System.out.println("| 4. Delete Booking                         |");
										System.out.println("| 5. Reschedule Booking                     |");
										System.out.println("| 6. Search Bookings by Date                |");
										System.out.println("| 7. Exit                                   |");
						        		System.out.println("+-------------------------------------------+");
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
								                System.out.println("\n+-------------------------------+");
								                System.out.println("|         Exiting. Goodbye!     |");
								                System.out.println("+-------------------------------+\n");
								                return;
								            }
								            default -> System.out.println("Invalid choice. Try again.");
								        }
		    						}
			        } else {
			            System.out.println("\n+-------------------------------+");
			            System.out.println("|         Login failed!         |");
			            System.out.println("+-------------------------------+");
			            System.out.println("Try again? Press Enter to retry or any other key to exit.");
			            retryInput = scanner.nextLine();  // Get input again to decide whether to retry or exit
			        }
			    } else {
			        System.out.println("Exiting system.\n");
			        return; // Exit the system if user chooses to exit
			    }
			}
		}
    }

    private static void addBooking(Scanner scanner, BookingManager bookingManager) {
        System.out.print("Enter customer's name: ");
        String name = scanner.nextLine();
        System.out.print("Enter IC/passport: ");
        String icPass = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();

        System.out.print("Enter date (dd/mm/yyyy): ");
        String date = scanner.nextLine();
        System.out.print("Enter time (e.g., 10:00 AM or 02:30 PM): ");
        String time = scanner.nextLine();

        System.out.println("\nMembership type:");
		System.out.println("1. Pay Per Day (RM10/day)");
		System.out.println("2. Membership");

		int membershipChoice;
		String membershipType;
		double amount = 0;

		while (true) {
		    System.out.print("\nChoose membership type: ");
		    membershipChoice = scanner.nextInt();
		    scanner.nextLine();

		    if (membershipChoice == 1) {
		        membershipType = "Pay Per Day";
		        amount = 10;
		        break;
		    } else if (membershipChoice == 2) {
		        System.out.println("\nMembership Plan:");
		        System.out.println("1. Monthly (RM100)");
		        System.out.println("2. Annually (RM1000)");
		        int planChoice;
		        while (true) {
		            System.out.print("\nChoose membership plan: ");
		            planChoice = scanner.nextInt();
		            scanner.nextLine();

		            if (planChoice == 1) {
		                membershipType = "Monthly";
		                amount = 100;
		                break;
		            } else if (planChoice == 2) {
		                membershipType = "Annually";
		                amount = 1000;
		                break;
		            } else {
		                System.out.println("Invalid option. Please choose 1 for Monthly or 2 for Annually.");
		            }
		        }
		        break;
		    } else {
		        System.out.println("Invalid option. Please choose 1 for Pay Per Day or 2 for Membership.");
		    }
		}

		System.out.println("\nPayment method:");
		System.out.println("1. Cash");
		System.out.println("2. QR");
		System.out.println("3. Transfer");

		String paymentMethod;
		while (true) {
		    System.out.print("\nChoose payment method: ");
		    int paymentChoice = scanner.nextInt();
		    scanner.nextLine();

		    if (paymentChoice == 1) {
		        paymentMethod = "Cash";
		        break;
		    } else if (paymentChoice == 2) {
		        paymentMethod = "QR";
		        break;
		    } else if (paymentChoice == 3) {
		        paymentMethod = "Transfer";
		        break;
		    } else {
		        System.out.println("Invalid option. Please choose 1 for Cash, 2 for QR, or 3 for Transfer.");
		    }
		}

        String paymentInput;
		boolean paymentStatus;
		while (true) {
		    System.out.print("\nPayment made? (y/n): ");
		    paymentInput = scanner.nextLine().trim().toLowerCase();

		    if (paymentInput.equals("y")) {
		        paymentStatus = true;
		        break;
		    } else if (paymentInput.equals("n")) {
		        paymentStatus = false;
		        break;
		    } else {
		        System.out.println("\nInvalid option. Please enter 'y' for Yes or 'n' for No.");
		    }
		}

        Booking booking = new Booking(name, icPass, phone, date, time, amount, membershipType, paymentMethod, paymentStatus);
        bookingManager.addBooking(booking);
    }

    private static void updatePaymentStatus(Scanner scanner, BookingManager bookingManager) {
        System.out.print("Enter booking number to update payment status: ");
        int bookingIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        String paymentInput;
		boolean status;
		while (true) {
		    System.out.print("Payment made? (y/n): ");
		    paymentInput = scanner.nextLine().trim().toLowerCase();

		    if (paymentInput.equals("y")) {
		        status = true;
		        break;
		    } else if (paymentInput.equals("n")) {
		        status = false;
		        break;
		    } else {
		        System.out.println("\nInvalid option. Please enter 'y' for Yes or 'n' for No.");
		    }
		}
		bookingManager.updatePaymentStatus(bookingIndex, status);

    }

    private static void deleteBooking(Scanner scanner, BookingManager bookingManager) {
        System.out.print("Enter booking number to delete: ");
        int bookingIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        bookingManager.deleteBooking(bookingIndex);
    }

    private static void rescheduleBooking(Scanner scanner, BookingManager bookingManager) {
        System.out.print("Enter booking number to reschedule: ");
        int bookingIndex = scanner.nextInt() - 1;
        scanner.nextLine();

        bookingManager.rescheduleBooking(bookingIndex, scanner);
    }

    private static void searchBookingsByDate(Scanner scanner, BookingManager bookingManager) {
        System.out.print("Enter date to search (dd/mm/yyyy): ");
        String date = scanner.nextLine();
        System.out.print("\n");

        bookingManager.searchBookingsByDate(date);
    }
}